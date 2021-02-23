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

class QNameAnnotator : QNameAnnotator() {
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

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return

        if (element.prefix != null) {
            if (element.prefix !is XdmWildcardValue) {
                val prefix = element.prefix?.element!!
                if (isXmlnsPrefix(element)) {
                    highlight(prefix, XQuerySyntaxHighlighterColors.ATTRIBUTE, holder)
                } else {
                    highlight(prefix, XQuerySyntaxHighlighterColors.NS_PREFIX, holder)
                }
            }

            // Detect whitespace errors here instead of the parser so the QName annotator gets run.
            element.children().filterIsElementType(XPathTokenType.QNAME_SEPARATOR).firstOrNull()?.let {
                checkQNameWhitespaceBefore(element, it, holder)
                checkQNameWhitespaceAfter(element, it, holder)
            }
        }

        if (element.localName != null) {
            val localName = element.localName?.element!!
            val highlight = XQuerySemanticHighlighter.getHighlighting(element)
            val elementHighlight = getElementHighlight(localName)
            when {
                highlight !== elementHighlight -> highlight(localName, highlight, holder)
                localName.elementType is IKeywordOrNCNameType && highlight !== elementHighlight -> {
                    highlight(localName, XQuerySyntaxHighlighterColors.IDENTIFIER, holder)
                }
            }
        }
    }

    private fun highlight(element: PsiElement, textAttributes: TextAttributesKey, holder: AnnotationHolder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element)
            .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
            .create()

        val parent = element.parent.parent
        if (parent is PluginDirAttribute || parent is XQueryDirElemConstructor) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element)
                .textAttributes(XQuerySyntaxHighlighterColors.XML_TAG)
                .create()
        }

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element)
            .textAttributes(textAttributes)
            .create()
    }
}
