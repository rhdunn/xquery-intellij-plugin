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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.histogram

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.content.Content
import com.intellij.util.Consumer
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ContentProvider
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileReportListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable

private val ISO_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
private val FILE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HHmmss")

private fun formatDate(date: String, dateFormat: DateFormat = SimpleDateFormat.getDateTimeInstance()): String {
    return try {
        val d = ISO_DATE_FORMAT.parse(date.replace("""\\.[0-9]+Z""".toRegex(), ""))
        dateFormat.format(d)
    } catch (e: Exception) {
        date
    }
}

class HistogramTableView(val project: Project) : ContentProvider, Disposable, QueryResultListener, ProfileReportListener {
    // region UI

    private var report: ProfileReport? = null
    private var save: SaveAction? = null

    private var panel: JPanel? = null
    private var results: JTable? = null

    private fun createUIComponents() {
        results = HistogramTable()
    }

    // endregion
    // region ContentProvider

    override val contentId: String = "Histogram"

    override fun getContent(ui: RunnerLayoutUi): Content {
        val consoleTitle: String = PluginApiBundle.message("console.tab.histogram.label")
        val content = ui.createContent(contentId, getComponent(), consoleTitle, AllIcons.Debugger.Overhead, null)
        content.isCloseable = false
        return content
    }

    private fun getComponent(): JComponent = panel!!

    override fun clear() {
        report = null
        results!!.removeAll()
    }

    override fun createRunnerLayoutActions(): Array<AnAction> {
        val descriptor = FileSaverDescriptor(
            PluginApiBundle.message("console.action.save.profile.title"),
            PluginApiBundle.message("console.action.save.profile.description"),
            "xml"
        )
        save = SaveAction(
            descriptor,
            getComponent(),
            project,
            Consumer { onSaveProfileReport(it) })
        save?.isEnabled = report?.xml != null
        return arrayOf(save!!)
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? ProfileableQueryProcessHandler)?.let {
            it.addQueryResultListener(this, this)
            it.addProfileReportListener(this, this)
        }
    }

    // endregion
    // region Disposable

    override fun dispose() {
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        (results as HistogramTable).let {
            it.isRunning = true
            it.hasException = false
        }
    }

    override fun onEndResults() {
        (results as HistogramTable).isRunning = false
    }

    override fun onQueryResult(result: QueryResult) {
    }

    override fun onException(e: Throwable) {
        (results as HistogramTable).hasException = true
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    // endregion
    // region ProfileReportListener

    override fun onProfileReport(result: ProfileReport) {
        report = result
        save?.isEnabled = report?.xml != null

        (results as HistogramTable).let {
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
                val name = "profile-report-${formatDate(
                    report!!.created,
                    FILE_DATE_FORMAT
                )}.xml"
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
