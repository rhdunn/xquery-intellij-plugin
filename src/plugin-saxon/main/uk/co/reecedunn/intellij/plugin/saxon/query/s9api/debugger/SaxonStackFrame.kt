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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.ComputeChildren
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.expr.XPathContext
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri
import javax.xml.transform.SourceLocator

class SaxonStackFrame private constructor(private val xpathContext: XPathContext?) : ComputeChildren {
    override fun computeChildren(node: XCompositeNode, evaluator: XDebuggerEvaluator?) {
        val frame = xpathContext?.getStackFrame() ?: return
    }

    companion object {
        fun create(locator: SourceLocator, xpathContext: XPathContext?, queryFile: VirtualFile): XStackFrame {
            val path = locator.systemId
            val line = locator.lineNumber.let { if (it == -1) 1 else it } - 1
            val column = locator.columnNumber.let { if (it == -1) 1 else it } - 1
            val context = (locator as? InstructionInfo)?.getObjectName()?.toString()
            val children = SaxonStackFrame(xpathContext)
            return when (path) {
                null -> VirtualFileStackFrame(queryFile, line, column, context, children, null)
                else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line, column, context, children, null)
            }
        }
    }
}
