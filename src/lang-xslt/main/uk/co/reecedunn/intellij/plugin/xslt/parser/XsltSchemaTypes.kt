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
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.*

object XsltSchemaTypes {
    fun create(type: String?): ISchemaType? = when (type) {
        "xsl:avt" -> XslAVT
        "xsl:element-names" -> XslNameTests
        "xsl:EQName" -> XslEQName
        "xsl:EQName-in-namespace" -> XslEQName
        "xsl:EQNames" -> XslEQNames
        "xsl:expr-avt" -> XslAVT
        "xsl:expression" -> XPath.Expression
        "xsl:item-type" -> XslItemType
        "xsl:mode" -> XslEQNameOrHashedKeyword
        "xsl:nametests" -> XslNameTests
        "xsl:pattern" -> XPath.Pattern
        "xsl:prefix" -> XslEQNameOrHashedKeyword
        "xsl:prefix-or-default" -> XslEQNameOrHashedKeyword
        "xsl:prefixes" -> XslPrefixes
        "xsl:QName" -> XslQName
        "xsl:QNames" -> XslQNames
        "xsl:sequence-type" -> XslSequenceType
        "xsl:tokens" -> XslPrefixes
        else -> null
    }

    fun create(element: PsiElement): ISchemaType? {
        val attr = element.toXmlAttributeValue()?.attribute ?: return null
        if (attr.parent.namespace != XSLT.NAMESPACE) return getAVTSchemaType(attr)
        return create(attr.schemaType)
    }

    private fun getAVTSchemaType(attribute: XmlAttribute): ISchemaType? {
        return when {
            attribute.isNamespaceDeclaration -> null
            attribute.parent.ancestors().find { it is XmlTag && it.namespace == XSLT.NAMESPACE } == null -> null
            attribute.value?.contains('{') == true -> XslAVT
            else -> null
        }
    }
}
