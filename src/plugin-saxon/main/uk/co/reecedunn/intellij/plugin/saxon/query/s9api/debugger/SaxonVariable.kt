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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger

import com.intellij.xdebugger.frame.*
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.QueryResultsValue
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.SaxonQueryResultIterator
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om.Sequence
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om.StructuredQName
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons

class SaxonVariable(name: StructuredQName, private val results: Sequence, private val processor: Processor) :
    XNamedValue("\$$name") {

    var value: XValue? = null

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        executeOnPooledThread {
            val values = SaxonQueryResultIterator(results.getXdmValue().iterator(), processor).asSequence().toList()
            value = QueryResultsValue(values, XPathIcons.Nodes.Variable)
            value?.computePresentation(node, place)
        }
    }

    override fun computeChildren(node: XCompositeNode) {
        value?.computeChildren(node)
    }
}
