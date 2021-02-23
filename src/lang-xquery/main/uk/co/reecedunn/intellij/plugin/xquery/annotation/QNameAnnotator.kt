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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class QNameAnnotator : QNameAnnotator() {
    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return

        if (element.prefix != null) {
            if (element.prefix !is XdmWildcardValue) {
                val prefix = element.prefix?.element!!
                if (isXmlnsPrefix(element)) {
                    XQuerySemanticHighlighter.highlight(prefix, XQuerySyntaxHighlighterColors.ATTRIBUTE, holder)
                } else {
                    XQuerySemanticHighlighter.highlight(prefix, XQuerySyntaxHighlighterColors.NS_PREFIX, holder)
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
            val elementHighlight = XQuerySemanticHighlighter.getElementHighlighting(localName)
            when {
                highlight !== elementHighlight -> {
                    XQuerySemanticHighlighter.highlight(localName, highlight, holder)
                }
                localName.elementType is IKeywordOrNCNameType && highlight !== elementHighlight -> {
                    XQuerySemanticHighlighter.highlight(localName, XQuerySyntaxHighlighterColors.IDENTIFIER, holder)
                }
            }
        }
    }
}
