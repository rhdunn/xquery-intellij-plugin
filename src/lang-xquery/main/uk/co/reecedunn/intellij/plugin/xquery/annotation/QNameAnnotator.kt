/*
 * Copyright (C) 2016, 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lexer.XQuerySyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration

class QNameAnnotator : QNameAnnotator() {
    private fun getHighlightAttributes(element: PsiElement, resolveReferences: Boolean = true): TextAttributesKey {
        return when (element.getUsageType()) {
            XpmUsageType.Annotation -> XQuerySyntaxHighlighterColors.ANNOTATION
            XpmUsageType.Attribute -> XQuerySyntaxHighlighterColors.ATTRIBUTE
            XpmUsageType.DecimalFormat -> XQuerySyntaxHighlighterColors.DECIMAL_FORMAT
            XpmUsageType.Element -> XQuerySyntaxHighlighterColors.ELEMENT
            XpmUsageType.FunctionDecl -> XQuerySyntaxHighlighterColors.FUNCTION_DECL
            XpmUsageType.FunctionRef -> XQuerySyntaxHighlighterColors.FUNCTION_CALL
            XpmUsageType.Namespace -> XQuerySyntaxHighlighterColors.NS_PREFIX
            XpmUsageType.Option -> XQuerySyntaxHighlighterColors.OPTION
            XpmUsageType.Parameter -> XQuerySyntaxHighlighterColors.PARAMETER
            XpmUsageType.Pragma -> XQuerySyntaxHighlighterColors.PRAGMA
            XpmUsageType.Type -> XQuerySyntaxHighlighterColors.TYPE
            XpmUsageType.Variable -> {
                if (resolveReferences)
                    element.reference?.resolve()?.let {
                        getHighlightAttributes(it, false)
                    } ?: XQuerySyntaxHighlighterColors.VARIABLE
                else
                    XQuerySyntaxHighlighterColors.VARIABLE
            }
            XpmUsageType.Unknown -> XQuerySyntaxHighlighterColors.IDENTIFIER
        }
    }

    private fun getElementHighlight(element: PsiElement): TextAttributesKey {
        val ret = XQuerySyntaxHighlighter.getTokenHighlights(element.elementType!!)
        return when {
            ret.isEmpty() -> XQuerySyntaxHighlighterColors.IDENTIFIER
            ret.size == 1 -> ret[0]
            else -> when (ret[1]) {
                XQuerySyntaxHighlighterColors.XML_TAG_NAME -> XQuerySyntaxHighlighterColors.ELEMENT
                XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME -> XQuerySyntaxHighlighterColors.ATTRIBUTE
                else -> ret[1]
            }
        }
    }

    private fun isXmlnsPrefix(element: PsiElement): Boolean {
        return (element.parent as? XpmNamespaceDeclaration)?.accepts(XdmNamespaceType.Prefixed) == true
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return

        if (element.prefix != null) {
            if (element.prefix !is XdmWildcardValue && !isXmlnsPrefix(element)) {
                val prefix = element.prefix?.element!!
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                    .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                    .create()
                if (element.parent is PluginDirAttribute || element.parent is XQueryDirElemConstructor) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                        .textAttributes(XQuerySyntaxHighlighterColors.XML_TAG)
                        .create()
                }
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                    .textAttributes(XQuerySyntaxHighlighterColors.NS_PREFIX)
                    .create()
            }

            // Detect whitespace errors here instead of the parser so the QName annotator gets run.
            element.children().filterIsElementType(XPathTokenType.QNAME_SEPARATOR).firstOrNull()?.let {
                checkQNameWhitespaceBefore(element, it, holder)
                checkQNameWhitespaceAfter(element, it, holder)
            }
        }

        if (element.localName != null) {
            val localName = element.localName?.element!!
            val highlight = getHighlightAttributes(element)
            val elementHighlight = getElementHighlight(localName)
            when {
                highlight !== elementHighlight -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    if (element.parent is PluginDirAttribute) {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                            .textAttributes(XQuerySyntaxHighlighterColors.XML_TAG)
                            .create()
                    }
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(highlight)
                        .create()
                }
                localName.elementType is IKeywordOrNCNameType && highlight !== elementHighlight -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(XQuerySyntaxHighlighterColors.IDENTIFIER)
                        .create()
                }
            }
        }
    }
}
