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
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.ui.table.JBTable
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileReportListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.border.MatteBorder

private fun formatDuration(duration: XsDurationValue): String {
    return "${duration.seconds.data} s"
}

private val PANEL_BORDER = MatteBorder(0, 0, 1, 0, Color(192, 192, 192))

class ProfileConsoleView : ConsoleView, ProfileReportListener {
    // region UI

    private var panel: JPanel? = null
    private var metadata: JPanel? = null
    private var elapsed: JLabel? = null
    private var created: JLabel? = null
    private var version: JLabel? = null
    private var results: JTable? = null

    private fun createUIComponents() {
        metadata = JPanel()
        metadata!!.border = PANEL_BORDER

        results = ProfileEntryTable()
    }

    // endregion
    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
        elapsed!!.text = PluginApiBundle.message("profile.console.elapsed.label.no-value")
        created!!.text = PluginApiBundle.message("profile.console.created.label.no-value")
        version!!.text = PluginApiBundle.message("profile.console.version.label.no-value")
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
        return AnAction.EMPTY_ARRAY
    }

    override fun getComponent(): JComponent = panel!!

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? ProfileableQueryProcessHandler)?.profileReportListener = this
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
    // region ProfileReportListener

    override fun onProfileReport(result: ProfileReport) {
        elapsed!!.text = PluginApiBundle.message("profile.console.elapsed.label", formatDuration(result.elapsed))
        created!!.text = PluginApiBundle.message("profile.console.created.label", result.created)
        version!!.text = PluginApiBundle.message("profile.console.version.label", result.version)
        (results as ProfileEntryTable).let {
            it.removeAll()
            result.results.forEach { entry -> it.addRow(entry) }
        }
    }

    // endregion
}
