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

import com.intellij.lang.PsiBuilder
import uk.co.reecedunn.intellij.plugin.core.lang.errorOnTokenType
import uk.co.reecedunn.intellij.plugin.core.lang.matchTokenType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParser
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.*

class XsltSchemaTypesParser(private val schemaType: ISchemaType) : XPathParser() {
    // region Grammar

    override fun parse(builder: PsiBuilder, isFirst: Boolean): Boolean = when (schemaType) {
        XslValueTemplate -> parseAVT(builder)
        XslEQName -> parseEQName(builder)
        XslEQNames -> parseEQNames(builder)
        XPath.Expression -> parseExpr(builder, null)
        XslItemType -> parseItemType(builder)
        XslNameTests -> parseNameTests(builder)
        XPath.Pattern -> parseExpr(builder, null)
        XslEQNameOrHashedKeyword -> parseEQNameOrHashedKeyword(builder)
        XslEQNamesOrHashedKeywords -> parseEQNamesOrHashedKeywords(builder)
        XslPrefixes -> parsePrefixes(builder)
        XslQName -> parseQName(builder)
        XslQNames -> parseQNames(builder)
        XslSequenceType -> parseSequenceType(builder)
        else -> false
    }

    private fun parseSchemaList(builder: PsiBuilder, itemType: (PsiBuilder) -> Boolean): Boolean {
        var matched = false
        while (itemType(builder)) {
            matched = true
            builder.matchTokenType(XPathTokenType.WHITE_SPACE)
        }
        return matched
    }

    // endregion
    // region Grammar :: schema types

    private fun parseAVT(builder: PsiBuilder): Boolean {
        var matched = false
        while (
            builder.matchTokenType(XslValueTemplate.VALUE_CONTENTS) ||
            builder.matchTokenType(XslValueTemplate.ESCAPED_CHARACTER) ||
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
