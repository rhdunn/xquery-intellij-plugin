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

import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessState
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonTraceListener

class SaxonDebugTraceListener : SaxonTraceListener(), DebugSession {
    // region DebugSession

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

    private fun checkIsSuspended() {
        if (state === QueryProcessState.Suspending) {
            state = QueryProcessState.Suspended
            listener?.onsuspended(SaxonSuspendContext())
        }

        while (state === QueryProcessState.Suspended) {
            Thread.sleep(100)
        }
    }

    // endregion
    // region TraceListener

    override fun enter(instruction: InstructionInfo, context: Any) {
        super.enter(instruction, context)
        checkIsSuspended()
    }

    override fun leave(instruction: InstructionInfo) {
        super.leave(instruction)
        checkIsSuspended()
    }

    // endregion
}
