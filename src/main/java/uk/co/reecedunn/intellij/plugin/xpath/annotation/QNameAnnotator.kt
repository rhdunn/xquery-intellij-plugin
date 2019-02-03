/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighterColors

class QNameAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XPathEQName) return

        val qname = element as XsQNameValue
        val xmlns: Boolean
        if (qname.prefix != null) {
            if (qname.prefix!!.data == "xmlns") {
                xmlns = true
            } else {
                xmlns = false
                val prefix = qname.prefix?.element!!
                holder.createInfoAnnotation(prefix, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                if (element.getParent() is PluginDirAttribute || element.getParent() is XQueryDirElemConstructor) {
                    holder.createInfoAnnotation(prefix, null).textAttributes = XQuerySyntaxHighlighterColors.XML_TAG
                }
                holder.createInfoAnnotation(prefix, null).textAttributes = XQuerySyntaxHighlighterColors.NS_PREFIX
            }
        } else {
            xmlns = false
        }

        if (qname.localName != null) {
            val localName = qname.localName?.element!!
            if (xmlns) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                if (element.getParent() is PluginDirAttribute) {
                    holder.createInfoAnnotation(localName, null).textAttributes = XQuerySyntaxHighlighterColors.XML_TAG
                }
                holder.createInfoAnnotation(localName, null).textAttributes = XQuerySyntaxHighlighterColors.NS_PREFIX
            } else if (element.parent is XQueryAnnotation) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                holder.createInfoAnnotation(localName, null).textAttributes = XQuerySyntaxHighlighterColors.ANNOTATION
            } else if (localName.node.elementType is IKeywordOrNCNameType) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                holder.createInfoAnnotation(localName, null).textAttributes = XQuerySyntaxHighlighterColors.IDENTIFIER
            } else if (localName is XPathNCName) {
                if (localName.node.elementType is IKeywordOrNCNameType) {
                    holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                    holder.createInfoAnnotation(localName, null).textAttributes = XQuerySyntaxHighlighterColors.IDENTIFIER
                }
            }
        }
    }
}
