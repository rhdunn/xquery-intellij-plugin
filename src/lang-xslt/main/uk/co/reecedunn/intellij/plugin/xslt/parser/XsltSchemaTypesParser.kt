/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.parser

import com.intellij.lang.Language
import com.intellij.lang.PsiBuilder
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParser
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle

class XsltSchemaTypesParser(private val schemaType: Language) : XPathParser() {
    // region Grammar

    override fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean {
        if (parseSchemaType(builder)) {
            parseWhiteSpaceAndCommentTokens(builder)
            return true
        }
        return false
    }

    override fun parseComment(builder: PsiBuilder): Boolean = when (schemaType) {
        ValueTemplate -> super.parseComment(builder)
        SequenceType -> super.parseComment(builder)
        else -> parseSchemaComment(builder)
    }

    private fun parseSchemaComment(builder: PsiBuilder): Boolean {
        if (builder.tokenType === XPathTokenType.COMMENT_START_TAG) {
            val errorMarker = builder.mark()
            builder.advanceLexer()
            parseCommentContents(builder)
            builder.advanceLexer() // COMMENT_END_TAG | UNEXPECTED_END_OF_BLOCK
            errorMarker.error(XsltBundle.message("parser.error.comment-in-schema-type", "(:"))
            return true
        } else if (builder.tokenType === XPathTokenType.COMMENT_END_TAG) {
            val errorMarker = builder.mark()
            builder.advanceLexer()
            errorMarker.error(XsltBundle.message("parser.error.comment-in-schema-type", "(:"))
            return true
        }
        return false
    }

    fun parseSchemaType(builder: PsiBuilder): Boolean = when (schemaType) {
        ValueTemplate -> parseValueTemplate(builder)
        EQName -> parseEQName(builder)
        EQNames -> parseEQNames(builder)
        NameTests -> parseNameTests(builder)
        EQNameOrHashedKeyword -> parseEQNameOrHashedKeyword(builder)
        EQNamesOrHashedKeywords -> parseEQNamesOrHashedKeywords(builder)
        Prefixes -> parsePrefixes(builder)
        QName -> parseQName(builder)
        QNames -> parseQNames(builder)
        SequenceType -> parseSequenceType(builder)
        else -> false
    }

    private fun parseSchemaList(builder: PsiBuilder, itemType: (PsiBuilder) -> Boolean): Boolean {
        var matched = false
        while (itemType(builder)) {
            matched = true
            parseWhiteSpaceAndCommentTokens(builder)
        }
        return matched
    }

    // endregion
    // region Grammar :: schema types

    private fun parseValueTemplate(builder: PsiBuilder): Boolean {
        var matched = false
        while (
            builder.matchTokenType(ValueTemplate.VALUE_CONTENTS) ||
            builder.matchTokenType(ValueTemplate.ESCAPED_CHARACTER) ||
            builder.errorOnTokenType(
                XPathTokenType.BLOCK_CLOSE,
                XsltBundle.message("parser.error.mismatched-exclosed-expr")
            ) ||
            parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)
        ) {
            matched = true
        }
        return matched
    }

    private fun parseEQName(builder: PsiBuilder): Boolean = parseEQNameOrWildcard(builder, QNAME) != null

    private fun parseEQNames(builder: PsiBuilder): Boolean = parseSchemaList(builder, ::parseEQName)

    private fun parseNameTest(builder: PsiBuilder): Boolean = parseNameTest(builder, null) != null

    private fun parseNameTests(builder: PsiBuilder): Boolean = parseSchemaList(builder, ::parseNameTest)

    private fun parseEQNameOrHashedKeyword(builder: PsiBuilder): Boolean {
        if (parseEQName(builder)) {
            return true
        } else if (builder.matchTokenType(XPathTokenType.FUNCTION_REF_OPERATOR)) {
            if (builder.tokenType is INCNameType) {
                builder.advanceLexer()
                return true
            }
        }
        return false
    }

    private fun parseEQNamesOrHashedKeywords(builder: PsiBuilder): Boolean {
        return parseSchemaList(builder, ::parseEQNameOrHashedKeyword)
    }

    private fun parsePrefixes(builder: PsiBuilder): Boolean = parseSchemaList(builder, ::parseNCName)

    private fun parseQName(builder: PsiBuilder): Boolean = parseQNameOrWildcard(builder, QNAME) != null

    private fun parseQNames(builder: PsiBuilder): Boolean = parseSchemaList(builder, ::parseQName)

    // endregion
}
