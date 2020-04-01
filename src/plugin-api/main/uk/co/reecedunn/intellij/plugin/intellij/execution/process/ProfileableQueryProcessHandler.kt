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

import com.intellij.openapi.application.ModalityState
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery

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
                var file: PsiFile? = null
                val results = query.profile()
                invokeLater(ModalityState.defaultModalityState()) {
                    try {
                        notifyProfileReport(results.report)
                        if (results.results.size == 1) {
                            notifyResult(results.results.first(), isSingleResult = true)
                        } else {
                            results.results.forEach { result -> notifyResult(result, isSingleResult = false) }
                        }
                        notifyResultTime(QueryResultTime.Elapsed, results.report.elapsed)
                    } catch (e: Throwable) {
                        notifyException(e)
                    } finally {
                        file = notifyEndResults()
                        notifyProcessDetached()
                    }
                }
                if (file != null) {
                    notifyQueryResultsPsiFile(file!!)
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

    // endregion
}
