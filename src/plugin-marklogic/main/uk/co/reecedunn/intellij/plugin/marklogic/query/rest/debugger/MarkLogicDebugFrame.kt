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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValueChildrenList
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.ComputeChildren
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri

class MarkLogicDebugFrame private constructor(private val frame: XmlElement) : ComputeChildren {
    override fun computeChildren(node: XCompositeNode, evaluator: XDebuggerEvaluator?) {
        node.addChildren(computeVariables("dbg:global-variables", "dbg:global-variable", evaluator), false)
        node.addChildren(computeVariables("dbg:external-variables", "dbg:external-variable", evaluator), false)
        node.addChildren(computeVariables("dbg:variables", "dbg:variable", evaluator), true)
    }

    private fun computeVariables(list: String, child: String, evaluator: XDebuggerEvaluator?): XValueChildrenList {
        val children = XValueChildrenList()
        frame.children(list).children(child).forEach { variable ->
            children.add(MarkLogicVariable.create(variable, evaluator))
        }
        return children
    }

    companion object {
        fun create(frame: XmlElement, queryFile: VirtualFile, evaluator: XDebuggerEvaluator) : XStackFrame {
            val path = frame.child("dbg:uri")?.text()?.nullize()
            val line = (frame.child("dbg:line")?.text()?.toIntOrNull() ?: 1) - 1
            val column = frame.child("dbg:column")?.text()?.toIntOrNull() ?: 0
            val context = frame.child("dbg:operation")?.text()?.nullize()
            val children = MarkLogicDebugFrame(frame)
            return when (path) {
                null -> VirtualFileStackFrame(queryFile, line, column, context, children, evaluator)
                else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line, column, context, children, evaluator)
            }
        }
    }
}
