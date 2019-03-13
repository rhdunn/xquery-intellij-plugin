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
    private var scriptFile: TextFieldWithBrowseButton? = null

    var path: String?
        get() = scriptFile!!.textField.text.nullize()
        set(value) {
            scriptFile!!.textField.text = value ?: ""
        }

    fun addBrowseFolderListener(
        @Nls(capitalization = Nls.Capitalization.Title) title: String?,
        @Nls(capitalization = Nls.Capitalization.Sentence) description: String?,
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor
    ) {
        scriptFile!!.addBrowseFolderListener(title, description, project, fileChooserDescriptor)
    }

    fun addActionListener(listener: () -> Unit) {
        scriptFile!!.textField.addActionListener { listener() }
    }

    fun create(): JComponent {
        return panel!!
    }

    private fun createUIComponents() {
        scriptFile = TextFieldWithBrowseButton()
    }
}
