/*
 * Copyright (C) 2016 Reece H. Dunn
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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter

class QNameAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XPathEQName) return
        if (element.getParent() is XPathEQName) return
        if (element is XPathVarName) return // TODO: Remove this when VarName no longer implements XPathEQName.

        val qname = (element as XdmStaticValue).staticValue as? QName
        val xmlns: Boolean
        if (qname?.prefix != null) {
            if (qname.prefix.staticValue == "xmlns") {
                xmlns = true
            } else {
                xmlns = false
                val prefix = qname.prefix as PsiElement
                holder.createInfoAnnotation(prefix, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                if (element.getParent() is XQueryDirAttribute || element.getParent() is XQueryDirElemConstructor) {
                    holder.createInfoAnnotation(prefix, null).textAttributes = SyntaxHighlighter.XML_TAG
                }
                holder.createInfoAnnotation(prefix, null).textAttributes = SyntaxHighlighter.NS_PREFIX
            }
        } else {
            xmlns = false
        }

        if (qname?.localName != null) {
            val localName = qname.localName as PsiElement
            if (xmlns) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                if (element.getParent() is XQueryDirAttribute) {
                    holder.createInfoAnnotation(localName, null).textAttributes = SyntaxHighlighter.XML_TAG
                }
                holder.createInfoAnnotation(localName, null).textAttributes = SyntaxHighlighter.NS_PREFIX
            } else if (element.parent is XQueryAnnotation) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                holder.createInfoAnnotation(localName, null).textAttributes = SyntaxHighlighter.ANNOTATION
            } else if (localName.node.elementType is IXQueryKeywordOrNCNameType) {
                holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                holder.createInfoAnnotation(localName, null).textAttributes = SyntaxHighlighter.IDENTIFIER
            } else if (localName is XPathNCName) {
                if (localName.node.elementType is IXQueryKeywordOrNCNameType) {
                    holder.createInfoAnnotation(localName, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
                    holder.createInfoAnnotation(localName, null).textAttributes = SyntaxHighlighter.IDENTIFIER
                }
            }
        }
    }
}
