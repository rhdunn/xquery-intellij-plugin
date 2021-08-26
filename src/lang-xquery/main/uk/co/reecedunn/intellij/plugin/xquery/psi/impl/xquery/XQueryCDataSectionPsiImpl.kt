/*
 * Copyright (C) 2016, 2020 Reece H. Dunn
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
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCDataSection
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryCDataSectionPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryCDataSection {
    // region PsiLanguageInjectionHost

    private class LiteralTextEscaperImpl(host: XQueryCDataSectionPsiImpl) :
        LiteralTextEscaper<XQueryCDataSectionPsiImpl>(host) {

        private var decoded: Array<Int>? = null

        override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
            outChars.append(rangeInsideHost.subSequence(myHost.text))
            decoded = Array(rangeInsideHost.length + 1) { it + rangeInsideHost.startOffset }
            return true
        }

        override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int = when {
            offsetInDecoded < 0 -> -1
            offsetInDecoded >= decoded!!.size -> -1
            else -> decoded!![offsetInDecoded]
        }

        override fun getRelevantTextRange(): TextRange = when {
            myHost.isClosed -> TextRange(9, myHost.textLength - 3)
            else -> TextRange(9, myHost.textLength)
        }

        override fun isOneLine(): Boolean = false
    }

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val updated = createElement<XQueryCDataSection>("<![CDATA[$text]]>") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    private val isClosed
        get() = children().find { it.elementType == XQueryTokenType.CDATA_SECTION_END_TAG } != null

    // endregion
}
