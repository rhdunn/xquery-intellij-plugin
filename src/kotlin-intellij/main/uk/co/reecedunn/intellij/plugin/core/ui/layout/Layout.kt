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
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.JBUI
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import java.awt.*
import javax.swing.JPanel
import javax.swing.JTabbedPane

// Layouts

// region panel

fun panel(layout: LayoutManager, init: JPanel.() -> Unit): JPanel {
    val panel = JPanel(layout)
    panel.init()
    return panel
}

fun panel(init: JPanel.() -> Unit): JPanel = panel(GridBagLayout(), init)

fun Container.horizontalPanel(constraints: Any?, init: JPanel.() -> Unit): JPanel {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = JBUI.insets(2, 4, 2, 4)
        constraints.weightx = 1.0
    }

    val panel = JPanel(GridBagLayout())
    panel.init()
    add(panel, constraints)
    return panel
}

// endregion
// region grid

fun grid(x: Int, y: Int): GridBagConstraints {
    val constraints = GridBagConstraints()
    constraints.gridx = x
    constraints.gridy = y
    return constraints
}

fun GridBagConstraints.size(dx: Int, dy: Int): GridBagConstraints {
    gridwidth = dx
    gridheight = dy
    return this
}

// endregion
// region scrollable

fun Container.scrollable(view: Component?, constraints: Any?, init: JBScrollPane.() -> Unit): JBScrollPane {
    if (constraints is GridBagConstraints) {
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        constraints.insets = JBUI.emptyInsets()
    }

    val pane = JBScrollPane(view)
    pane.init()
    add(pane, constraints)
    return pane
}

fun Container.scrollable(constraints: Any?, init: JBScrollPane.() -> Unit): JBScrollPane {
    return scrollable(null, constraints, init)
}

fun Container.scrollable(init: JBScrollPane.() -> Unit): JBScrollPane = scrollable(null, null, init)

// endregion
// region dialog

fun dialog(title: String, init: DialogBuilder.() -> Unit): DialogBuilder {
    val builder = DialogBuilder()
    builder.setTitle(title)
    builder.init()
    return builder
}

// endregion
// region tabbed panel

fun tabbedPanel(init: JTabbedPane.() -> Unit): JPanel = panel {
    val constraints = grid(0, 0)
    constraints.fill = GridConstraints.FILL_HORIZONTAL
    constraints.weightx = 1.0

    val pane = JBTabbedPane()
    pane.init()
    add(pane, constraints)
}

fun JTabbedPane.tab(title: String, component: Component) {
    add(title, component)
}

// endregion

// Components

// region combo box

fun <T> Container.comboBox(constraints: Any?, init: ComboBox<T>.() -> Unit): ComboBox<T> {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.HORIZONTAL
    }

    val combobox = ComboBox<T>()
    combobox.init()
    add(combobox, constraints)
    return combobox
}

// endregion
// region label

fun Container.label(text: String, constraints: Any? = null): JBLabel {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.NONE
        if (constraints.gridx == 0)
            constraints.insets = JBUI.insets(0, 0, 4, 8)
        else
            constraints.insets = JBUI.insets(0, 8, 4, 8)
    }

    val label = JBLabel(text)
    add(label, constraints)
    return label
}

// endregion
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
// region text field with browse button

fun Container.textFieldWithBrowseButton(
    constraints: Any?,
    init: TextFieldWithBrowseButton.() -> Unit
): TextFieldWithBrowseButton {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = JBUI.insetsBottom(4)
    }

    val field = TextFieldWithBrowseButton()
    field.init()
    add(field, constraints)
    return field
}

fun Container.textFieldWithBrowseButton(init: TextFieldWithBrowseButton.() -> Unit): TextFieldWithBrowseButton {
    return textFieldWithBrowseButton(null, init)
}

// endregion
// region spacers

fun Container.horizontalSpacer(constraints: Any? = null): Spacer {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
    }

    val spacer = Spacer()
    add(spacer, constraints)
    return spacer
}

// endregion
