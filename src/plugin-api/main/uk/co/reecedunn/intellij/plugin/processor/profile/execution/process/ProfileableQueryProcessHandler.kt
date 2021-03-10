/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.profile.execution.process

import com.intellij.openapi.application.ModalityState
import com.intellij.util.containers.ContainerUtil
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.run.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.ProcessTerminatedException
import uk.co.reecedunn.intellij.plugin.processor.query.StoppableQuery

class ProfileableQueryProcessHandler(private val query: ProfileableQuery) : QueryProcessHandlerBase() {
    // region Profile Report

    private val profileReportListeners = ContainerUtil.createLockFreeCopyOnWriteList<ProfileReportListener>()

    fun addProfileReportListener(listener: ProfileReportListener) {
        profileReportListeners.add(listener)
    }

    fun removeProfileReportListener(listener: ProfileReportListener) {
        profileReportListeners.remove(listener)
    }

    private fun notifyProfileReport(report: FlatProfileReport) {
        profileReportListeners.forEach { it.onProfileReport(report) }
    }

    // endregion
    // region ProcessHandler

    override fun startNotify() {
        super.startNotify()

        notifyBeginResults()
        executeOnPooledThread {
            try {
                val results = query.profile()
                invokeLater(ModalityState.defaultModalityState()) {
                    try {
                        notifyProfileReport(results.report)
                        results.results.forEach { result -> notifyResult(result) }
                        notifyElapsedTime(results.report.elapsed)
                    } catch (e: Throwable) {
                        if (e !is ProcessTerminatedException) {
                            notifyException(e)
                        }
                    } finally {
                        notifyEndResults()
                        notifyProcessDetached()
                    }
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.defaultModalityState()) {
                    if (e !is ProcessTerminatedException) {
                        notifyException(e)
                    }
                    notifyEndResults()
                    notifyProcessDetached()
                }
            }

        }
    }

    override fun destroyProcessImpl() {
        if (query is StoppableQuery) {
            query.stop()
        }
    }

    // endregion
}
