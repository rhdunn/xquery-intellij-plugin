/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import javax.swing.JComboBox

fun JComboBox<String>.populateServerUI(settings: QueryProcessorSettings?, database: String) {
    if (settings == null) return
    executeOnPooledThread {
        try {
            val servers = settings.session.servers(database)
            invokeLater(ModalityState.any()) {
                val current = selectedItem
                removeAllItems()
                servers.forEach { name -> addItem(name) }
                selectedItem = current
            }
        } catch (e: Throwable) {
            invokeLater(ModalityState.any()) {
                val current = selectedItem
                removeAllItems()
                selectedItem = current
            }
        }
    }
}

fun JComboBox<String>.populateDatabaseUI(settings: QueryProcessorSettings?) {
    if (settings == null) return
    executeOnPooledThread {
        try {
            val databases = settings.session.databases
            invokeLater(ModalityState.any()) {
                val current = selectedItem
                removeAllItems()
                databases.forEach { name -> addItem(name) }
                selectedItem = current
            }
        } catch (e: Throwable) {
            invokeLater(ModalityState.any()) {
                val current = selectedItem
                removeAllItems()
                selectedItem = current
            }
        }
    }
}
