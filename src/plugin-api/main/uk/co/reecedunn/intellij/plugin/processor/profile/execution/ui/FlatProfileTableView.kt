/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.profile.execution.ui

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.Content
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ContentProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.process.ProfileReportListener
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.run.execution.ui.QueryConsoleView
import uk.co.reecedunn.intellij.plugin.processor.run.execution.ui.QueryTable
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.swing.JComponent
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

class FlatProfileTableView(val project: Project) :
    ContentProvider,
    Disposable,
    ProfileReportListener {
    // region UI

    private var report: FlatProfileReport? = null
    private var save: SaveAction? = null

    private var component: JComponent? = null
    private var results: JTable? = null

    private fun createComponent(): JComponent {
        results = FlatProfileTable()
        results!!.selectionModel.addListSelectionListener {
            val row = results!!.selectedRow
            if (row >= 0) {
                val index = results!!.convertRowIndexToModel(row)
                val item = (results!!.model as ListTableModel<*>).getItem(index) as FlatProfileEntry
                val navigatable = item.frame.sourcePosition?.createNavigatable(project)
                if (navigatable?.canNavigate() == true) {
                    navigatable.navigate(true)
                }
            }
        }

        component = JBScrollPane(results)
        return component!!
    }

    // endregion
    // region ContentProvider

    private var queryProcessHandler: ProfileableQueryProcessHandler? = null

    override val contentId: String = "FlatProfile"

    override fun getContent(ui: RunnerLayoutUi): Content {
        val consoleTitle: String = PluginApiBundle.message("console.tab.flat-profile.label")
        val content = ui.createContent(contentId, getComponent(), consoleTitle, AllIcons.Debugger.Overhead, null)
        content.isCloseable = false
        return content
    }

    private fun getComponent(): JComponent = component ?: createComponent()

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
        save = SaveAction(descriptor, getComponent(), project) { onSaveProfileReport(it) }
        save?.isEnabled = report?.xml != null
        return arrayOf(save!!)
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
        queryProcessHandler = processHandler as? ProfileableQueryProcessHandler
        queryProcessHandler?.addProfileReportListener(this)
    }

    override fun attachToConsole(consoleView: ConsoleView) {
        (consoleView as? QueryConsoleView)?.registerQueryTable(results as QueryTable)
    }

    // endregion
    // region Disposable

    override fun dispose() {
        queryProcessHandler?.removeProfileReportListener(this)
    }

    // endregion
    // region ProfileReportListener

    override fun onProfileReport(result: FlatProfileReport) {
        report = result
        save?.isEnabled = report?.xml != null

        (results as FlatProfileTable).let {
            it.elapsed = result.elapsed
            it.removeAll()
            result.results.forEach { entry -> it.addRow(entry) }
        }
    }

    // endregion
    // region Actions

    private fun onSaveProfileReport(file: VirtualFile) {
        when {
            report?.xml == null -> return
            file.isDirectory -> {
                val name = "profile-report-${
                    formatDate(report!!.created, FILE_DATE_FORMAT)
                }.xml"
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
