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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.content.Content
import com.intellij.util.Consumer
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewEx
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileReportListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable

private val ISO_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
private val FILE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HHmmss")
private val NANOSECOND_FORMAT = {
    val nf = NumberFormat.getInstance()
    nf.minimumFractionDigits = 6
    nf.maximumFractionDigits = 9
    nf
}()

internal fun formatDuration(duration: XsDurationValue): String {
    return "${NANOSECOND_FORMAT.format(duration.seconds.data)} s"
}

private fun formatDate(date: String, dateFormat: DateFormat = SimpleDateFormat.getDateTimeInstance()): String {
    return try {
        val d = ISO_DATE_FORMAT.parse(date.replace("""\\.[0-9]+Z""".toRegex(), ""))
        dateFormat.format(d)
    } catch (e: Exception) {
        date
    }
}

class ProfileReportTableView(val project: Project) : ConsoleViewEx, QueryResultListener, ProfileReportListener {
    // region UI

    private var report: ProfileReport? = null
    private var save: SaveAction? = null

    private var panel: JPanel? = null
    private var results: JTable? = null

    private fun createUIComponents() {
        results = ProfileReportTable()
    }

    // endregion
    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
        report = null
        results!!.removeAll()
    }

    override fun setHelpId(helpId: String) {
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
    }

    override fun getContentSize(): Int = 0

    override fun setOutputPaused(value: Boolean) {
    }

    override fun createConsoleActions(): Array<AnAction> {
        val descriptor = FileSaverDescriptor(
            PluginApiBundle.message("console.action.save.profile.title"),
            PluginApiBundle.message("console.action.save.profile.description"),
            "xml"
        )
        save = SaveAction(descriptor, component, project, Consumer { onSaveProfileReport(it) })
        save?.isEnabled = report?.xml != null
        return arrayOf(save!!)
    }

    override fun getComponent(): JComponent = panel!!

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? ProfileableQueryProcessHandler)?.let {
            it.addQueryResultListener(this, this)
            it.addProfileReportListener(this, this)
        }
    }

    override fun getPreferredFocusableComponent(): JComponent = component

    override fun isOutputPaused(): Boolean = false

    override fun addMessageFilter(filter: Filter) {
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
    }

    override fun canPause(): Boolean = false

    override fun allowHeavyFilters() {
    }

    override fun dispose() {
    }

    override fun scrollTo(offset: Int) {
    }

    // endregion
    // region ConsoleViewEx

    override fun scrollToTop(offset: Int) {
    }

    override fun getContent(ui: RunnerLayoutUi): Content {
        val consoleTitle: String = PluginApiBundle.message("console.tab.profile.label")
        val content = ui.createContent("Profile", component, consoleTitle, null, null)
        content.isCloseable = false
        return content
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        (results as ProfileReportTable).let {
            it.isRunning = true
            it.hasException = false
        }
    }

    override fun onEndResults() {
        (results as ProfileReportTable).isRunning = false
    }

    override fun onQueryResult(result: QueryResult) {
    }

    override fun onException(e: Throwable) {
        (results as ProfileReportTable).hasException = true
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    // endregion
    // region ProfileReportListener

    override fun onProfileReport(result: ProfileReport) {
        report = result
        save?.isEnabled = report?.xml != null

        (results as ProfileReportTable).let {
            it.removeAll()
            result.results.forEach { entry -> it.addRow(entry) }
        }
    }

    // endregion
    // region Actions

    fun onSaveProfileReport(file: VirtualFile) {
        when {
            report?.xml == null -> return
            file.isDirectory -> {
                val name = "profile-report-${formatDate(report!!.created, FILE_DATE_FORMAT)}.xml"
                runUndoTransparentWriteAction {
                    onSaveProfileReport(file.createChildData(this, name))
                }
            }
            else -> file.getOutputStream(this).use {
                it.write(report?.xml!!.toByteArray())
            }
        }
    }

    // endregion
}
