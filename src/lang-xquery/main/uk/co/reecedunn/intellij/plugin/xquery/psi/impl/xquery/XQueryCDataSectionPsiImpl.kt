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
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextEscaperImpl
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextHost
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCDataSection
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryCDataSectionPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryCDataSection, LiteralTextHost {
    // region PsiLanguageInjectionHost

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val updated = createElement<XQueryCDataSection>("<![CDATA[$text]]>") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return object : LiteralTextEscaperImpl<XQueryCDataSectionPsiImpl>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.subSequence(text))
                decoded = Array(rangeInsideHost.length + 1) { it + rangeInsideHost.startOffset }
                return true
            }
        }
    }

    // endregion
    // region LiteralTextHost

    private val isClosed = children().find { it.elementType == XQueryTokenType.CDATA_SECTION_END_TAG } != null

    override val isOneLine: Boolean = false

    override val relevantTextRange: TextRange
        get() = when {
            isClosed -> TextRange(9, textLength - 3)
            else -> TextRange(9, textLength)
        }

    override val decoded: String
        get() = relevantTextRange.substring(text)

    // endregion
}
