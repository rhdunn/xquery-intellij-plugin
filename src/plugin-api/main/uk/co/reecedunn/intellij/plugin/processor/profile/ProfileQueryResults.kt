/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.profile

import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue

data class FlatProfileEntry(
    val id: String,
    val expression: String,
    val count: Int,
    val shallowTime: XsDurationValue,
    val deepTime: XsDurationValue,
    val frame: StackFrame
)

data class ProfileReport(
    val xml: String?,
    val elapsed: XsDurationValue,
    val created: String,
    val version: String,
    val results: Sequence<FlatProfileEntry>
)

data class ProfileQueryResults(
    val results: List<QueryResult>,
    val report: ProfileReport
)
