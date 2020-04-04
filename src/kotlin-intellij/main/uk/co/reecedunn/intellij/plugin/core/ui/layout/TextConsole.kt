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
/**
 * IntelliJ defines a DSL for creating UIs, but currently has several disadvantages:
 * 1.  It is not supported on IntelliJ 2019.1 or earlier;
 * 2.  It is classified as being in active development, with potential breaking changes between releases;
 * 3.  It is not necessarily comparable to the swing API in terms of available functionality and flexibility.
 *
 * As such, a swing compatible DSL is defined in this file.
 */
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import java.awt.*

// region text console

fun Container.textConsole(project: Project, constraints: Any?, init: TextConsoleView.() -> Unit): TextConsoleView {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 1.0
        constraints.weighty = 1.0
    }

    val view = TextConsoleView(project)
    val console = view.component
    view.init()
    add(console, constraints)
    return view
}

// endregion
