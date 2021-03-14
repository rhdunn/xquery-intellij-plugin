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

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttributeValue
import uk.co.reecedunn.intellij.plugin.core.sequences.contexts
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.xdm.schema.ISchemaListType
import uk.co.reecedunn.intellij.plugin.xdm.schema.ISchemaType
import uk.co.reecedunn.intellij.plugin.xdm.schema.XdmSchemaListType
import uk.co.reecedunn.intellij.plugin.xdm.schema.XdmSchemaType

object XProcSchemaTypes {
    private val AvtDatatype: ISchemaType = XdmSchemaType("avt.datatype", "xsl:value-template")
    private val EQName: ISchemaListType = XdmSchemaListType("EQName", "EQNames-or-hashed-keywords")
    private val EQNameList: ISchemaListType = XdmSchemaListType("EQNameList", "EQNames-or-hashed-keywords")
    private val ExcludeInlinePrefixes: ISchemaListType = XdmSchemaListType("ExcludeInlinePrefixes", "EQNames-or-hashed-keywords")
    private val ListOfEQNames: ISchemaListType = XdmSchemaListType("ListOfEQNames", "EQNames-or-hashed-keywords")
    private val ListOfQNames: ISchemaListType = XdmSchemaListType("ListOfQNames", "EQNames-or-hashed-keywords")
    private val PrefixList: ISchemaListType = XdmSchemaListType("PrefixList", "EQNames-or-hashed-keywords")
    private val QName: ISchemaListType = XdmSchemaListType("QName", "EQNames-or-hashed-keywords")
    private val XPathExpression: ISchemaType = XdmSchemaType("XPathExpression", "XMLPath")
    private val XPathSequenceType: ISchemaType = XdmSchemaType("XPathSequenceType", "xsl:sequence-type")
    private val XSLTSelectionPattern: ISchemaType = XdmSchemaType("XSLTSelectionPattern", "XMLPath")

    private fun create(type: String?): ISchemaType? = when (type) {
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

    fun create(element: PsiElement): ISchemaType? {
        return element.contexts(false).mapNotNull { getSchemaType(it) }.firstOrNull()
    }

    private fun getSchemaType(element: PsiElement) = when (element) {
        is XmlAttributeValue -> element.attribute?.let { attr ->
            create(attr.schemaType)
        }
        else -> null
    }
}
