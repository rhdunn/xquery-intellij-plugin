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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger

import com.intellij.xdebugger.frame.*
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.presentation.QueryValuePresentation
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import javax.swing.Icon

class MarkLogicValue(private val results: List<QueryResult>) : XValue() {
    var icon: Icon? = null

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        val presentation = QueryValuePresentation.forResults(results)
        node.setPresentation(icon, presentation, results.size > 1)
    }

    override fun computeChildren(node: XCompositeNode) {
        val children = XValueChildrenList()
        results.withIndex().forEach { (index, result) ->
            children.add(MarkLogicNamedValue(index.toString(), listOf(result)))
        }
        node.addChildren(children, true)
    }
}
