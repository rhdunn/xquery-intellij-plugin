/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.psi.impl.error

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.ILogLevelElementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyAtomicType
import java.util.*

class MarkLogicErrorLogLinePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), MarkLogicErrorLogLine {
    companion object {
        private val LOG_LEVEL = Key.create<Optional<PsiElement>>("LOG_LEVEL")
    }

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(LOG_LEVEL)
    }

    override val logLevel: PsiElement?
        get() = computeUserDataIfAbsent(LOG_LEVEL) {
            Optional.ofNullable(children().find { it.elementType is ILogLevelElementType })
        }.orElse(null)
}
