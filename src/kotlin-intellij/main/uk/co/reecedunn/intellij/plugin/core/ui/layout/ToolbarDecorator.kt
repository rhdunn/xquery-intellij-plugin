// Copyright (C) 2019, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.compat.ui.addExtraActionButton
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.AnActionButton
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import java.awt.Container
import java.awt.Dimension
import javax.swing.*

fun ToolbarDecorator.actionButton(title: String, icon: Icon, action: (AnActionEvent) -> Unit) {
    val button = object : AnActionButton(title, icon)  {
        override fun actionPerformed(e: AnActionEvent) = action(e)
    }
    addExtraActionButton(button)
}

fun <T> toolbarDecorator(init: () -> T): ToolbarDecorator = when (val ui = init()) {
    is JList<*> -> ToolbarDecorator.createDecorator(ui)
    is JTree -> ToolbarDecorator.createDecorator(ui)
    is JTable -> ToolbarDecorator.createDecorator(ui)
    is TableView<*> -> ToolbarDecorator.createDecorator(ui)
    else -> throw UnsupportedOperationException()
}

fun DialogBuilder.toolbarPanel(decorator: ToolbarDecorator, minimumSize: Dimension?): JPanel {
    val panel = decorator.createPanel()
    minimumSize?.let { panel.minimumSize = it }
    setCenterPanel(panel)
    return panel
}

fun Container.toolbarPanel(decorator: ToolbarDecorator, constraints: Any?): JPanel {
    val panel = decorator.createPanel()
    add(panel, constraints)
    return panel
}
