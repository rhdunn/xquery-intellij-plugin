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

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import uk.co.reecedunn.compat.actionSystem.AnAction
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import javax.swing.JComponent

class SaveAction(
    val descriptor: FileSaverDescriptor,
    val parent: JComponent,
    val project: Project?,
    val consumer: Consumer<VirtualFile>
) : AnAction(
    PluginApiBundle.message("console.action.save.label"),
    null,
    AllIcons.Actions.Menu_saveall
) {
    var isEnabled: Boolean = true

    override fun doAction(e: AnActionEvent) {
        FileChooserFactory.getInstance().createSaveFileDialog(descriptor, null as Project?)
        FileChooser.chooseFile(descriptor, project, parent, null, consumer)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = isEnabled
    }
}
