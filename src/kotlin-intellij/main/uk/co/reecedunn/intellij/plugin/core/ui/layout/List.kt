// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.ui.components.JBList
import javax.swing.ListModel

fun <Item> list(model: ListModel<Item>, init: JBList<Item>.() -> Unit): JBList<Item> {
    val list = JBList(model)
    list.init()
    return list
}
