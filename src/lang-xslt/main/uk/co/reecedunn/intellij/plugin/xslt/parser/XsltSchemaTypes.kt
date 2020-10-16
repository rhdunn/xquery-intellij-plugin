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

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.contexts
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.*

object XsltSchemaTypes {
    fun create(type: String?): ISchemaType? = when (type) {
        "xsl:accumulator-names" -> XslEQNamesOrHashedKeywords
        "xsl:avt" -> XslAVT
        "xsl:default-mode-type" -> XslEQNameOrHashedKeyword
        "xsl:element-names" -> XslNameTests
        "xsl:EQName" -> XslEQName
        "xsl:EQName-in-namespace" -> XslEQName
        "xsl:EQNames" -> XslEQNames
        "xsl:expr-avt" -> XslAVT
        "xsl:expression" -> XslExpression
        "xsl:item-type" -> XslItemType
        "xsl:method" -> XslQName
        "xsl:mode" -> XslEQNameOrHashedKeyword
        "xsl:modes" -> XslEQNamesOrHashedKeywords
        "xsl:nametests" -> XslNameTests
        "xsl:pattern" -> XslPattern
        "xsl:prefix" -> XslEQNameOrHashedKeyword
        "xsl:prefix-list" -> XslEQNamesOrHashedKeywords
        "xsl:prefix-list-or-all" -> XslEQNamesOrHashedKeywords
        "xsl:prefix-or-default" -> XslEQNameOrHashedKeyword
        "xsl:prefixes" -> XslPrefixes
        "xsl:QName" -> XslQName
        "xsl:QNames" -> XslQNames
        "xsl:sequence-type" -> XslSequenceType
        "xsl:streamability-type" -> XslEQName
        "xsl:tokens" -> XslPrefixes
        else -> null
    }

    fun create(element: PsiElement): ISchemaType? {
        return element.contexts(false).mapNotNull { getSchemaType(it) }.firstOrNull()
    }

    private fun getSchemaType(element: PsiElement) = when (element) {
        is XmlAttributeValue -> element.attribute?.let { attr ->
            when (attr.parent.namespace) {
                XSD_NAMESPACE -> getAVTSchemaType(attr) // Calling attr.schemaType here causes an infinite recursion.
                else -> create(attr.schemaType) ?: getAVTSchemaType(attr)
            }
        }
        is XmlText -> {
            if ((element.parent as? XmlTag)?.expandText == true && element.value.contains(BRACES))
                TextValueTemplate
            else
                null
        }
        else -> null
    }

    private fun getAVTSchemaType(attribute: XmlAttribute): ISchemaType? = when {
        attribute.isNamespaceDeclaration -> null
        attribute.parent.ancestors().find { it is XmlTag && it.namespace == XSLT.NAMESPACE } == null -> null
        attribute.value?.contains('{') == true -> XslAVT
        else -> null
    }

    private const val XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema"
    private val BRACES = "[{}]".toRegex()
}
