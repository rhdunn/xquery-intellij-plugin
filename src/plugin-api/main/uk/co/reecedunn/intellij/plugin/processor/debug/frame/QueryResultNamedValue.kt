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
package uk.co.reecedunn.intellij.plugin.processor.debug.frame

import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.presentation.QueryValuePresentation
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

class QueryResultNamedValue(name: String, private val result: QueryResult) : XNamedValue(name) {
    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        val presentation = QueryValuePresentation.forValue(result.value.toString(), result.type)
        node.setPresentation(null, presentation, false)
    }
}
