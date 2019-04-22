/*
 * Copyright (C) 2019 Reece H. Dunn
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

import com.intellij.openapi.Disposable
import uk.co.reecedunn.intellij.plugin.core.event.Multicaster
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery

class ProfileableQueryProcessHandler(private val query: ProfileableQuery) : QueryProcessHandlerBase() {
    // region Profile Report

    private val profileReportListeners = Multicaster(ProfileReportListener::class.java)

    fun addProfileReportListener(listener: ProfileReportListener) {
        profileReportListeners.addListener(listener)
    }

    fun addProfileReportListener(listener: ProfileReportListener, parentDisposable: Disposable) {
        profileReportListeners.addListener(listener, parentDisposable)
    }

    fun removeProfileReportListener(listener: ProfileReportListener) {
        profileReportListeners.removeListener(listener)
    }

    fun notifyProfileReport(report: ProfileReport) {
        profileReportListeners.eventMulticaster.onProfileReport(report)
    }

    // endregion
    // region ProcessHandler

    override fun startNotify() {
        super.startNotify()
        try {
            notifyBeginResults()
            query.profile().execute { results ->
                try {
                    notifyProfileReport(results.report)
                    results.results.forEach { result -> notifyResult(result) }
                    notifyResultTime(QueryResultTime.Elapsed, results.report.elapsed)
                } catch (e: Throwable) {
                    notifyException(e)
                } finally {
                    notifyEndResults()
                    notifyProcessDetached()
                }
            }.onException { e ->
                notifyException(e)
                notifyEndResults()
                notifyProcessDetached()
            }
        } catch(e: Throwable) {
            notifyException(e)
            notifyEndResults()
            notifyProcessDetached()
        }
    }

    // endregion
}
