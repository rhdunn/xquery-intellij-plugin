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
package uk.co.reecedunn.intellij.plugin.xquery.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
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
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

class QNameAnnotator : Annotator() {
    private fun getHighlightAttributes(element: PsiElement, resolveReferences: Boolean = true): TextAttributesKey {
        return when (element.getUsageType()) {
            XstUsageType.Annotation -> XQuerySyntaxHighlighterColors.ANNOTATION
            XstUsageType.Attribute -> XQuerySyntaxHighlighterColors.ATTRIBUTE
            XstUsageType.DecimalFormat -> XQuerySyntaxHighlighterColors.DECIMAL_FORMAT
            XstUsageType.Element -> XQuerySyntaxHighlighterColors.ELEMENT
            XstUsageType.FunctionDecl -> XQuerySyntaxHighlighterColors.FUNCTION_DECL
            XstUsageType.FunctionRef -> XQuerySyntaxHighlighterColors.FUNCTION_CALL
            XstUsageType.Namespace -> XQuerySyntaxHighlighterColors.NS_PREFIX
            XstUsageType.Option -> XQuerySyntaxHighlighterColors.OPTION
            XstUsageType.Parameter -> XQuerySyntaxHighlighterColors.PARAMETER
            XstUsageType.Pragma -> XQuerySyntaxHighlighterColors.PRAGMA
            XstUsageType.Type -> XQuerySyntaxHighlighterColors.TYPE
            XstUsageType.Variable -> {
                if (resolveReferences)
                    element.reference?.resolve()?.let {
                        getHighlightAttributes(it, false)
                    } ?: XQuerySyntaxHighlighterColors.VARIABLE
                else
                    XQuerySyntaxHighlighterColors.VARIABLE
            }
            XstUsageType.Unknown -> XQuerySyntaxHighlighterColors.IDENTIFIER
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

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return

        if (element.prefix != null) {
            if (element.prefix !is XdmWildcardValue && element.prefix?.data != "xmlns") {
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
