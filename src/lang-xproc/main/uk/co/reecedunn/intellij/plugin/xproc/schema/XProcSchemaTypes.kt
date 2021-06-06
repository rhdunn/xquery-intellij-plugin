/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xproc.schema

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.xdm.schema.*
import uk.co.reecedunn.intellij.plugin.xproc.lang.XProc

@Suppress("Reformat")
object XProcSchemaTypes : XdmSchemaTypes() {
    // region Schema Types

    private val AvtDatatype: ISchemaType = XdmSchemaType("avt.datatype", "xsl:value-template")
    private val EQName: ISchemaListType = XdmSchemaListType("EQName", "EQNames-or-hashed-keywords")
    private val EQNameList: ISchemaListType = XdmSchemaListType("EQNameList", "EQNames-or-hashed-keywords")
    private val ExcludeInlinePrefixes: ISchemaListType = XdmSchemaListType("ExcludeInlinePrefixes", "EQNames-or-hashed-keywords")
    private val ListOfEQNames: ISchemaListType = XdmSchemaListType("ListOfEQNames", "EQNames-or-hashed-keywords")
    private val ListOfQNames: ISchemaListType = XdmSchemaListType("ListOfQNames", "EQNames-or-hashed-keywords")
    private val PrefixList: ISchemaListType = XdmSchemaListType("PrefixList", "EQNames-or-hashed-keywords")
    private val QName: ISchemaListType = XdmSchemaListType("QName", "EQNames-or-hashed-keywords")
    private val TvtDatatype: ISchemaType = XdmSchemaType("tvt.datatype", "xsl:value-template")
    private val XPathExpression: ISchemaType = XdmSchemaType("XPathExpression", "XMLPath")
    private val XPathSequenceType: ISchemaType = XdmSchemaType("XPathSequenceType", "xsl:sequence-type")
    private val XSLTSelectionPattern: ISchemaType = XdmSchemaType("XSLTSelectionPattern", "XMLPath")

    // endregion
    // region XdmSchemaTypes

    override fun create(type: String?): ISchemaType? = when (type) {
        "avt.datatype" -> AvtDatatype
        "EQName" -> EQName
        "EQNameList" -> EQNameList
        "ExcludeInlinePrefixes" -> ExcludeInlinePrefixes
        "ListOfEQNames" -> ListOfEQNames
        "ListOfQNames" -> ListOfQNames
        "PrefixList" -> PrefixList
        "QName" -> QName
        "XPathSequenceType" -> XPathSequenceType
        "XPathExpression" -> XPathExpression
        "XSLTSelectionPattern" -> XSLTSelectionPattern
        else -> null
    }

    override fun create(attribute: XmlAttribute): ISchemaType? = when {
        attribute.isNamespaceDeclaration -> null
        attribute.parent.ancestors().find { it is XmlTag && isInline(it) } == null -> null
        attribute.value?.contains('{') == true -> AvtDatatype
        else -> super.create(attribute)
    }

    override fun create(text: XmlText): ISchemaType? = when {
        text.parent.ancestorsAndSelf().find { it is XmlTag && isInline(it) } == null -> null
        text.value.contains(BRACES) -> TvtDatatype
        else -> null
    }

    private fun isInline(tag: XmlTag): Boolean = tag.localName == "inline" && tag.namespace == XProc.NAMESPACE

    private val BRACES = "[{}]".toRegex()

    // endregion
}
