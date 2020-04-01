/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery

class RunnableQueryProcessHandler(private val query: RunnableQuery) : QueryProcessHandlerBase() {
    override fun startNotify() {
        super.startNotify()

        notifyBeginResults()
        executeOnPooledThread {
            try {
                val results = query.run()
                invokeLater(ModalityState.defaultModalityState()) {
                    try {
                        if (results.status.statusCode != 200) {
                            notifyResult(QueryResult.fromItemType(0, results.status.toString(), "http:status-line"))
                        }
                        if (results.results.size == 1) {
                            notifyResult(results.results.first())
                        } else {
                            results.results.forEach { result -> notifyResult(result) }
                        }
                        notifyResultTime(QueryResultTime.Elapsed, results.elapsed)
                    } catch (e: Throwable) {
                        notifyException(e)
                    } finally {
                        val file = notifyEndResults()
                        if (file != null) {
                            notifyQueryResultsPsiFile(file, results.results.size == 1)
                        }
                        notifyProcessDetached()
                    }
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.defaultModalityState()) {
                    notifyException(e)
                    notifyEndResults()
                    notifyProcessDetached()
                }
            }
        }
    }
}
