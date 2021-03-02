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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.ILogLevelElementType

class MarkLogicErrorLogLinePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), MarkLogicErrorLogLine {
    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedLogLevel.invalidate()
    }

    private val cachedLogLevel = CacheableProperty {
        children().find { it.elementType is ILogLevelElementType }?.elementType as? ILogLevelElementType
    }

    override val logLevel: ILogLevelElementType?
        get() = cachedLogLevel.get()
}
