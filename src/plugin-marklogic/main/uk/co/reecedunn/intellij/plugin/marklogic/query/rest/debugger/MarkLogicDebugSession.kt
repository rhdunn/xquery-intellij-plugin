// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger

import com.intellij.lang.Language
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicQueryProcessor
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.breakpoints.MarkLogicXQueryBreakpointHandler
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.debug.StepAction
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.QueryResultsValue
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.QuerySuspendContext
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessState
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.xdebugger.breakpoints.XQueryExpressionBreakpointType
import java.lang.ref.WeakReference

internal class MarkLogicDebugSession(
    private val processor: MarkLogicQueryProcessor,
    private val query: VirtualFile
) : XDebuggerEvaluator(), DebugSession {
    private var modulePath: String = "/"
    private var state: QueryProcessState = QueryProcessState.Starting
    private var requestId: String? = null
    private var breakpointHandlers: Array<XBreakpointHandler<*>> = arrayOf()

    // region XDebuggerEvaluator

    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Value, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.bindVariable("expression", expression, "xs:string")
        try {
            val results = query.run().results
            callback.evaluated(QueryResultsValue(results))
        } catch (e: Throwable) {
            e.message?.let {
                callback.errorOccurred(it)
            }
        }
    }

    // endregion
    // region DebugSession

    override fun getBreakpointHandlers(language: Language): Array<XBreakpointHandler<*>> {
        breakpointHandlers = arrayOf(
            MarkLogicXQueryBreakpointHandler(XQueryExpressionBreakpointType::class.java, WeakReference(this))
        )
        return breakpointHandlers
    }

    override var listener: DebugSessionListener? = null

    private fun performAction(queryFile: VirtualFile, ifState: QueryProcessState, nextState: QueryProcessState) {
        if (state === ifState) {
            state = QueryProcessState.UpdatingState

            val query = processor.createRunnableQuery(queryFile, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            query.run()

            state = nextState
        }
    }

    override fun suspend() {
        performAction(MarkLogicQueries.Debug.Break, QueryProcessState.Running, QueryProcessState.Suspending)
    }

    override fun resume() {
        performAction(MarkLogicQueries.Debug.Continue, QueryProcessState.Suspended, QueryProcessState.Resuming)
    }

    override fun step(action: StepAction) {
        val query = when (action) {
            StepAction.Into -> MarkLogicQueries.Debug.StepInto
            StepAction.Over -> MarkLogicQueries.Debug.StepOver
            StepAction.Out -> MarkLogicQueries.Debug.StepOut
        }
        performAction(query, QueryProcessState.Suspended, QueryProcessState.Resuming)
    }

    override val stackFrames: List<XStackFrame>
        get() {
            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Stack, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            val xml = query.run().results.firstOrNull()?.value as? String ?: return listOf()

            val stack = XmlDocument.parse(xml, DBG_STACK_NAMESPACES)
            return stack.root.children("dbg:frame").map {
                MarkLogicDebugFrame.create(it, this.query, this)
            }.toList()
        }

    override val suspendContext: XSuspendContext
        get() = QuerySuspendContext(query.name, this)

    // endregion

    private fun getModuleUri(element: PsiElement): String? {
        val file = element.containingFile.virtualFile
        if (file == query) return ""

        val project = element.project
        val path = XpmModuleLoaderSettings.getInstance(project).relativePathTo(file, project)
        return when {
            path == null -> null
            path.startsWith("/MarkLogic/") -> path
            modulePath.endsWith("/") -> "$modulePath$path"
            else -> "$modulePath/$path"
        }
    }

    private fun updateBreakpoint(uri: String, line: Int, column: Int, register: Boolean): Boolean {
        val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Breakpoint, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.bindVariable("register", register.toString(), "xs:boolean")
        query.bindVariable("exprUri", uri, "xs:string")
        query.bindVariable("exprLine", line.toString(), "xs:nonNegativeInteger")
        query.bindVariable("exprColumn", column.toString(), "xs:nonNegativeInteger")
        return query.run().results.first().value == "true"
    }

    fun updateBreakpoint(element: XpmExpression, register: Boolean, initializing: Boolean = false): Boolean {
        if (!initializing && state === QueryProcessState.Starting) return true

        val uri = getModuleUri(element as PsiElement) ?: return false
        val document = element.containingFile.document ?: return false
        val offset = element.expressionElement?.textOffset ?: return false
        val line = document.getLineNumber(offset)
        val column = offset - document.getLineStartOffset(line)

        val currentState = state
        state = QueryProcessState.UpdatingState

        val ret = updateBreakpoint(uri, line + 1, column, register = register)

        if (state == QueryProcessState.UpdatingState) {
            state = currentState
        }
        return ret
    }

    private fun registerBreakpoints() {
        // Accessing the containing file and associated document need to be
        // accessed via a read action on the EDT thread.
        val xquery = breakpointHandlers.get(0) as? MarkLogicXQueryBreakpointHandler ?: return
        runInEdt {
            runReadAction {
                xquery.expressionBreakpoints.forEach { updateBreakpoint(it, register = true, initializing = true) }

                // MarkLogic requests are suspended at the start of the first expression.
                state = QueryProcessState.Suspended
            }
        }

        // Wait for the breakpoints to be processed.
        while (state !== QueryProcessState.Suspended) {
            Thread.sleep(100)
        }
    }

    fun run(requestId: String) {
        this.requestId = requestId

        registerBreakpoints()
        resume() // MarkLogic requests are suspended at the start of the first expression.
        while (state !== QueryProcessState.Stopped) {
            Thread.sleep(100)

            val newState = when (val status = status()) {
                "none" -> QueryProcessState.Stopped
                "running" -> QueryProcessState.Running
                "stopped" -> QueryProcessState.Suspending
                else -> throw RuntimeException(status)
            }

            if (state !== QueryProcessState.UpdatingState) {
                if (newState === QueryProcessState.Suspending) {
                    if (state !== QueryProcessState.Suspended) {
                        state = QueryProcessState.Suspended
                        listener?.positionReached()
                    }
                } else {
                    state = newState
                }
            }
        }
    }

    private fun status(): String {
        val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Status, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        return query.run().results.first().value as String
    }

    fun stop() {
        state = QueryProcessState.UpdatingState

        val query = processor.createRunnableQuery(MarkLogicQueries.Request.Cancel, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.run()

        state = QueryProcessState.Stopping
    }

    companion object {
        private val DBG_STACK_NAMESPACES = mapOf("dbg" to "http://marklogic.com/xdmp/debug")
    }
}
