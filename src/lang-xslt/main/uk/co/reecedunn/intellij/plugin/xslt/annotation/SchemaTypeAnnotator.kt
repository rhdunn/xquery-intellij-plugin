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
package uk.co.reecedunn.intellij.plugin.xslt.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttributeValue
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.schemaType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSequenceType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypes
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslItemType
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslSequenceType
import java.lang.UnsupportedOperationException

class SchemaTypeAnnotator(val schemaType: ISchemaType? = null) : Annotator() {
    companion object {
        val REMOVE_START = "^(Plugin|Scripting|UpdateFacility|XPath|XQuery)".toRegex()
        val REMOVE_END = "(PsiImpl|Impl)$".toRegex()
    }

    fun getSymbolName(element: PsiElement): String {
        return element.javaClass.name.split('.').last().replace(REMOVE_START, "").replace(REMOVE_END, "")
    }

    fun accept(schemaType: ISchemaType, element: PsiElement): Boolean = when (schemaType) {
        XslItemType -> element !is XPathSequenceType
        XslSequenceType -> true
        else -> throw UnsupportedOperationException()
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiFile) return
        val schemaType = schemaType ?: XsltSchemaTypes.create(element) ?: return
        element.children().forEach { child ->
            if (!accept(schemaType, child)) {
                val symbol = getSymbolName(child)
                val message = XsltBundle.message("schema.validation.unsupported", symbol, schemaType.type)
                holder.newAnnotation(HighlightSeverity.ERROR, message).range(element).create()
            }
        }
    }
}
