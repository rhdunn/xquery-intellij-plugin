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
package uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpm.lang.highlighter.XpmSemanticHighlighter

open class QNameAnnotator : Annotator {
    private fun checkQNameWhitespaceBefore(qname: XsQNameValue, separator: PsiElement, holder: AnnotationHolder) {
        val before = separator.prevSibling
        if (before.elementType === XPathTokenType.WHITE_SPACE || before is XPathComment) {
            val message =
                if (qname is XPathWildcard)
                    XPathBundle.message("parser.error.wildcard.whitespace-before-local-part")
                else
                    XPathBundle.message("parser.error.qname.whitespace-before-local-part")
            holder.newAnnotation(HighlightSeverity.ERROR, message).range(before).create()
        }
    }

    private fun checkQNameWhitespaceAfter(qname: XsQNameValue, separator: PsiElement, holder: AnnotationHolder) {
        val after = separator.nextSibling
        if (after.elementType === XPathTokenType.WHITE_SPACE || after is XPathComment) {
            val message =
                if (qname is XPathWildcard)
                    XPathBundle.message("parser.error.wildcard.whitespace-after-local-part")
                else
                    XPathBundle.message("parser.error.qname.whitespace-after-local-part")
            holder.newAnnotation(HighlightSeverity.ERROR, message).range(after).create()
        }
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return

        val highlighter = XpmSemanticHighlighter.highlighter(element) ?: return
        val prefix = element.prefix?.element
        val localName = element.localName?.element

        if (prefix != null) {
            if (prefix !== localName) {
                val highlight = highlighter.getQNamePrefixHighlighting(element)
                highlighter.highlight(prefix, highlight, holder)
            }

            // Detect whitespace errors here instead of the parser so the QName annotator gets run.
            element.children().filterIsElementType(XPathTokenType.QNAME_SEPARATOR).firstOrNull()?.let {
                checkQNameWhitespaceBefore(element, it, holder)
                checkQNameWhitespaceAfter(element, it, holder)
            }
        }

        if (localName != null) {
            val highlight = highlighter.getHighlighting(element)
            val elementHighlight = highlighter.getElementHighlighting(localName)
            when {
                highlight !== elementHighlight -> {
                    highlighter.highlight(localName, highlight, holder)
                }
                localName.elementType is IKeywordOrNCNameType && highlight !== elementHighlight -> {
                    highlighter.highlight(localName, holder)
                }
            }
        }
    }
}
