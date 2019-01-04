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

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import java.io.OutputStream
import java.lang.ref.WeakReference

class ProfileableQueryProcessHandler(private val query: ProfileableQuery) : ProcessHandler() {
    // region Profile Report

    private var mProfileReportListener: WeakReference<ProfileReportListener>? = null
    var profileReportListener: ProfileReportListener?
        get() = mProfileReportListener?.get()
        set(value) {
            mProfileReportListener = value?.let { WeakReference(it) }
        }

    fun notifyProfileReport(report: ProfileReport) {
        profileReportListener?.onProfileReport(report)
    }

    // endregion
    // region ProcessHandler

    override fun getProcessInput(): OutputStream? = null

    override fun detachIsDefault(): Boolean = false

    override fun detachProcessImpl() {}

    override fun destroyProcessImpl() {}

    override fun startNotify() {
        super.startNotify()
        try {
            query.profile().execute { results ->
                try {
                    notifyProfileReport(results.report)
                    results.results.forEach { result -> notifyResult(result) }
                } catch (e: Throwable) {
                    notifyException(e)
                } finally {
                    notifyProcessDetached()
                }
            }.onException { e ->
                notifyException(e)
                notifyProcessDetached()
            }
        } catch(e: Throwable) {
            notifyException(e)
            notifyProcessDetached()
        }
    }

    // endregion
    // Query Results

    fun notifyException(e: Throwable) {
        e.message?.let { notifyTextAvailable("$it\n", ProcessOutputTypes.STDOUT) }
    }

    fun notifyResult(result: QueryResult) {
        notifyTextAvailable("----- ${result.type} [${result.mimetype}]\n", ProcessOutputTypes.STDOUT)
        notifyTextAvailable("${result.value}\n", ProcessOutputTypes.STDOUT)
    }

    // endregion
}
