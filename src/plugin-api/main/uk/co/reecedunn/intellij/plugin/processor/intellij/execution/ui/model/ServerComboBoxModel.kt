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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.model

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings

class ServerComboBoxModel : QueryServerComboBoxModel() {
    fun update(settings: QueryProcessorSettings?, database: String) {
        if (settings == null) return
        executeOnPooledThread {
            val items = settings.session.servers
            try {
                invokeLater(ModalityState.any()) {
                    update(items.asSequence().filter { it.database == database }.map { it.server }.toList())
                }
            } catch (e: Throwable) {
                update(listOf())
            }
        }
    }
}
