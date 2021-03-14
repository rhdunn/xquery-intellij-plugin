/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.schema

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.contexts
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.xdm.schema.*
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.parser.expandText

object XsltSchemaTypes : XdmSchemaTypes() {
    // region Schema Types

    private val TextValueTemplate: ISchemaType = XdmSchemaType("text-value-template", "xsl:value-template")
    val XslAccumulatorNames: ISchemaListType = XdmSchemaListType("xsl:accumulator-names", "EQNames-or-hashed-keywords")
    private val XslAVT: ISchemaType = XdmSchemaType("xsl:avt", "xsl:value-template")
    val XslDefaultModeType: ISchemaType = XdmSchemaType("xsl:default-mode-type", "EQNames-or-hashed-keywords")
    private val XslElementNames: ISchemaListType = XdmSchemaListType("xsl:element-names", "xsl:nametests")
    val XslEQName: ISchemaType = XdmSchemaType("xsl:EQName", "EQNames-or-hashed-keywords")
    val XslEQNameInNamespace: ISchemaType = XdmSchemaType("xsl:EQName-in-namespace", "EQNames-or-hashed-keywords")
    val XslEQNames: ISchemaListType = XdmSchemaListType("xsl:EQNames", "EQNames-or-hashed-keywords")
    private val XslExprAVT: ISchemaType = XdmSchemaType("xsl:expr-avt", "xsl:value-template")
    private val XslExpression: ISchemaType = XdmSchemaType("xsl:expression", "XMLPath")
    val XslItemType: ISchemaType = XdmSchemaType("xsl:item-type", "xsl:sequence-type")
    val XslMethod: ISchemaType = XdmSchemaType("xsl:method", "EQNames-or-hashed-keywords")
    val XslMode: ISchemaType = XdmSchemaType("xsl:mode", "EQNames-or-hashed-keywords")
    val XslModes: ISchemaListType = XdmSchemaListType("xsl:modes", "EQNames-or-hashed-keywords")
    private val XslNameTests: ISchemaListType = XdmSchemaListType("xsl:nametests", "xsl:nametests")
    private val XslPattern: ISchemaType = XdmSchemaType("xsl:pattern", "XMLPath")
    val XslPrefix: ISchemaType = XdmSchemaType("xsl:prefix", "EQNames-or-hashed-keywords")
    val XslPrefixList: ISchemaListType = XdmSchemaListType("xsl:prefix-list", "EQNames-or-hashed-keywords")
    val XslPrefixListOrAll: ISchemaListType = XdmSchemaListType("xsl:prefix-list-or-all", "EQNames-or-hashed-keywords")
    val XslPrefixOrDefault: ISchemaType = XdmSchemaType("xsl:prefix-or-default", "EQNames-or-hashed-keywords")
    val XslPrefixes: ISchemaListType = XdmSchemaListType("xsl:prefixes", "EQNames-or-hashed-keywords")
    val XslQName: ISchemaType = XdmSchemaType("xsl:QName", "EQNames-or-hashed-keywords")
    val XslQNames: ISchemaListType = XdmSchemaListType("xsl:QNames", "EQNames-or-hashed-keywords")
    val XslSequenceType: ISchemaType = XdmSchemaType("xsl:sequence-type", "xsl:sequence-type")
    val XslStreamabilityType: ISchemaType = XdmSchemaType("xsl:streamability-type", "EQNames-or-hashed-keywords")
    val XslTokens: ISchemaListType = XdmSchemaListType("xsl:tokens", "EQNames-or-hashed-keywords")

    // endregion
    // region XdmSchemaTypes

    override fun create(type: String?): ISchemaType? = when (type) {
        "xsl:accumulator-names" -> XslAccumulatorNames
        "xsl:avt" -> XslAVT
        "xsl:default-mode-type" -> XslDefaultModeType
        "xsl:element-names" -> XslElementNames
        "xsl:EQName" -> XslEQName
        "xsl:EQName-in-namespace" -> XslEQNameInNamespace
        "xsl:EQNames" -> XslEQNames
        "xsl:expr-avt" -> XslExprAVT
        "xsl:expression" -> XslExpression
        "xsl:item-type" -> XslItemType
        "xsl:method" -> XslMethod
        "xsl:mode" -> XslMode
        "xsl:modes" -> XslModes
        "xsl:nametests" -> XslNameTests
        "xsl:pattern" -> XslPattern
        "xsl:prefix" -> XslPrefix
        "xsl:prefix-list" -> XslPrefixList
        "xsl:prefix-list-or-all" -> XslPrefixListOrAll
        "xsl:prefix-or-default" -> XslPrefixOrDefault
        "xsl:prefixes" -> XslPrefixes
        "xsl:QName" -> XslQName
        "xsl:QNames" -> XslQNames
        "xsl:sequence-type" -> XslSequenceType
        "xsl:streamability-type" -> XslStreamabilityType
        "xsl:tokens" -> XslTokens
        else -> null
    }

    override fun getSchemaType(element: PsiElement) = when (element) {
        is XmlAttributeValue -> element.attribute?.let { attr ->
            when (attr.parent.namespace) {
                XSD_NAMESPACE -> create(attr) // Calling attr.schemaType here causes an infinite recursion.
                else -> create(attr.schemaType) ?: create(attr)
            }
        }
        is XmlText -> create(element)
        else -> null
    }

    private fun create(attribute: XmlAttribute): ISchemaType? = when {
        attribute.isNamespaceDeclaration -> null
        attribute.parent.ancestors().find { it is XmlTag && it.namespace == XSLT.NAMESPACE } == null -> null
        attribute.value?.contains('{') == true -> XslAVT
        else -> null
    }

    private fun create(text: XmlText): ISchemaType? = when {
        (text.parent as? XmlTag)?.expandText == true && text.value.contains(BRACES) -> TextValueTemplate
        else -> null
    }

    private const val XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema"
    private val BRACES = "[{}]".toRegex()

    // endregion
}
