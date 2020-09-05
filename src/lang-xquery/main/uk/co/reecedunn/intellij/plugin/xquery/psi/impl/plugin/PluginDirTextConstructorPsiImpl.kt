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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextEscaperImpl
import uk.co.reecedunn.intellij.plugin.core.lang.injection.LiteralTextHost
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor

class PluginDirTextConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginDirTextConstructor, LiteralTextHost {
    // region PsiLanguageInjectionHost

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        return this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaperImpl(this)
    }

    // endregion
    // region LiteralTextHost

    override val isOneLine: Boolean = false

    override val relevantTextRange: TextRange
        get() = TODO()

    override val decoded: String
        get() = TODO()

    // endregion
}
