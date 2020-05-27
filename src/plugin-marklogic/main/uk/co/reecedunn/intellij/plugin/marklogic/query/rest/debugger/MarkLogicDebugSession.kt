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

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicQueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessState
import java.lang.RuntimeException

internal class MarkLogicDebugSession(
    private val processor: MarkLogicQueryProcessor,
    private val query: VirtualFile
) : XDebuggerEvaluator(), DebugSession {
    private var state: QueryProcessState = QueryProcessState.Starting
    private var requestId: String? = null

    // region XDebuggerEvaluator

    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Value, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.bindVariable("expression", expression, "xs:string")
        try {
            val results = query.run().results
            callback.evaluated(MarkLogicValue(results))
        } catch (e: Throwable) {
            e.message?.let {
                callback.errorOccurred(it)
            }
        }
    }

    // endregion
    // region DebugSession

    override fun getBreakpointHandlers(language: Language): Array<XBreakpointHandler<*>> {
        return arrayOf()
    }

    override var listener: DebugSessionListener? = null

    override fun suspend() {
        if (state === QueryProcessState.Running) {
            state = QueryProcessState.Suspending

            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Break, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            query.run()
        }
    }

    override fun resume() {
        if (state === QueryProcessState.Suspended) {
            state = QueryProcessState.Resuming

            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Continue, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            query.run()
        }
    }

    override val stackFrames: List<XStackFrame>
        get() {
            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Stack, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")

            val stack = XmlDocument.parse(query.run().results.first().value as String, DBG_STACK_NAMESPACES)
            return stack.root.children("dbg:frame").map {
                MarkLogicDebugFrame.create(it, this.query, this)
            }.toList()
        }

    // endregion

    fun run(requestId: String) {
        this.requestId = requestId
        state = QueryProcessState.Suspended // MarkLogic requests are suspended at the start of the first expression.

        resume()
        while (state !== QueryProcessState.Stopped) {
            Thread.sleep(100)

            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Status, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            state = when (val status = query.run().results.first().value as String) {
                "none" -> QueryProcessState.Stopped
                "running" -> QueryProcessState.Running
                "stopped" -> {
                    if (state !== QueryProcessState.Suspended) {
                        state = QueryProcessState.Suspended
                        listener?.onsuspended(this.query.name)
                    }
                    QueryProcessState.Suspended
                }
                else -> throw RuntimeException(status)
            }
        }
    }

    fun stop() {
        val query = processor.createRunnableQuery(MarkLogicQueries.Request.Cancel, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.run()
    }

    companion object {
        private val DBG_STACK_NAMESPACES = mapOf("dbg" to "http://marklogic.com/xdmp/debug")
    }
}
