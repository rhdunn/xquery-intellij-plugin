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
package uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.text.nullize
import org.jetbrains.annotations.Nls
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import javax.swing.ButtonGroup
import javax.swing.JRadioButton
import javax.swing.JTextField

class QueryProcessorDataSource(private val allowUnspecified: Boolean = false) {
    internal lateinit var localFilePath: TextFieldWithBrowseButton
    internal lateinit var databaseModulePath: JTextField

    internal lateinit var types: ButtonGroup
    internal lateinit var notSpecifiedType: JRadioButton
    internal lateinit var localFileType: JRadioButton
    internal lateinit var databaseModuleType: JRadioButton
    internal lateinit var activeEditorFileType: JRadioButton

    var path: String?
        get() = when (type) {
            null -> null
            QueryProcessorDataSourceType.LocalFile -> localFilePath.textField.text.nullize()
            QueryProcessorDataSourceType.DatabaseModule -> databaseModulePath.text.nullize()
            QueryProcessorDataSourceType.ActiveEditorFile -> null
        }
        set(value) {
            when (type) {
                QueryProcessorDataSourceType.LocalFile -> localFilePath.textField.text = value ?: ""
                QueryProcessorDataSourceType.DatabaseModule -> databaseModulePath.text = value ?: ""
                QueryProcessorDataSourceType.ActiveEditorFile, null -> {
                }
            }
        }

    var type: QueryProcessorDataSourceType?
        get() = when {
            notSpecifiedType.isSelected -> null
            localFileType.isSelected -> QueryProcessorDataSourceType.LocalFile
            databaseModuleType.isSelected -> QueryProcessorDataSourceType.DatabaseModule
            activeEditorFileType.isSelected -> QueryProcessorDataSourceType.ActiveEditorFile
            else -> QueryProcessorDataSourceType.LocalFile
        }
        set(value) {
            when (value) {
                null -> {
                    if (allowUnspecified) {
                        notSpecifiedType.isSelected = true
                    } else {
                        localFileType.isSelected = true
                    }
                }
                QueryProcessorDataSourceType.LocalFile -> localFileType.isSelected = true
                QueryProcessorDataSourceType.DatabaseModule -> databaseModuleType.isSelected = true
                QueryProcessorDataSourceType.ActiveEditorFile -> activeEditorFileType.isSelected = true
            }
        }

    fun addBrowseFolderListener(
        @Nls(capitalization = Nls.Capitalization.Title) title: String?,
        @Nls(capitalization = Nls.Capitalization.Sentence) description: String?,
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor
    ) {
        localFilePath.addBrowseFolderListener(title, description, project, fileChooserDescriptor)
    }

    fun addActionListener(listener: () -> Unit) {
        localFilePath.textField.addActionListener { listener() }
    }
}

fun GridPanel.queryProcessorDataSource(
    allowUnspecified: Boolean = false,
    init: QueryProcessorDataSource.() -> Unit = {}
): QueryProcessorDataSource {
    val ui = QueryProcessorDataSource(allowUnspecified)
    ui.types = buttonGroup {
        row {
            ui.localFileType = radio(column.vgap()) {
                text = PluginApiBundle.message("xquery.configurations.data-source.local-file.label")
            }
            ui.localFilePath = textFieldWithBrowseButton(column.spanCols().horizontal().hgap().vgap())
        }
        row {
            ui.databaseModuleType = radio(column.vgap()) {
                text = PluginApiBundle.message("xquery.configurations.data-source.database-module.label")
            }
            ui.databaseModulePath = textField(column.spanCols().horizontal().hgap().vgap())
        }
        row {
            ui.activeEditorFileType = radio(column.spanCols().vgap()) {
                text = PluginApiBundle.message("xquery.configurations.data-source.active-editor-file.label")
            }
        }
        row {
            ui.notSpecifiedType = radio(column.spanCols()) {
                text = PluginApiBundle.message("xquery.configurations.data-source.not-specified.label")
                isVisible = allowUnspecified
            }
        }
    }
    ui.init()
    return ui
}
