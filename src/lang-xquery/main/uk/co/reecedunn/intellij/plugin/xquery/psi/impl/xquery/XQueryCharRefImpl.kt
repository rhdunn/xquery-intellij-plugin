/*
 * Copyright (C) 2016-2017, 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.lexer.entityReferenceCodePoint
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCharRef
import xqt.platform.xml.model.XmlChar

class XQueryCharRefImpl(type: IElementType, text: CharSequence) :
    LeafPsiElement(type, text), XQueryCharRef, PsiElementTextDecoder {

    override val codepoint: XmlChar
        get() = XmlChar(node.chars.entityReferenceCodePoint())

    override fun decode(decoded: StringBuilder) {
        decoded.append(codepoint.toString())
    }

    override fun decode(offset: Int, rangeInsideHost: TextRange, decoded: StringBuilder, offsets: ArrayList<Int>) {
        if (offset >= rangeInsideHost.startOffset && offset + textLength <= rangeInsideHost.endOffset) {
            val text = codepoint.toString()
            decoded.append(text)
            repeat(text.length) { offsets.add(offset) }
        }
    }
}
