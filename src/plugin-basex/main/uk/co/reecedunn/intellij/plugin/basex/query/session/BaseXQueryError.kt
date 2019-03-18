/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val RE_BASEX_EXCEPTION =
    "^(Stopped at (.+), ([0-9]+)/([0-9]+):[\r\n]+)?\\[([^]]+)] (.*)".toRegex()
private val RE_BASEX_EXCEPTION_LINE_COL =
    "^(Stopped at ()line ([0-9]+), column ([0-9]+):[\r\n]+)?\\[([^]]+)] (.*)".toRegex()

class BaseXQueryError(msg: String) : QueryError() {
    private val parts by lazy {
        RE_BASEX_EXCEPTION.matchEntire(msg)?.groupValues
            ?: RE_BASEX_EXCEPTION_LINE_COL.matchEntire(msg)?.groupValues
    }

    override val standardCode: String =
        parts?.get(5) ?: throw RuntimeException("Unable to parse BaseX error message: $msg")

    override val vendorCode: String? = null

    override val description: String? = parts?.get(6)

    override val frames: List<StackFrame> by lazy {
        val path = parts?.get(2)?.let { if (it == "." || it.isEmpty()) null else it }
        val line = parts?.get(3)?.toIntOrNull()
        val col = parts?.get(4)?.toIntOrNull()
        listOf(StackFrame(path, line, col))
    }
}
