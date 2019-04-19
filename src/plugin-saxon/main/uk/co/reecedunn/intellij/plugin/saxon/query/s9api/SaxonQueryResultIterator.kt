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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmItem

internal class SaxonQueryResultIterator(val results: Any, val classes: SaxonClasses) : Iterator<QueryResult> {
    private val xdmItemClass = classes.loader.loadClass("net.sf.saxon.s9api.XdmItem")

    private val hasNextMethod = classes.xdmSequenceIteratorClass.getMethod("hasNext")
    private val nextMethod = classes.xdmSequenceIteratorClass.getMethod("next")
    private val getItemTypeMethod =
        classes.typeClass.getMethod("getItemType", classes.itemClass, classes.typeHierarchyClass)
    private var position: Long = -1

    override fun hasNext(): Boolean {
        return hasNextMethod.invoke(results) as Boolean
    }

    override fun next(): QueryResult {
        val next = XdmItem(nextMethod.invoke(results), xdmItemClass)
        val value = next.getUnderlyingValue()
        val type = getItemTypeMethod.invoke(null, value, null)
        return QueryResult.fromItemType(++position, next.toString(), type.toString())
    }
}
