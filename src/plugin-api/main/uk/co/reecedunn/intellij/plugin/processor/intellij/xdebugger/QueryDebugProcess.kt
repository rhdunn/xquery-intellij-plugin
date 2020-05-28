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
package uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger

import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.lang.Language
import com.intellij.openapi.application.ModalityState
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations.QueryProcessorRunState
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.evaluation.QueryEditorsProvider
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.QuerySuspendContext
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQuery

class QueryDebugProcess(
    session: XDebugSession,
    private val language: Language,
    private val state: QueryProcessorRunState
) : XDebugProcess(session), DebugSessionListener {
    private val editorsProvider: XDebuggerEditorsProvider = QueryEditorsProvider(language)
    private val query: DebuggableQuery = state.createQuery() as DebuggableQuery
    private val debugger: DebugSession = query.session

    init {
        debugger.listener = this
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider = editorsProvider

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = debugger.getBreakpointHandlers(language)

    override fun createConsole(): ExecutionConsole {
        val console = state.createConsole(DefaultDebugExecutor.getDebugExecutorInstance())
        console.attachToProcess(processHandler)
        return console
    }

    override fun doGetProcessHandler(): ProcessHandler? = state.createProcessHandler(query)

    override fun stop() {
        debugger.listener = null
    }

    override fun startPausing() = debugger.suspend()

    override fun resume(context: XSuspendContext?) = debugger.resume()

    override fun onsuspended(displayName: String) {
        invokeLater(ModalityState.defaultModalityState()) {
            session.positionReached(QuerySuspendContext(displayName, debugger))
        }
    }
}
