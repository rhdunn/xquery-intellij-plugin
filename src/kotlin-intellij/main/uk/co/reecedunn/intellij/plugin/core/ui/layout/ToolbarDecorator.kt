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

fun <T> DialogBuilder.toolbarPanel(minimumSize: Dimension?, init: ToolbarDecoratorBuilder.() -> T): JPanel {
    val panel = toolbarDecorator(init).createPanel()
    minimumSize?.let { panel.minimumSize = it }
    setCenterPanel(panel)
    return panel
}

fun <T> Container.toolbarPanel(constraints: Any?, init: ToolbarDecoratorBuilder.() -> T): JPanel {
    val panel = toolbarDecorator(init).createPanel()
    add(panel, constraints)
    return panel
}
