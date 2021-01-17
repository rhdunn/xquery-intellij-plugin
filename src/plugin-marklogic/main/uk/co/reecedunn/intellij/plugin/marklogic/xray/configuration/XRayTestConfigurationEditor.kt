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
import uk.co.reecedunn.intellij.plugin.core.ui.selectOrAddItem
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorComboBox
import uk.co.reecedunn.intellij.plugin.processor.query.populateDatabaseUI
import uk.co.reecedunn.intellij.plugin.processor.query.populateServerUI
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
    private lateinit var outputFormat: JComboBox<XRayTestFormat>

    @Suppress("DuplicatedCode")
    private val panel = panel {
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.query-processor.label"), column.vgap())
            queryProcessor = QueryProcessorComboBox(project)
            add(queryProcessor.component, column.horizontal().hgap().vgap())
            queryProcessor.addActionListener {
                database.populateDatabaseUI(queryProcessor.settings)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.content-database.label"), column)
            database = comboBox(column.horizontal().hgap()) {
                addActionListener {
                    server.populateServerUI(queryProcessor.settings, database.selectedItem as? String? ?: "")
                }
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.server.label"), column.vgap())
            server = comboBox(column.horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.module-root.label"), column.vgap())
            modulePath = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.module-path")
                addBrowseFolderListener(null, null, project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.test-root.label"), column.vgap())
            testPath = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.test-path")
                addBrowseFolderListener(null, null, project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.module-pattern.label"), column.vgap())
            modulePattern = textField(column.horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.test-pattern.label"), column.vgap())
            testPattern = textField(column.horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.test.output-format.label"), column.vgap())
            outputFormat = comboBox(column.horizontal().hgap().vgap()) {
                addItem(XRayTestFormat.HTML)
                addItem(XRayTestFormat.JSON)
                addItem(XRayTestFormat.Text)
                addItem(XRayTestFormat.XML)
                addItem(XRayTestFormat.XUnit)
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.horizontal())
        }
    }

    // endregion
    // region SettingsEditor

    override fun createEditor(): JComponent = panel

    override fun resetEditorFrom(settings: XRayTestConfiguration) {
        queryProcessor.processorId = settings.processorId
        database.selectOrAddItem(settings.database)
        server.selectOrAddItem(settings.server)
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
        settings.outputFormat = (outputFormat.selectedItem as? XRayTestFormat) ?: XRayTestFormat.Text
    }

    private val projectRoot: String?
        get() = XpmProjectConfigurations.getInstance(project).modulePaths.firstOrNull()?.canonicalPath

    // endregion
}
