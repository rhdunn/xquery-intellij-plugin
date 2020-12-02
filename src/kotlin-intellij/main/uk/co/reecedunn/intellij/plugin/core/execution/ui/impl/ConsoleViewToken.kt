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

import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.ui.ConsoleViewContentType

data class ConsoleViewToken(
    val text: String,
    val contentType: ConsoleViewContentType?,
    val hyperlinkInfo: HyperlinkInfo?
) {
    constructor(text: String, contentType: ConsoleViewContentType) : this(text, contentType, null)

    constructor(text: String, hyperlinkInfo: HyperlinkInfo?) : this(text, null, hyperlinkInfo)
}
