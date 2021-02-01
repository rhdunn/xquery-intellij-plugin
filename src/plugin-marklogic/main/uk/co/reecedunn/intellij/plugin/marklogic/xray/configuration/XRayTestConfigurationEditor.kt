/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.html.XRayHtmlFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.json.XRayJsonFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.text.XRayTextFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xunit.XRayXUnitFormat
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.model.DatabaseComboBoxModel
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.model.ServerComboBoxModel
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorComboBox
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JTextField

class XRayTestConfigurationEditor(private val project: Project) : SettingsEditor<XRayTestConfiguration>() {
    // region Form

    private lateinit var queryProcessor: QueryProcessorComboBox
    private lateinit var server: JComboBox<String>
    private lateinit var database: JComboBox<String>
    private lateinit var modulePath: TextFieldWithBrowseButton

    private lateinit var testPath: TextFieldWithBrowseButton
    private lateinit var modulePattern: JTextField
    private lateinit var testPattern: JTextField
    private lateinit var outputFormat: JComboBox<TestFormat>

    @Suppress("DuplicatedCode")
    private val panel = panel {
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.query-processor.label"), column.vgap())
            queryProcessor = QueryProcessorComboBox(project)
            add(queryProcessor.component, column.spanCols().horizontal().hgap().vgap())
            queryProcessor.addActionListener {
                (database.model as DatabaseComboBoxModel).update(queryProcessor.settings)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.content-database.label"), column.vgap())
            database = comboBox(column.horizontal().hgap().vgap()) {
                model = DatabaseComboBoxModel()
                addActionListener {
                    val database = database.selectedItem as? String? ?: ""
                    (server.model as ServerComboBoxModel).update(queryProcessor.settings, database)
                }
            }
            label(PluginApiBundle.message("xquery.configurations.processor.server.label"), column.hgap().vgap())
            server = comboBox(column.horizontal().hgap().vgap()) {
                model = ServerComboBoxModel()
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.module-root.label"), column.vgap())
            modulePath = textFieldWithBrowseButton(column.spanCols().horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.module-path")
                addBrowseFolderListener(null, null, project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.test-root.label"), column.vgap())
            testPath = textFieldWithBrowseButton(column.spanCols(3).horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.test-path")
                addBrowseFolderListener(null, null, project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.module-pattern.label"), column.vgap())
            modulePattern = textField(column.spanCols().horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.test-pattern.label"), column.vgap())
            testPattern = textField(column.spanCols().horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.output-format.label"), column.vgap())
            outputFormat = comboBox(column.spanCols().horizontal().hgap().vgap()) {
                addItem(XRayHtmlFormat)
                addItem(XRayJsonFormat)
                addItem(XRayTextFormat)
                addItem(XRayXmlFormat)
                addItem(XRayXUnitFormat)
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.spanCols().horizontal())
        }
    }

    // endregion
    // region SettingsEditor

    override fun createEditor(): JComponent = panel

    override fun resetEditorFrom(settings: XRayTestConfiguration) {
        queryProcessor.processorId = settings.processorId
        (database.model as DatabaseComboBoxModel).defaultSelection = settings.database
        (server.model as ServerComboBoxModel).defaultSelection = settings.server
        modulePath.textField.text = settings.modulePath ?: ""
        testPath.textField.text = settings.testPath ?: projectRoot ?: ""
        modulePattern.text = settings.modulePattern ?: ""
        testPattern.text = settings.testPattern ?: ""
        outputFormat.selectedItem = settings.outputFormat
    }

    override fun applyEditorTo(settings: XRayTestConfiguration) {
        settings.processorId = queryProcessor.processorId
        settings.database = database.selectedItem as? String
        settings.server = server.selectedItem as? String
        settings.modulePath = modulePath.textField.text.nullize()
        settings.testPath = when (val path = testPath.textField.text) {
            projectRoot -> null
            else -> path.nullize()
        }
        settings.modulePattern = modulePattern.text.nullize()
        settings.testPattern = testPattern.text.nullize()
        settings.outputFormat = (outputFormat.selectedItem as? TestFormat) ?: XRayTextFormat
    }

    private val projectRoot: String?
        get() = XpmProjectConfigurations.getInstance(project).modulePaths.firstOrNull()?.canonicalPath

    // endregion
}
