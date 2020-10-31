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

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.scale.JBUIScale
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.JBInsets
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import java.awt.*
import javax.swing.JPanel
import javax.swing.JTabbedPane

// region panel

fun panel(layout: LayoutManager, init: JPanel.() -> Unit): JPanel {
    val panel = JBPanel<JBPanel<*>>(layout)
    panel.init()
    return panel
}

fun panel(init: GridPanel.() -> Unit): GridPanel {
    val panel = GridPanel()
    panel.init()
    return panel
}

fun Container.panel(constraints: Any?, layout: LayoutManager, init: JPanel.() -> Unit): JPanel {
    val panel = JBPanel<JBPanel<*>>(layout)
    panel.init()
    add(panel, constraints)
    return panel
}

fun Container.panel(constraints: Any?, init: GridPanel.() -> Unit): GridPanel {
    val panel = GridPanel()
    panel.init()
    add(panel, constraints)
    return panel
}

// endregion
// region grid

fun grid(x: Int, y: Int): GridBagConstraints = GridBagConstraints(
    x, y, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER,
    GridBagConstraints.NONE,
    JBInsets(0, 0, 0, 0),
    0, 0
)

@Suppress("unused")
fun GridBagConstraints.spanRows(dy: Int = GridBagConstraints.REMAINDER): GridBagConstraints {
    gridheight = dy
    return this
}

fun GridBagConstraints.spanCols(dx: Int = GridBagConstraints.REMAINDER): GridBagConstraints {
    gridwidth = dx
    return this
}

fun GridBagConstraints.padding(x: Int, y: Int): GridBagConstraints {
    val sx = JBUIScale.scale(x)
    val sy = JBUIScale.scale(y)
    insets.set(sy, sx, sy, sx)
    return this
}

enum class LayoutPosition {
    Before,
    After,
    Both
}

@Suppress("DuplicatedCode")
fun GridBagConstraints.vgap(y: Int, position: LayoutPosition = LayoutPosition.After): GridBagConstraints {
    val sy = JBUIScale.scale(y)
    when (position) {
        LayoutPosition.Before -> {
            insets.top = sy
            insets.bottom = 0
        }
        LayoutPosition.After -> {
            insets.top = 0
            insets.bottom = sy
        }
        LayoutPosition.Both -> {
            insets.top = sy
            insets.bottom = sy
        }
    }
    return this
}

fun GridBagConstraints.vgap(position: LayoutPosition = LayoutPosition.After): GridBagConstraints = vgap(4, position)

@Suppress("DuplicatedCode")
fun GridBagConstraints.hgap(x: Int, position: LayoutPosition = LayoutPosition.Before): GridBagConstraints {
    val sx = JBUIScale.scale(x)
    when (position) {
        LayoutPosition.Before -> {
            insets.left = sx
            insets.right = 0
        }
        LayoutPosition.After -> {
            insets.left = 0
            insets.right = sx
        }
        LayoutPosition.Both -> {
            insets.left = sx
            insets.right = sx
        }
    }
    return this
}

fun GridBagConstraints.hgap(position: LayoutPosition = LayoutPosition.Before): GridBagConstraints = hgap(6, position)

fun GridBagConstraints.horizontal(weight: Double = 1.0): GridBagConstraints {
    fill = GridBagConstraints.HORIZONTAL
    weightx = weight
    return this
}

fun GridBagConstraints.vertical(weight: Double = 1.0): GridBagConstraints {
    fill = GridBagConstraints.VERTICAL
    weighty = weight
    return this
}

fun GridBagConstraints.fill(weightx: Double = 1.0, weighty: Double = 1.0): GridBagConstraints {
    fill = GridBagConstraints.BOTH
    this.weightx = weightx
    this.weighty = weighty
    return this
}

// endregion
// region grid panel

class GridPanel : JBPanel<GridPanel>(GridBagLayout()) {
    var currentRow: Int = 0
    var currentCol: Int = 0
}

fun GridPanel.row(init: GridPanel.() -> Unit): GridPanel {
    this.init()
    currentCol = 0
    currentRow++
    return this
}

val GridPanel.column: GridBagConstraints
    get() = grid(currentCol++, currentRow)

// endregion
// region details panel

fun detailsPanel(init: GridPanel.() -> Unit): GridPanel = panel {
    border = Borders.Details
    init()
    row {
        spacer(column.vertical())
        spacer(column.horizontal())
    }
}

fun GridPanel.details(label: String, text: String?) {
    if (text != null) {
        row {
            label(label, column.vgap()) { foreground = Color.GRAY }
            label(text, column.hgap().vgap())
        }
    }
}

fun GridPanel.details(label: String, text: Sequence<String>) {
    if (text.any()) {
        row {
            label(label, column.vgap()) { foreground = Color.GRAY }
            label(text.joinToString(" "), column.hgap().vgap())
        }
    }
}

// endregion
// region scrollable

fun Container.scrollable(view: Component?, constraints: Any?, init: JBScrollPane.() -> Unit): JBScrollPane {
    val pane = JBScrollPane(view)
    pane.init()
    add(pane, constraints)
    return pane
}

fun Container.scrollable(constraints: Any?, init: JBScrollPane.() -> Unit): JBScrollPane {
    return scrollable(null, constraints, init)
}

@Suppress("unused")
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
    row {
        val pane = JBTabbedPane()
        pane.init()
        add(pane, column.horizontal())
    }
}

fun JTabbedPane.tab(title: String, component: Component) {
    if (component is JPanel) {
        component.border = Borders.TabPanel
    }

    add(title, component)
}

// endregion
// region spacers

fun Container.spacer(constraints: Any? = null): Spacer {
    val spacer = Spacer()
    add(spacer, constraints)
    return spacer
}

// endregion
