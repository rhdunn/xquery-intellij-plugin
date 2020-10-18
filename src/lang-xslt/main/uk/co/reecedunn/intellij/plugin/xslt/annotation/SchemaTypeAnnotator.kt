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
package uk.co.reecedunn.intellij.plugin.xslt.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaListType
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltHashedKeyword
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypes
import uk.co.reecedunn.intellij.plugin.xslt.schema.*
import java.lang.UnsupportedOperationException

class SchemaTypeAnnotator(val schemaType: ISchemaType? = null) : Annotator() {
    companion object {
        val REMOVE_START = "^(Plugin|Scripting|UpdateFacility|XPath|XQuery|Xslt)".toRegex()
        val REMOVE_END = "(PsiImpl|Impl)$".toRegex()
    }

    fun getSymbolName(element: PsiElement): String {
        return element.javaClass.name.split('.').last().replace(REMOVE_START, "").replace(REMOVE_END, "")
    }

    fun accept(schemaType: ISchemaType, element: PsiElement): Boolean = when (schemaType) {
        XslAccumulatorNames -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_ALL
            else -> true
        }
        XslDefaultModeType -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_UNNAMED
            else -> true
        }
        XslEQName, XslEQNames -> element !is XsltHashedKeyword
        XslItemType -> element !is XPathSequenceType
        XslMode -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_CURRENT -> true
                XPathTokenType.K_DEFAULT -> true
                XPathTokenType.K_UNNAMED -> true
                else -> false
            }
            else -> true
        }
        XslModes -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_ALL -> true
                XPathTokenType.K_DEFAULT -> true
                XPathTokenType.K_UNNAMED -> true
                else -> false
            }
            else -> true
        }
        XslPrefixes, XslTokens -> when (element) {
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            else -> false
        }
        XslPrefix, XslPrefixList, XslPrefixOrDefault -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_DEFAULT
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            else -> false
        }
        XslPrefixListOrAll -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_ALL -> true
                XPathTokenType.K_DEFAULT -> true
                else -> false
            }
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            else -> false
        }
        XslQNames -> when (element) {
            is XPathNCName -> true
            is XPathQName -> true
            is PsiWhiteSpace -> true
            else -> false
        }
        XslSequenceType -> true
        else -> throw UnsupportedOperationException()
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiFile) return
        val schemaType = schemaType ?: XsltSchemaTypes.create(element) ?: return

        var itemCount = 0
        element.children().forEach { child ->
            if (child is XsltHashedKeyword) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(child)
                    .textAttributes(XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
                    .create()
            }

            if (accept(schemaType, child)) {
                if (child !is PsiWhiteSpace) {
                    itemCount += 1
                    if (itemCount > 1 && schemaType !is ISchemaListType) {
                        val message = XsltBundle.message("schema.validation.multiple-items", schemaType.type)
                        holder.newAnnotation(HighlightSeverity.ERROR, message).range(child).create()
                    }
                }
            } else {
                val symbol = getSymbolName(child)
                val message =
                    if (child is XsltHashedKeyword)
                        XsltBundle.message("schema.validation.unsupported-keyword", child.text, schemaType.type)
                    else
                        XsltBundle.message("schema.validation.unsupported", symbol, schemaType.type)
                holder.newAnnotation(HighlightSeverity.ERROR, message).range(child).create()
            }
        }
    }
}
