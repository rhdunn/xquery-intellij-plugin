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

import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicQueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSessionListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessState

internal class MarkLogicDebugSession(private val processor: MarkLogicQueryProcessor) : DebugSession {
    private var state: QueryProcessState = QueryProcessState.Starting
    private var requestId: String? = null

    override var listener: DebugSessionListener? = null

    override fun suspend() {
    }

    override fun resume() {
        if (state === QueryProcessState.Suspended) {
            val query = processor.createRunnableQuery(MarkLogicQueries.Debug.Continue, XQuery)
            query.bindVariable("requestId", requestId, "xs:unsignedLong")
            query.run()
        }
    }

    fun run(requestId: String) {
        this.requestId = requestId
        state = QueryProcessState.Suspended // MarkLogic requests are suspended at the start of the first expression.

        resume()
        while (state !== QueryProcessState.Stopped) {
            Thread.sleep(100)
        }
    }

    fun stop() {
        val query = processor.createRunnableQuery(MarkLogicQueries.Request.Cancel, XQuery)
        query.bindVariable("requestId", requestId, "xs:unsignedLong")
        query.run()

        state = QueryProcessState.Stopped
    }
}
