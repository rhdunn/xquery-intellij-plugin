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
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenTypeWithMarker
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParser
import uk.co.reecedunn.intellij.plugin.xslt.lang.EQNamesOrHashedKeywords
import uk.co.reecedunn.intellij.plugin.xslt.lang.NameTests
import uk.co.reecedunn.intellij.plugin.xslt.lang.SequenceType
import uk.co.reecedunn.intellij.plugin.xslt.lang.ValueTemplate
import uk.co.reecedunn.intellij.plugin.xslt.resources.XsltBundle

class XsltSchemaTypesParser(
    private val schemaType: Language,
    private val HASHED_KEYWORD_TOKEN: IElementType? = null
) : XPathParser() {
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

    private fun parseSchemaType(builder: PsiBuilder): Boolean = when (schemaType) {
        ValueTemplate -> parseValueTemplate(builder)
        NameTests -> parseNameTests(builder)
        EQNamesOrHashedKeywords -> parseEQNamesOrHashedKeywords(builder)
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
                XsltBundle.message("parser.error.mismatched-enclosed-expr")
            ) ||
            parseEnclosedExprOrBlock(builder, null, BlockOpen.REQUIRED, BlockExpr.REQUIRED)
        ) {
            matched = true
        }
        return matched
    }

    private fun parseEQName(builder: PsiBuilder): Boolean {
        return parseEQNameOrWildcard(builder, XPathElementType.QNAME) != null
    }

    private fun parseNameTest(builder: PsiBuilder): Boolean {
        return parseNameTest(builder, null, allowAxisStep = false) != null
    }

    private fun parseNameTests(builder: PsiBuilder): Boolean = parseSchemaList(builder, ::parseNameTest)

    private fun parseEQNameOrHashedKeyword(builder: PsiBuilder): Boolean {
        return parseEQName(builder) || parseHashedKeyword(builder)
    }

    private fun parseHashedKeyword(builder: PsiBuilder): Boolean {
        val marker = builder.matchTokenTypeWithMarker(XPathTokenType.FUNCTION_REF_OPERATOR)
        if (marker != null) {
            if (builder.tokenType is INCNameType) {
                builder.advanceLexer()
                marker.done(HASHED_KEYWORD_TOKEN!!)
                return true
            }
            marker.drop()
        }
        return false
    }

    private fun parseEQNamesOrHashedKeywords(builder: PsiBuilder): Boolean {
        return parseSchemaList(builder, ::parseEQNameOrHashedKeyword)
    }

    // endregion
}
