/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.execution.ui.impl

class ConsoleViewTokenBuffer {
    private val tokens: ArrayList<ConsoleViewToken> = arrayListOf()

    var length: Int = 0
        private set

    fun toTypedArray(): Array<ConsoleViewToken> = tokens.toTypedArray()

    fun add(token: ConsoleViewToken) {
        tokens.add(token)
        length += token.text.length
    }

    fun clear() {
        tokens.clear()
        length = 0
    }

    fun isEmpty(): Boolean = length == 0

    fun isNotEmpty(): Boolean = length != 0
}
