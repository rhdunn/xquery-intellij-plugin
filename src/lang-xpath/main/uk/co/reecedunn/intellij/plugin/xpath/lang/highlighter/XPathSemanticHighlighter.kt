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
package uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath as XPathFile
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpm.lang.highlighter.XpmSemanticHighlighter

object XPathSemanticHighlighter : XpmSemanticHighlighter {
    private fun getVariableHighlighting(element: PsiElement?): TextAttributesKey = when (element?.getUsageType()) {
        XpmUsageType.Parameter -> XPathSyntaxHighlighterColors.PARAMETER
        else -> XPathSyntaxHighlighterColors.VARIABLE
    }

    override fun accepts(file: PsiFile): Boolean = file is XPathFile

    override fun getHighlighting(element: PsiElement): TextAttributesKey = when (element.getUsageType()) {
        XpmUsageType.Annotation -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
        XpmUsageType.Attribute -> XPathSyntaxHighlighterColors.ATTRIBUTE
        XpmUsageType.DecimalFormat -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
        XpmUsageType.Element -> XPathSyntaxHighlighterColors.ELEMENT
        XpmUsageType.FunctionDecl -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
        XpmUsageType.FunctionRef -> XPathSyntaxHighlighterColors.FUNCTION_CALL
        XpmUsageType.MapKey -> XPathSyntaxHighlighterColors.MAP_KEY
        XpmUsageType.Namespace -> XPathSyntaxHighlighterColors.NS_PREFIX
        XpmUsageType.Option -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
        XpmUsageType.Parameter -> XPathSyntaxHighlighterColors.PARAMETER
        XpmUsageType.Pragma -> XPathSyntaxHighlighterColors.PRAGMA
        XpmUsageType.ProcessingInstruction -> XPathSyntaxHighlighterColors.PROCESSING_INSTRUCTION
        XpmUsageType.Type -> XPathSyntaxHighlighterColors.TYPE
        XpmUsageType.Variable -> getVariableHighlighting(element.reference?.resolve())
        XpmUsageType.Unknown -> XPathSyntaxHighlighterColors.IDENTIFIER
    }

    override fun getElementHighlighting(element: PsiElement): TextAttributesKey {
        return XPathSyntaxHighlighterColors.IDENTIFIER
    }

    override fun getQNamePrefixHighlighting(element: PsiElement): TextAttributesKey = when {
        XpmSemanticHighlighter.isXmlnsPrefix(element) -> XPathSyntaxHighlighterColors.ATTRIBUTE
        else -> XPathSyntaxHighlighterColors.NS_PREFIX
    }

    override fun highlight(element: PsiElement, holder: AnnotationHolder) {
        highlight(element, XPathSyntaxHighlighterColors.IDENTIFIER, holder)
    }

    override fun highlight(element: PsiElement, textAttributes: TextAttributesKey, holder: AnnotationHolder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element)
            .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
            .create()

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element)
            .textAttributes(textAttributes)
            .create()
    }
}
