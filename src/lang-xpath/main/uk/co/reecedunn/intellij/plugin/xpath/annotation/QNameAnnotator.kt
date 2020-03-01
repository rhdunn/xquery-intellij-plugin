/*
 * Copyright (C) 2016, 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

class QNameAnnotator : Annotator() {
    private fun getHighlightAttributes(element: PsiElement): TextAttributesKey {
        return when (element.getUsageType()) {
            XstUsageType.Attribute -> XPathSyntaxHighlighterColors.ATTRIBUTE
            else -> XPathSyntaxHighlighterColors.IDENTIFIER
        }
    }

    private fun checkQNameWhitespaceBefore(qname: XsQNameValue, separator: PsiElement, holder: AnnotationHolder) {
        val before = separator.prevSibling
        if (
            before.node.elementType === XPathTokenType.WHITE_SPACE ||
            before.node.elementType === XPathElementType.COMMENT
        ) {
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
        if (
            after.node.elementType === XPathTokenType.WHITE_SPACE ||
            after.node.elementType === XPathElementType.COMMENT
        ) {
            val message =
                if (qname is XPathWildcard)
                    XPathBundle.message("parser.error.wildcard.whitespace-after-local-part")
                else
                    XPathBundle.message("parser.error.qname.whitespace-after-local-part")
            holder.newAnnotation(HighlightSeverity.ERROR, message).range(after).create()
        }
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return
        if (element.language != XPath) return

        val xmlns: Boolean
        if (element.prefix != null) {
            when {
                element.prefix!!.data == "xmlns" -> xmlns = true
                element.prefix !is XdmWildcardValue -> {
                    xmlns = false
                    val prefix = element.prefix?.element!!
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                        .textAttributes(XPathSyntaxHighlighterColors.NS_PREFIX)
                        .create()
                }
                else -> xmlns = false
            }

            // Detect whitespace errors here instead of the parser so the QName annotator gets run.
            element.children().filterIsElementType(XPathTokenType.QNAME_SEPARATOR).firstOrNull()?.let {
                checkQNameWhitespaceBefore(element, it, holder)
                checkQNameWhitespaceAfter(element, it, holder)
            }
        } else {
            xmlns = false
        }

        if (element.localName != null) {
            val localName = element.localName?.element!!
            val highlight = if (xmlns) XPathSyntaxHighlighterColors.NS_PREFIX else getHighlightAttributes(element)
            when {
                highlight !== XPathSyntaxHighlighterColors.IDENTIFIER -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(highlight)
                        .create()
                }
                localName.node.elementType is IKeywordOrNCNameType -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(XPathSyntaxHighlighterColors.IDENTIFIER)
                        .create()
                }
            }
        }
    }
}
