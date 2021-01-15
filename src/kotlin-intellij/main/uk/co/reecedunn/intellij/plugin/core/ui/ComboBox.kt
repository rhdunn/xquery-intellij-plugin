/*
 * Copyright (C) 2021 Reece H. Dunn
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

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import javax.swing.JComboBox

fun JComboBox<String>.selectOrAddItem(item: String?) {
    if (itemCount == 0 && item != null) {
        addItem(item)
    } else {
        selectedItem = item
    }
}

fun JComboBox<String>.replaceItems(items: Sequence<String>) {
    val wasEnabled = isEnabled
    try {
        // Don't generate action listener events during the update.
        isEnabled = false

        val current = selectedItem
        removeAllItems()

        if (isEditable) {
            selectedItem = current
        }
        items.forEach { addItem(it) }
        if (!isEditable) {
            selectedItem = current
        }
    } finally {
        isEnabled = wasEnabled
    }
}

fun JComboBox<String>.replaceItems(items: List<String>) = replaceItems(items.asSequence())

fun JComboBox<String>.replaceItemsOnPooledThread(calculateItems: () -> List<String>, onupdated: () -> Unit) {
    executeOnPooledThread {
        var updated = false
        try {
            val items = calculateItems()
            invokeLater(ModalityState.any()) {
                replaceItems(items)
                updated = true
                onupdated()
            }
        } catch (e: Throwable) {
            if (!updated) {
                invokeLater(ModalityState.any()) {
                    replaceItems(emptySequence())
                }
            }
        }
    }
}

fun JComboBox<String>.replaceItemsOnPooledThread(calculateItems: () -> List<String>) {
    replaceItemsOnPooledThread(calculateItems, {})
}
