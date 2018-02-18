/*
 * Copyright (C) 2018 Reece H. Dunn
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
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.lexer.STATE_NCNAME
import uk.co.reecedunn.intellij.plugin.xdm.lexer.XmlSchemaDataTypeLexer
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class StringLiteralAnnotator : Annotator {
    private fun annotateTokens(element: PsiElement, holder: AnnotationHolder, state: Int) {
        var start = 0
        var end: Int = element.textLength
        val offset: Int = element.textOffset

        if (element.firstChild.node.elementType === XQueryTokenType.STRING_LITERAL_START)
            ++start
        if (element.lastChild.node.elementType === XQueryTokenType.STRING_LITERAL_END)
            --end

        val lexer: Lexer = XmlSchemaDataTypeLexer()
        val highlighter = SyntaxHighlighter()
        lexer.start(element.text, start, end, state)
        while (lexer.tokenType !== null) {
            val range = TextRange(lexer.tokenStart + offset, lexer.tokenEnd + offset)
            val attrs = highlighter.getTokenHighlights(lexer.tokenType!!)
            if (attrs.isNotEmpty())
                holder.createInfoAnnotation(range, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
            for (attr in attrs) {
                holder.createInfoAnnotation(range, null).textAttributes = attr
            }
            lexer.advance()
        }
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XPathStringLiteral) return
        if (element.parent is XPathPITest)
            annotateTokens(element, holder, STATE_NCNAME)
    }
}
