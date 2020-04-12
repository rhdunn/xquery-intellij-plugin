/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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

import com.intellij.openapi.ui.DialogWrapper
import uk.co.reecedunn.intellij.plugin.core.ui.layout.dialog

abstract class Dialog<Configuration> : SettingsUI<Configuration> {
    abstract val resizable: Boolean
    abstract val createTitle: String
    abstract val editTitle: String

    open fun validate(onvalidate: (Boolean) -> Unit) = onvalidate(true)

    private fun run(configuration: Configuration, title: String): Boolean {
        val dialog = dialog(title) {
            resizable(resizable)
            setCenterPanel(panel)
            setPreferredFocusComponent(null)
            setOkOperation {
                dialogWrapper.isOKActionEnabled = false
                validate { valid ->
                    dialogWrapper.isOKActionEnabled = true
                    if (valid) {
                        dialogWrapper.close(DialogWrapper.OK_EXIT_CODE)
                    }
                }
            }
        }

        reset(configuration)
        if (dialog.showAndGet()) {
            apply(configuration)
            return true
        }
        return false
    }

    fun create(configuration: Configuration): Boolean = run(configuration, createTitle)

    fun edit(configuration: Configuration): Boolean = run(configuration, editTitle)
}
