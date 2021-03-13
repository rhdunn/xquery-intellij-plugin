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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.ComputeChildren
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.addChildren
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.expr.XPathContext
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri
import javax.xml.transform.SourceLocator

class SaxonStackFrame private constructor(
    private val xpathContext: XPathContext?,
    private val processor: Processor?
) : ComputeChildren {
    override fun computeChildren(node: XCompositeNode, evaluator: XDebuggerEvaluator?) {
        node.addChildren(computeStackVariables(), true)
    }

    private fun computeStackVariables(): Sequence<XNamedValue> {
        val frame = xpathContext!!.getStackFrame()
        val names = frame.getStackFrameMap().getVariableMap()
        return frame.getStackFrameValues().asSequence().withIndex().mapNotNull { (index, value) ->
            SaxonVariable(names[index], value, processor!!)
        }
    }

    companion object {
        fun create(
            locator: SourceLocator,
            xpathContext: XPathContext?,
            processor: Processor?,
            queryFile: VirtualFile
        ): XStackFrame {
            val path = locator.systemId
            val line = locator.lineNumber.let { if (it == -1) 1 else it } - 1
            val column = locator.columnNumber.let { if (it == -1) 1 else it } - 1
            val context = (locator as? InstructionInfo)?.getObjectName()?.toString()
            val children = SaxonStackFrame(xpathContext, processor)
            val file = when {
                path.isNullOrEmpty() -> null
                path.startsWith("file:/") -> LocalFileSystem.getInstance().findFileByPath(path.substring(6))
                else -> XpmModuleUri(queryFile, path)
            }
            return VirtualFileStackFrame(file ?: queryFile, line, column, context, children, null)
        }
    }
}
