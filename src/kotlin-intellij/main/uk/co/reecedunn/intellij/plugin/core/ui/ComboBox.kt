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

fun JComboBox<String>.replaceItems(items: Sequence<String>) {
    val current = selectedItem
    removeAllItems()
    items.forEach { addItem(it) }
    selectedItem = current
}

fun JComboBox<String>.replaceItems(items: List<String>) = replaceItems(items.asSequence())

fun JComboBox<String>.replaceItemsOnPooledThread(calculateItems: () -> List<String>) {
    executeOnPooledThread {
        try {
            val items = calculateItems()
            invokeLater(ModalityState.any()) {
                replaceItems(items)
            }
        } catch (e: Throwable) {
            invokeLater(ModalityState.any()) {
                replaceItems(emptySequence())
            }
        }
    }
}
