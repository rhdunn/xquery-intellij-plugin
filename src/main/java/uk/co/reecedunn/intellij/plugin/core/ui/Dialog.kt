/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.ui

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper

abstract class Dialog<Configuration> : SettingsUIFactory<Configuration> {
    abstract val resizable: Boolean
    abstract val createTitle: String
    abstract val editTitle: String

    open fun validate(editor: SettingsUI<Configuration>, onvalidate: (Boolean) -> Unit) {
        onvalidate(true)
    }

    private fun run(configuration: Configuration, title: String): Boolean {
        val editor = createSettingsUI()

        val builder = DialogBuilder()
        builder.resizable(resizable)
        builder.setTitle(title)
        builder.setCenterPanel(editor.panel!!)
        builder.setPreferredFocusComponent(null)
        builder.setOkOperation {
            validate(editor) { valid ->
                if (valid) {
                    builder.dialogWrapper.close(DialogWrapper.OK_EXIT_CODE)
                }
            }
        }

        editor.reset(configuration)
        if (builder.showAndGet()) {
            editor.apply(configuration)
            return true
        }
        return false
    }

    fun create(configuration: Configuration): Boolean {
        return run(configuration, createTitle)
    }

    fun edit(configuration: Configuration): Boolean {
        return run(configuration, editTitle)
    }
}
