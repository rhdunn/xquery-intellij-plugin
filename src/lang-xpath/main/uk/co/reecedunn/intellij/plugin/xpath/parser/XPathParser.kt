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
open class XPathParser : PsiParser {
    open val STRING_LITERAL: IElementType = XPathElementType.STRING_LITERAL
    open val QNAME: IElementType = XPathElementType.QNAME
    open val NCNAME: IElementType = XPathElementType.NCNAME

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
        if (parseExprSingle(builder)) {
            return true
        }
        if (isFirst) {
            builder.error(XPathBundle.message("parser.error.expected-expression"))
        }
        return false
    }

    // endregion
    // region Grammar :: Expr

    private fun parseExprSingle(builder: PsiBuilder): Boolean {
        return parseOrExpr(builder)
    }

    // endregion
    // region Grammar :: Expr :: OrExpr

    private fun parseOrExpr(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseStepExpr(builder)) {
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

    open fun parseNodeTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseNameTest(builder, type)) {
            marker.done(XPathElementType.NODE_TEST)
            return true
        }
        marker.drop()
        return false
    }

    open fun parseNameTest(builder: PsiBuilder, type: IElementType?): Boolean {
        val marker = builder.mark()
        if (parseQName(builder)) {
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
    // region Lexical Structure :: Terminal Symbols :: QName

    private fun parseQName(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        if (parseQNameNCName(builder)) {
            if (parseQNameSeparator(builder)) {
                builder.advanceLexer()

                parseQNameNCName(builder)
                marker.done(QNAME)
            } else {
                marker.done(NCNAME)
            }
            return true
        }
        marker.drop()
        return false
    }

    private fun parseQNameNCName(builder: PsiBuilder): Boolean {
        if (builder.tokenType is INCNameType) {
            builder.advanceLexer()
            return true
        }
        return false
    }

    open fun parseQNameSeparator(builder: PsiBuilder): Boolean {
        return builder.tokenType === XPathTokenType.QNAME_SEPARATOR
    }

    // endregion
}
