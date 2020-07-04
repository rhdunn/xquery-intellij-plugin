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

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.debug.StepAction
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.QuerySuspendContext
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessState
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonTraceListener
import java.util.*

class SaxonDebugTraceListener(val query: VirtualFile) : SaxonTraceListener(), DebugSession {
    // region DebugSession

    override fun getBreakpointHandlers(language: Language): Array<XBreakpointHandler<*>> {
        return arrayOf()
    }

    override var listener: DebugSessionListener? = null

    override fun suspend() {
        if (state === QueryProcessState.Running) {
            state = QueryProcessState.Suspending
        }
    }

    override fun resume() {
        if (state === QueryProcessState.Suspended || state === QueryProcessState.Suspending) {
            state = QueryProcessState.Running
        }
    }

    override fun step(action: StepAction) {
    }

    override val stackFrames: List<XStackFrame> get() = currentStackFrames.asReversed()

    override val suspendContext: XSuspendContext get() = QuerySuspendContext(query.name, this)

    private fun checkIsSuspended() {
        if (state === QueryProcessState.Suspending) {
            state = QueryProcessState.Suspended
            listener?.positionReached()
        }

        while (state === QueryProcessState.Suspended) {
            Thread.sleep(100)
        }
    }

    // endregion
    // region TraceListener

    private val currentStackFrames: Stack<XStackFrame> = Stack()

    override fun enter(instruction: InstructionInfo, properties: Map<String, Any>, context: Any) {
        super.enter(instruction, properties, context)

        currentStackFrames.push(SaxonStackFrame.create(instruction, query))
        checkIsSuspended()
    }

    override fun leave(instruction: InstructionInfo) {
        super.leave(instruction)

        currentStackFrames.pop()
        checkIsSuspended()
    }

    // endregion
}
