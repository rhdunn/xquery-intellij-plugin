/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.process

import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.Disposable
import uk.co.reecedunn.intellij.plugin.core.event.Multicaster
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import java.io.OutputStream

abstract class QueryProcessHandlerBase : ProcessHandler() {
    // region Profile Report

    private val queryResultListeners = Multicaster(QueryResultListener::class.java)

    fun addQueryResultListener(listener: QueryResultListener) {
        queryResultListeners.addListener(listener)
    }

    fun addQueryResultListener(listener: QueryResultListener, parentDisposable: Disposable) {
        queryResultListeners.addListener(listener, parentDisposable)
    }

    fun removeQueryResultListener(listener: QueryResultListener) {
        queryResultListeners.removeListener(listener)
    }

    fun notifyBeginResults() {
        queryResultListeners.eventMulticaster.onBeginResults()
    }

    fun notifyEndResults() {
        queryResultListeners.eventMulticaster.onEndResults()
    }

    fun notifyException(e: Throwable) {
        queryResultListeners.eventMulticaster.onException(e)
    }

    fun notifyResult(result: QueryResult) {
        queryResultListeners.eventMulticaster.onQueryResult(result)
    }

    // endregion
    // region ProcessHandler

    override fun getProcessInput(): OutputStream? = null

    override fun detachIsDefault(): Boolean = false

    override fun detachProcessImpl() {}

    override fun destroyProcessImpl() {}

    // endregion
}
