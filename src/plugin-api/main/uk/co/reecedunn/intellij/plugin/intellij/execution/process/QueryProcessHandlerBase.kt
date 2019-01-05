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
import com.intellij.execution.process.ProcessOutputTypes
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import java.io.OutputStream
import java.lang.ref.WeakReference

abstract class QueryProcessHandlerBase : ProcessHandler() {
    // region Profile Report

    private var mQueryResultListener: WeakReference<QueryResultListener>? = null
    var queryResultListener: QueryResultListener?
        get() = mQueryResultListener?.get()
        set(value) {
            mQueryResultListener = value?.let { WeakReference(it) }
        }

    fun notifyException(e: Throwable) {
        queryResultListener?.onException(e) ?: {
            e.message?.let { notifyTextAvailable("$it\n", ProcessOutputTypes.STDOUT) }
        }()
    }

    fun notifyResult(result: QueryResult) {
        queryResultListener?.onQueryResult(result) ?: {
            notifyTextAvailable("----- ${result.type} [${result.mimetype}]\n", ProcessOutputTypes.STDOUT)
            notifyTextAvailable("${result.value}\n", ProcessOutputTypes.STDOUT)
        }()
    }

    // endregion
    // region ProcessHandler

    override fun getProcessInput(): OutputStream? = null

    override fun detachIsDefault(): Boolean = false

    override fun detachProcessImpl() {}

    override fun destroyProcessImpl() {}

    // endregion
}
