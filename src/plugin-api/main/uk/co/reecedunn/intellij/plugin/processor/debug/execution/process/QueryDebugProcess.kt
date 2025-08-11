// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.debug.execution.process

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
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQuery
import uk.co.reecedunn.intellij.plugin.processor.debug.StepAction
import uk.co.reecedunn.intellij.plugin.processor.debug.evaluation.QueryEditorsProvider
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.RunProfileStateEx

class QueryDebugProcess(
    session: XDebugSession,
    language: Language,
    private val state: RunProfileStateEx
) : XDebugProcess(session), DebugSessionListener {
    private val editorsProvider: XDebuggerEditorsProvider = QueryEditorsProvider(language)
    private val query: DebuggableQuery = state.createQuery() as DebuggableQuery
    private val debugger: DebugSession = query.session
    private val breakpointHandlers: Array<XBreakpointHandler<*>> = debugger.getBreakpointHandlers(language, session.project)

    init {
        debugger.listener = this
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider = editorsProvider

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = breakpointHandlers

    override fun createConsole(): ExecutionConsole {
        val console = state.createConsole(DefaultDebugExecutor.getDebugExecutorInstance())
        console.attachToProcess(processHandler)
        return console
    }

    override fun doGetProcessHandler(): ProcessHandler = state.createProcessHandler(query)

    override fun stop() {
        debugger.listener = null
    }

    override fun startPausing(): Unit = debugger.suspend()

    override fun resume(context: XSuspendContext?): Unit = debugger.resume()

    override fun startStepInto(context: XSuspendContext?): Unit = debugger.step(StepAction.Into)

    override fun startStepOver(context: XSuspendContext?): Unit = debugger.step(StepAction.Over)

    override fun startStepOut(context: XSuspendContext?): Unit = debugger.step(StepAction.Out)

    override fun positionReached() {
        invokeLater(ModalityState.defaultModalityState()) {
            session.positionReached(debugger.suspendContext)
        }
    }
}
