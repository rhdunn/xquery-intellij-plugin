/*
 * Copyright (C) 2016, 2020-2021 Reece H. Dunn
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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCDataSection
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import kotlin.math.max
import kotlin.math.min

class XQueryCDataSectionPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryCDataSection, PsiElementTextDecoder {
    override fun decode(decoded: StringBuilder) {
        when (isClosed) {
            true -> decoded.append(text.subSequence(9, textLength - 3))
            else -> decoded.append(text.subSequence(9, textLength))
        }
    }

    override fun decode(offset: Int, rangeInsideHost: TextRange, decoded: StringBuilder, offsets: ArrayList<Int>) {
        val endOffset = offset + textLength
        if (rangeInsideHost.endOffset > offset && rangeInsideHost.startOffset < endOffset) {
            val start = max(9, rangeInsideHost.startOffset - offset)
            val end = when (isClosed) {
                true -> min(textLength - 3, rangeInsideHost.endOffset - offset)
                else -> min(textLength, rangeInsideHost.endOffset - offset)
            }

            decoded.append(text.subSequence(start, end))
            (start until end).forEach { offsets.add(it + offset) }
        }
    }

    private val isClosed
        get() = children().find { it.elementType == XQueryTokenType.CDATA_SECTION_END_TAG } != null
}
