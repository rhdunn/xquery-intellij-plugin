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
package uk.co.reecedunn.intellij.plugin.xslt.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.originalFile
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xslt.intellij.fileTypes.XsltSchemaTypesFileType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle

@Suppress("MemberVisibilityCanBePrivate")
object XsltSchemaTypes : Language(XPath, "XSLTSchemaTypes") {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = XsltBundle.message("language.schema-types.name")

    override fun getAssociatedFileType(): LanguageFileType? = XsltSchemaTypesFileType

    // endregion
    // region Schema Types

    fun create(type: String?): ISchemaType? = when (type) {
        EQName.type -> EQName
        Expression.type -> Expression
        ItemType.type -> ItemType
        Pattern.type -> Pattern
        Prefixes.type, "xsl:tokens" -> Prefixes
        QName.type -> QName
        SequenceType.type -> SequenceType
        else -> null
    }

    fun create(element: PsiElement): ISchemaType? {
        val schemaType = element.containingFile.virtualFile.originalFile.getUserData(ISchemaType.XDM_SCHEMA_TYPE)
        if (schemaType != null) return schemaType

        val attr = element.toXmlAttributeValue()?.attribute ?: return null
        if (attr.parent.namespace != XSLT.NAMESPACE) return null
        return create(attr.schemaType)
    }

    // endregion
    // region Schema Types :: XSLT 1.0

    val Expression: ISchemaType = ISchemaType("xsl:expression", false, XPath)
    val Pattern: ISchemaType = ISchemaType("xsl:pattern", false, XPath)
    val QName: ISchemaType = ISchemaType("xsl:QName", false, XsltSchemaTypes)

    // endregion
    // region Schema Types :: XSLT 2.0

    val Prefixes: ISchemaType = ISchemaType("xsl:prefixes", false, XsltSchemaTypes)
    val SequenceType: ISchemaType = ISchemaType("xsl:sequence-type", false, XsltSchemaTypes)

    // endregion
    // region Schema Types :: XSLT 3.0

    val EQName: ISchemaType = ISchemaType("xsl:EQName", false, XsltSchemaTypes)
    val ItemType: ISchemaType = ISchemaType("xsl:item-type", false, XsltSchemaTypes)

    // endregion
}
