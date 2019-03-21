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
package uk.co.reecedunn.intellij.plugin.processor.query

import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame

abstract class QueryError : RuntimeException() {
    override val message: String? get() = description?.let { "[$standardCode] $it" } ?: standardCode

    abstract val standardCode: String

    abstract val vendorCode: String?

    abstract val description: String?

    abstract val value: List<String>

    abstract val frames: List<StackFrame>
}

data class QueryErrorImpl(
    override val standardCode: String,
    override val vendorCode: String?,
    override val description: String?,
    override val value: List<String>,
    override val frames: List<StackFrame>
) : QueryError()
