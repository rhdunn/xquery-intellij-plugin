/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.http.mime

import com.intellij.util.ArrayUtil
import org.apache.http.Header

fun Array<Header>.get(header: String): String? {
    val index = ArrayUtil.indexOf(this, header) { a, b -> (a as Header).name == b }
    return if (index >= 0) this[index].value else null
}

class Message internal constructor(private val headers: Array<Header>, val body: String) {
    fun getHeader(header: String): String? = this.headers.get(header)
}
