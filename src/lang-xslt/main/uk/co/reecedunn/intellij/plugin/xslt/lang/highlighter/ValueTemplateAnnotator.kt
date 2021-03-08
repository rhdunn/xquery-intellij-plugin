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
package uk.co.reecedunn.intellij.plugin.xslt.lang.highlighter

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttributeValue
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xslt.lang.ValueTemplate

class ValueTemplateAnnotator : Annotator() {
    private fun getHighlightType(element: PsiElement): TextAttributesKey? = when (element.elementType) {
        ValueTemplate.FileElementType -> XsltSyntaxHighlighterColors.XSLT_DIRECTIVE
        ValueTemplate.ESCAPED_CHARACTER -> XsltSyntaxHighlighterColors.ESCAPED_CHARACTER
        ValueTemplate.VALUE_CONTENTS -> element.contextOfType<XmlAttributeValue>(false)?.let {
            XsltSyntaxHighlighterColors.ATTRIBUTE_VALUE
        }
        else -> null
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        getHighlightType(element)?.let {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element).textAttributes(it).create()
        }
    }
}
