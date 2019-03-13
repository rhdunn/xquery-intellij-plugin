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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.text.nullize
import org.jetbrains.annotations.Nls
import javax.swing.*

class QueryProcessorDataSource {
    private var panel: JPanel? = null

    private var localFilePath: TextFieldWithBrowseButton? = null

    private var types: ButtonGroup = ButtonGroup()
    private var localFileType: JRadioButton? = null
    private var activeEditorFileType: JRadioButton? = null

    var path: String?
        get() {
            return when (type) {
                QueryProcessorDataSourceType.LocalFile -> localFilePath!!.textField.text.nullize()
                QueryProcessorDataSourceType.ActiveEditorFile -> null
            }
        }
        set(value) {
            when (type) {
                QueryProcessorDataSourceType.LocalFile -> localFilePath!!.textField.text = value ?: ""
                QueryProcessorDataSourceType.ActiveEditorFile -> {
                }
            }
        }

    var type: QueryProcessorDataSourceType
        get() {
            return when {
                localFileType!!.isSelected -> QueryProcessorDataSourceType.LocalFile
                activeEditorFileType!!.isSelected -> QueryProcessorDataSourceType.ActiveEditorFile
                else -> QueryProcessorDataSourceType.LocalFile
            }
        }
        set(value) {
            when (value) {
                QueryProcessorDataSourceType.LocalFile -> localFileType!!.isSelected = true
                QueryProcessorDataSourceType.ActiveEditorFile -> activeEditorFileType!!.isSelected = true
            }
        }

    fun addBrowseFolderListener(
        @Nls(capitalization = Nls.Capitalization.Title) title: String?,
        @Nls(capitalization = Nls.Capitalization.Sentence) description: String?,
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor
    ) {
        localFilePath!!.addBrowseFolderListener(title, description, project, fileChooserDescriptor)
    }

    fun addActionListener(listener: () -> Unit) {
        localFilePath!!.textField.addActionListener { listener() }
    }

    fun create(): JComponent {
        types.add(localFileType)
        types.add(activeEditorFileType)

        return panel!!
    }
}
