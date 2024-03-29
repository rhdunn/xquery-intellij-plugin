/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.lang.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.schema.ISchemaListType
import uk.co.reecedunn.intellij.plugin.xdm.schema.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltHashedKeyword
import uk.co.reecedunn.intellij.plugin.xslt.resources.XsltBundle
import uk.co.reecedunn.intellij.plugin.xslt.schema.XsltSchemaTypes

class SchemaTypeAnnotator(val schemaType: ISchemaType? = null) : Annotator {
    companion object {
        @Suppress("RegExpAnonymousGroup")
        val REMOVE_START: Regex = "^(Plugin|Scripting|UpdateFacility|XPath|XQuery|Xslt)".toRegex()

        @Suppress("RegExpAnonymousGroup")
        val REMOVE_END: Regex = "(PsiImpl|Impl)$".toRegex()
    }

    private fun getSymbolName(element: PsiElement): String {
        return element.javaClass.name.split('.').last().replace(REMOVE_START, "").replace(REMOVE_END, "")
    }

    fun accept(schemaType: ISchemaType, element: PsiElement): Boolean = when (schemaType) {
        XsltSchemaTypes.XslAccumulatorNames -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_ALL
            else -> true
        }
        XsltSchemaTypes.XslDefaultModeType -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_UNNAMED
            else -> true
        }
        XsltSchemaTypes.XslEQName,
        XsltSchemaTypes.XslEQNameInNamespace,
        XsltSchemaTypes.XslEQNames,
        XsltSchemaTypes.XslStreamabilityType ->
            element !is XsltHashedKeyword
        XsltSchemaTypes.XslItemType -> element !is XPathSequenceType
        XsltSchemaTypes.XslMode -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_CURRENT -> true
                XPathTokenType.K_DEFAULT -> true
                XPathTokenType.K_UNNAMED -> true
                else -> false
            }
            else -> true
        }
        XsltSchemaTypes.XslModes -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_ALL -> true
                XPathTokenType.K_DEFAULT -> true
                XPathTokenType.K_UNNAMED -> true
                else -> false
            }
            else -> true
        }
        XsltSchemaTypes.XslPrefixes, XsltSchemaTypes.XslTokens -> when (element) {
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            is PsiErrorElement -> true
            else -> false
        }
        XsltSchemaTypes.XslPrefix, XsltSchemaTypes.XslPrefixList, XsltSchemaTypes.XslPrefixOrDefault -> when (element) {
            is XsltHashedKeyword -> element.keyword === XPathTokenType.K_DEFAULT
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            is PsiErrorElement -> true
            else -> false
        }
        XsltSchemaTypes.XslPrefixListOrAll -> when (element) {
            is XsltHashedKeyword -> when (element.keyword) {
                XPathTokenType.K_ALL -> true
                XPathTokenType.K_DEFAULT -> true
                else -> false
            }
            is XPathNCName -> true
            is PsiWhiteSpace -> true
            is PsiErrorElement -> true
            else -> false
        }
        XsltSchemaTypes.XslMethod, XsltSchemaTypes.XslQName, XsltSchemaTypes.XslQNames -> when (element) {
            is XPathNCName -> true
            is XPathQName -> true
            is PsiWhiteSpace -> true
            is PsiErrorElement -> true
            else -> false
        }
        XsltSchemaTypes.XslSequenceType -> true
        else -> true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
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
                if (child !is PsiWhiteSpace && child !is PsiErrorElement) {
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
