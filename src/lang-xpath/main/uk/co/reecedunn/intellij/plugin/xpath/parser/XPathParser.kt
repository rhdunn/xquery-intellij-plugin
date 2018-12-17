/*
 * Copyright (C) 2018 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

/**
 * A unified XPath parser for different XPath versions and dialects.
 */
@Suppress("PropertyName")
open class XPathParser : PsiParser {
    // region XPath/XQuery Element Types
    //
    // These element types have different PSI implementations in XPath and XQuery.

    open val BRACED_URI_LITERAL: IElementType = XPathElementType.BRACED_URI_LITERAL
    open val ENCLOSED_EXPR: IElementType = XPathElementType.ENCLOSED_EXPR
    open val EXPR: IElementType = XPathElementType.EXPR
    open val FUNCTION_BODY: IElementType = XPathElementType.FUNCTION_BODY
    open val FUNCTION_TEST: IElementType = XPathElementType.FUNCTION_TEST
    open val NCNAME: IElementType = XPathElementType.NCNAME
    open val QNAME: IElementType = XPathElementType.QNAME
    open val STRING_LITERAL: IElementType = XPathElementType.STRING_LITERAL
    open val URI_QUALIFIED_NAME: IElementType = XPathElementType.URI_QUALIFIED_NAME

    // endregion
    // region PsiParser

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        parse(builder)
        rootMarker.done(root)
        return builder.treeBuilt
    }

    // endregion
    // region Main Interface

    fun parse(builder: PsiBuilder) {
        var matched = false
        var haveError = false
        while (builder.tokenType != null) {
            if (matched && !haveError) {
                builder.error(XPathBundle.message("parser.error.expected-eof"))
                haveError = true
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (parse(builder, !matched && !haveError)) {
                matched = true
                continue
            }

            if (haveError) {
                builder.advanceLexer()
            } else if (builder.tokenType != null) {
                val errorMarker = builder.mark()
                builder.advanceLexer()
                errorMarker.error(XPathBundle.message("parser.error.unexpected-token"))
                haveError = true
            }
        }
    }

    open fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean {
        if (parseExpr(builder, null)) {
            return true
        }
        if (isFirst) {
            builder.error(XPathBundle.message("parser.error.expected-expression"))
        }
        return false
    }

    // endregion
    // region Grammar :: Expr

    fun parseExpr(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseExprSingle(builder)) {
            var haveErrors = false

            parseWhiteSpaceAndCommentTokens(builder)
            while (builder.matchTokenType(XPathTokenType.COMMA)) {
                parseWhiteSpaceAndCommentTokens(builder)
                if (!parseExprSingle(builder) && !haveErrors) {
                    builder.error(XPathBundle.message("parser.error.expected-expression"))
                    haveErrors = true
                }
                parseWhiteSpaceAndCommentTokens(builder)
            }

            if (type == null)
                marker.drop()
            else
                marker.done(type)
            return true
        }
        marker.drop()
        return false
    }

    open fun parseExprSingle(builder: PsiBuilder): Boolean {
        return parseOrExpr(builder)
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private fun parseOrExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseStepExpr(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            marker.done(XPathElementType.OR_EXPR)
            return true
        }
        marker.drop()
        return false
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: StepExpr

    private fun parseStepExpr(builder: PsiBuilder): Boolean {
        return parsePostfixExpr(builder) || parseNodeTest(builder, null)
    }

    fun parseNodeTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseKindTest(builder) || parseNameTest(builder, type)) {
            marker.done(XPathElementType.NODE_TEST)
            return true
        }
        marker.drop()
        return false
    }

    fun parseNameTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseEQNameOrWildcard(builder, XPathElementType.WILDCARD, type === XPathElementType.MAP_CONSTRUCTOR_ENTRY)) {
            marker.done(XPathElementType.NAME_TEST)
            return true
        }
        marker.drop()
        return false
    }

    private fun parsePostfixExpr(builder: PsiBuilder): Boolean {
        return parsePrimaryExpr(builder)
    }

    // endregion
    // region Grammar :: Expr :: OrExpr :: PrimaryExpr

    private fun parsePrimaryExpr(builder: PsiBuilder): Boolean {
        return parseLiteral(builder)
    }

    fun parseLiteral(builder: PsiBuilder): Boolean {
        return parseNumericLiteral(builder) || parseStringLiteral(builder)
    }

    fun parseNumericLiteral(builder: PsiBuilder): Boolean {
        if (
            builder.matchTokenType(XPathTokenType.INTEGER_LITERAL) ||
            builder.matchTokenType(XPathTokenType.DOUBLE_LITERAL)
        ) {
            return true
        } else if (builder.matchTokenType(XPathTokenType.DECIMAL_LITERAL)) {
            builder.errorOnTokenType(
                XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT,
                XPathBundle.message("parser.error.incomplete-double-exponent")
            )
            return true
        }
        return false
    }

    // endregion
    // region Grammar :: TypeDeclaration :: KindTest

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    open fun parseKindTest(builder: PsiBuilder): Boolean {
        return (
            parseAnyKindTest(builder) ||
            parseTextTest(builder)
        )
    }

    open fun parseAnyKindTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_NODE)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ANY_KIND_TEST)
            return true
        }
        return false
    }

    open fun parseTextTest(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.K_TEXT)
        if (marker != null) {
            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_OPEN)) {
                marker.rollbackTo()
                return false
            }

            parseWhiteSpaceAndCommentTokens(builder)
            if (!builder.matchTokenType(XPathTokenType.PARENTHESIS_CLOSE)) {
                builder.error(XPathBundle.message("parser.error.expected", ")"))
            }

            marker.done(XPathElementType.ANY_TEXT_TEST)
            return true
        }
        return false
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols

    fun parseStringLiteral(builder: PsiBuilder): Boolean {
        return parseStringLiteral(builder, STRING_LITERAL)
    }

    open fun parseStringLiteral(builder: PsiBuilder, type: IElementType): Boolean {
        val stringMarker = builder.matchTokenTypeWithMarker(XPathTokenType.STRING_LITERAL_START)
        while (stringMarker != null) {
            if (
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) ||
                builder.matchTokenType(XPathTokenType.ESCAPED_CHARACTER)
            ) {
                //
            } else if (builder.matchTokenType(XPathTokenType.STRING_LITERAL_END)) {
                stringMarker.done(type)
                return true
            } else {
                stringMarker.done(type)
                builder.error(XPathBundle.message("parser.error.incomplete-string"))
                return true
            }
        }
        return false
    }

    fun parseComment(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.COMMENT_START_TAG) {
            val commentMarker = builder.mark()
            builder.advanceLexer()
            // NOTE: XQueryTokenType.COMMENT is omitted by the PsiBuilder.
            if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
                builder.advanceLexer()
                commentMarker.done(XPathElementType.COMMENT)
            } else {
                builder.advanceLexer() // XQueryTokenType.UNEXPECTED_END_OF_BLOCK
                commentMarker.done(XPathElementType.COMMENT)
                builder.error(XPathBundle.message("parser.error.incomplete-comment"))
            }
            return true
        } else if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
            val errorMarker = builder.mark()
            builder.advanceLexer()
            errorMarker.error(XPathBundle.message("parser.error.end-of-comment-without-start", "(:"))
            return true
        }
        return false
    }

    open fun parseWhiteSpaceAndCommentTokens(builder: PsiBuilder): Boolean {
        var skipped = false
        while (true) {
            if (builder.tokenType === XPathTokenType.WHITE_SPACE) {
                skipped = true
                builder.advanceLexer()
            } else if (parseComment(builder)) {
                skipped = true
            } else {
                return skipped
            }
        }
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: EQName

    fun parseEQNameOrWildcard(builder: PsiBuilder, type: IElementType, endQNameOnSpace: Boolean): Boolean {
        val marker = builder.mark()
        if (
            parseQNameOrWildcard(builder, type, endQNameOnSpace) ||
            parseURIQualifiedNameOrWildcard(builder, type)
        ) {
            if (type === QNAME || type === NCNAME || type === XPathElementType.WILDCARD) {
                marker.drop()
            } else {
                marker.done(type)
            }
            return true
        }
        marker.drop()
        return false
    }

    fun parseURIQualifiedNameOrWildcard(builder: PsiBuilder, type: IElementType): Boolean {
        val marker = builder.mark()
        if (parseBracedURILiteral(builder)) {
            val localName = parseQNameNCName(builder, QNamePart.URIQualifiedLiteralLocalName, type, false)
            if (type === XPathElementType.WILDCARD && localName === XPathTokenType.STAR) {
                marker.done(XPathElementType.WILDCARD)
            } else {
                marker.done(URI_QUALIFIED_NAME)
            }
            return true
        }
        marker.drop()
        return false
    }

    open fun parseBracedURILiteral(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.BRACED_URI_LITERAL_START)
        while (marker != null) {
            when {
                builder.matchTokenType(XPathTokenType.STRING_LITERAL_CONTENTS) -> {
                    //
                }
                builder.matchTokenType(XPathTokenType.BRACED_URI_LITERAL_END) -> {
                    marker.done(BRACED_URI_LITERAL)
                    return true
                }
                else -> {
                    marker.done(BRACED_URI_LITERAL)
                    builder.error(XPathBundle.message("parser.error.incomplete-braced-uri-literal"))
                    return true
                }
            }
        }
        return false
    }

    // endregion
    // region Lexical Structure :: Terminal Symbols :: QName

    open fun parseQNameOrWildcard(
        builder: PsiBuilder,
        type: IElementType,
        endQNameOnSpace: Boolean = false
    ): Boolean {
        val marker = builder.mark()
        val prefix = parseQNameNCName(builder, QNamePart.Prefix, type, false)
        if (prefix != null) {
            if (parseQNameWhitespace(builder, QNamePart.Prefix, endQNameOnSpace, prefix === XPathTokenType.STAR)) {
                if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                    marker.done(XPathElementType.WILDCARD)
                } else {
                    marker.done(NCNAME)
                }
                return true
            }

            val nameMarker = builder.mark()
            if (parseQNameSeparator(builder, type)) {
                if (parseQNameWhitespace(builder, QNamePart.LocalName, endQNameOnSpace, prefix === XPathTokenType.STAR)) {
                    nameMarker.rollbackTo()
                    if (type === XPathElementType.WILDCARD && prefix === XPathTokenType.STAR) {
                        marker.done(XPathElementType.WILDCARD)
                    } else {
                        marker.done(NCNAME)
                    }
                    return true
                }
                nameMarker.drop()

                val localName = parseQNameNCName(builder, QNamePart.LocalName, type, prefix == XPathTokenType.STAR)
                if (
                    type === XPathElementType.WILDCARD &&
                    (prefix === XPathTokenType.STAR || localName === XPathTokenType.STAR)
                ) {
                    marker.done(XPathElementType.WILDCARD)
                } else {
                    marker.done(QNAME)
                }
            } else if (type === XPathElementType.WILDCARD && prefix == XPathTokenType.STAR) {
                nameMarker.drop()
                marker.done(XPathElementType.WILDCARD)
            } else {
                nameMarker.drop()
                marker.done(NCNAME)
            }
            return true
        }

        if (parseQNameSeparator(builder, null)) { // Missing prefix
            builder.advanceLexer()
            parseWhiteSpaceAndCommentTokens(builder)
            if (builder.tokenType is INCNameType || builder.tokenType == XPathTokenType.STAR) {
                builder.advanceLexer()
            }
            if (type === NCNAME) {
                marker.error(XPathBundle.message("parser.error.expected-ncname-not-qname"))
            } else {
                marker.error(XPathBundle.message("parser.error.qname.missing-prefix"))
            }
            return true
        }

        marker.drop()
        return false
    }

    enum class QNamePart {
        Prefix,
        LocalName,
        URIQualifiedLiteralLocalName
    }

    fun parseQNameNCName(
        builder: PsiBuilder,
        partType: QNamePart,
        elementType: IElementType,
        isWildcard: Boolean
    ): IElementType? {
        val tokenType = builder.tokenType
        if (tokenType is INCNameType) {
            builder.advanceLexer()
            return tokenType
        } else if (tokenType == XPathTokenType.STAR) {
            if (elementType === XPathElementType.WILDCARD) {
                if (isWildcard) {
                    builder.error(XPathBundle.message("parser.error.wildcard.both-prefix-and-local-wildcard"))
                }
            } else if (partType === QNamePart.Prefix) {
                builder.error(XPathBundle.message("parser.error.unexpected-wildcard"))
            } else if (partType === QNamePart.URIQualifiedLiteralLocalName) {
                builder.error(XPathBundle.message("parser.error.eqname.wildcard-local-name"))
            } else {
                builder.error(XPathBundle.message("parser.error.qname.wildcard-local-name"))
            }
            builder.advanceLexer()
            return tokenType
        } else if (tokenType == XPathTokenType.INTEGER_LITERAL && partType == QNamePart.LocalName) {
            // The user has started the local name with a number, so treat it as part of the QName.
            val errorMarker = builder.mark()
            builder.advanceLexer()
            errorMarker.error(XPathBundle.message("parser.error.qname.missing-local-name"))
        } else if (partType == QNamePart.LocalName) {
            // Don't consume the next token with an error, as it may be a valid part of the next construct
            // (e.g. the start of a string literal, or the '>' of a direct element constructor).
            builder.error(XPathBundle.message("parser.error.qname.missing-local-name"))
        } else if (partType === QNamePart.URIQualifiedLiteralLocalName) {
            // Don't consume the next token with an error, as it may be a valid part of the next construct
            // (e.g. the start of a string literal, or the '>' of a direct element constructor).
            builder.error(XPathBundle.message("parser.error.eqname.missing-local-name"))
        }
        return null
    }

    protected fun parseQNameWhitespace(
        builder: PsiBuilder,
        type: QNamePart,
        endQNameOnWhitespace: Boolean,
        isWildcard: Boolean
    ): Boolean {
        val marker = builder.mark()
        if (parseWhiteSpaceAndCommentTokens(builder)) {
            if (endQNameOnWhitespace) {
                marker.drop()
                return true
            } else if (type == QNamePart.Prefix && parseQNameSeparator(builder, null)) {
                if (isWildcard)
                    marker.error(XPathBundle.message("parser.error.wildcard.whitespace-before-local-part"))
                else
                    marker.error(XPathBundle.message("parser.error.qname.whitespace-before-local-part"))
                return false
            } else if (type == QNamePart.LocalName) {
                if (isWildcard)
                    marker.error(XPathBundle.message("parser.error.wildcard.whitespace-after-local-part"))
                else
                    marker.error(XPathBundle.message("parser.error.qname.whitespace-after-local-part"))
                return false
            }
        }
        marker.drop()
        return false
    }

    open fun parseQNameSeparator(builder: PsiBuilder, type: IElementType?): Boolean {
        if (builder.tokenType === XPathTokenType.QNAME_SEPARATOR) {
            if (type != null) {
                builder.advanceLexer()
            }
            return true
        }
        return false
    }

    // endregion
}
