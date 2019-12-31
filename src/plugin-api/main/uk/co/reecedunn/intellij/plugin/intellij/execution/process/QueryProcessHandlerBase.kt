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
import com.intellij.util.containers.ContainerUtil
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import java.io.OutputStream

abstract class QueryProcessHandlerBase : ProcessHandler() {
    // region Results

    private val queryResultListeners = ContainerUtil.createLockFreeCopyOnWriteList<QueryResultListener>()

    fun addQueryResultListener(listener: QueryResultListener) {
        queryResultListeners.add(listener)
    }

    fun removeQueryResultListener(listener: QueryResultListener) {
        queryResultListeners.add(listener)
    }

    fun notifyBeginResults() {
        queryResultListeners.forEach { it.onBeginResults() }
    }

    fun notifyEndResults() {
        queryResultListeners.forEach { it.onEndResults() }
    }

    fun notifyException(e: Throwable) {
        queryResultListeners.forEach { it.onException(e) }
    }

    fun notifyResult(result: QueryResult) {
        queryResultListeners.forEach { it.onQueryResult(result) }
    }

    fun notifyResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
        queryResultListeners.forEach { it.onQueryResultTime(resultTime, time) }
    }

    // endregion
    // region ProcessHandler

    override fun getProcessInput(): OutputStream? = null

    override fun detachIsDefault(): Boolean = false

    override fun detachProcessImpl() {}

    override fun destroyProcessImpl() {}

    // endregion
}
