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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor
import kotlin.collections.ArrayList

class PluginDirTextConstructorPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginDirTextConstructor {
    companion object {
        private val STRING_VALUE = Key.create<String>("STRING_VALUE")
    }
    // region PsiLanguageInjectionHost

    private class LiteralTextEscaperImpl(host: PluginDirTextConstructorPsiImpl) :
        LiteralTextEscaper<PluginDirTextConstructorPsiImpl>(host) {

        private var decoded: Array<Int>? = null

        override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
            var currentOffset = 0
            val offsets = ArrayList<Int>()
            myHost.children().forEach { child ->
                if (child is PsiElementTextDecoder) {
                    child.decode(currentOffset, rangeInsideHost, outChars, offsets)
                }
                currentOffset += child.textLength
            }
            offsets.add(rangeInsideHost.endOffset)
            decoded = offsets.toTypedArray()
            return true
        }

        override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int = when {
            offsetInDecoded < 0 -> -1
            offsetInDecoded >= decoded!!.size -> -1
            else -> decoded!![offsetInDecoded]
        }

        override fun getRelevantTextRange(): TextRange = TextRange(0, myHost.textLength)

        override fun isOneLine(): Boolean = false
    }

    private fun encoded(text: String): String {
        val out = StringBuilder()
        text.forEach { c ->
            when (c) {
                '{' -> out.append("{{")
                '}' -> out.append("}}")
                '&' -> out.append("&amp;")
                '<' -> out.append("&lt;")
                '>' -> out.append("&gt;")
                else -> out.append(c)
            }
        }
        return out.toString()
    }

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val updated = createElement<PluginDirTextConstructor>("<a>${encoded(text)}</a>") ?: return this
        return replace(updated) as PsiLanguageInjectionHost
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    // endregion
    // region XdmTextNode

    override val stringValue: String?
        get() = computeUserDataIfAbsent(STRING_VALUE) {
            val value = StringBuilder()
            children().filterIsInstance<PsiElementTextDecoder>().forEach { it.decode(value) }
            value.toString()
        }

    // endregion
}
