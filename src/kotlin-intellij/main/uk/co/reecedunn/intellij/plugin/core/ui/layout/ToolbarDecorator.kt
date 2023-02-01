// Copyright (C) 2019, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.AnActionButton
import com.intellij.ui.AnActionButtonRunnable
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.SmartList
import java.awt.Container
import java.awt.Dimension
import javax.swing.*

// NOTE: The ToolbarDecorator class is not easily usable as a DSL class, so this is a DSL wrapper around that class.
class ToolbarDecoratorBuilder {
    internal var addActionRunnable: AnActionButtonRunnable? = null
    internal var editActionRunnable: AnActionButtonRunnable? = null
    internal var removeActionRunnable: AnActionButtonRunnable? = null

    internal var moveUpActionRunnable: AnActionButtonRunnable? = null
    internal var moveDownActionRunnable: AnActionButtonRunnable? = null

    internal var extraActions: MutableList<AnActionButton> = SmartList()

    fun addAction(action: (AnActionButton) -> Unit) {
        addActionRunnable = AnActionButtonRunnable { action(it) }
    }

    fun editAction(action: (AnActionButton) -> Unit) {
        editActionRunnable = AnActionButtonRunnable { action(it) }
    }

    fun removeAction(action: (AnActionButton) -> Unit) {
        removeActionRunnable = AnActionButtonRunnable { action(it) }
    }

    fun action(title: String, icon: Icon, action: (AnActionEvent) -> Unit) {
        extraActions.add(object : AnActionButton(title, icon) {
            override fun actionPerformed(e: AnActionEvent) = action(e)
        })
    }
}

fun ToolbarDecorator.actionButton(title: String, icon: Icon, action: (AnActionEvent) -> Unit) {
    val button = object : AnActionButton(title, icon)  {
        override fun actionPerformed(e: AnActionEvent) = action(e)
    }
    addExtraAction(button)
}

fun <T> toolbarDecorator(init: ToolbarDecoratorBuilder.() -> T): ToolbarDecorator {
    val builder = ToolbarDecoratorBuilder()
    val decorator = when (val ui = builder.init()) {
        is JList<*> -> ToolbarDecorator.createDecorator(ui)
        is JTree -> ToolbarDecorator.createDecorator(ui)
        is JTable -> ToolbarDecorator.createDecorator(ui)
        is TableView<*> -> ToolbarDecorator.createDecorator(ui)
        else -> throw UnsupportedOperationException()
    }

    decorator.setAddAction(builder.addActionRunnable)
    decorator.setEditAction(builder.editActionRunnable)
    decorator.setRemoveAction(builder.removeActionRunnable)

    decorator.setMoveUpAction(builder.moveUpActionRunnable)
    decorator.setMoveDownAction(builder.moveDownActionRunnable)

    builder.extraActions.forEach { decorator.addExtraAction(it) }
    return decorator
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
