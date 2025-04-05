// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.project.settings

import com.intellij.compat.openapi.ui.addBrowseFolderListenerEx
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmBundle
import javax.swing.JCheckBox
import javax.swing.JComponent

class XpmModuleConfigurable(val project: Project) : Configurable {
    private val settings = XpmModuleLoaderSettings.getInstance(project)

    // region Configurable

    private lateinit var databasePath: TextFieldWithBrowseButton
    private lateinit var registerSchemaFiles: JCheckBox

    override fun getDisplayName(): String = XpmBundle.message("preferences.module.title")

    override fun createComponent(): JComponent = panel {
        row {
            label(XpmBundle.message("preferences.module.database-path.label"), column.vgap())
            databasePath = textFieldWithBrowseButton(column.vgap().hgap().horizontal()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = XpmBundle.message("preferences.module.database-path.choose-path")
                addBrowseFolderListenerEx(project, descriptor)
            }
        }
        row {
            registerSchemaFiles = checkBox(column.horizontal().spanCols(2)) {
                text = XpmBundle.message("preferences.module.register-schema-files.label")
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.horizontal())
        }
    }

    override fun isModified(): Boolean {
        if (databasePath.textField.text != settings.databasePath) return true
        if (registerSchemaFiles.isSelected != settings.registerSchemaFile) return true
        return false
    }

    override fun apply() {
        settings.databasePath = databasePath.textField.text
        settings.registerSchemaFile = registerSchemaFiles.isSelected
    }

    override fun reset() {
        databasePath.textField.text = settings.databasePath
        registerSchemaFiles.isSelected = settings.registerSchemaFile
    }

    // endregion
}
