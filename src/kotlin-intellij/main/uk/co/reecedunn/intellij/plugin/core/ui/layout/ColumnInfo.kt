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
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.util.ui.ColumnInfo
import javax.swing.table.TableCellRenderer

private abstract class ColumnInfoImpl<Item, Aspect : Comparable<Aspect>>(
    heading: String,
    private val sortable: Boolean = true
) : ColumnInfo<Item, Aspect>(heading) {

    abstract override fun valueOf(item: Item): Aspect

    private val compare: Comparator<Item> = compareBy { valueOf(it) }

    override fun getComparator(): Comparator<Item>? = if (sortable) compare else null
}

fun <Item, Aspect : Comparable<Aspect>> columnInfo(
    heading: String,
    getter: (Item) -> Aspect,
    sortable: Boolean = true,
    renderer: (Item) -> TableCellRenderer? = { null }
): ColumnInfo<Item, Aspect> {
    return object : ColumnInfoImpl<Item, Aspect>(heading, sortable) {
        override fun valueOf(item: Item): Aspect = getter(item)

        override fun getRenderer(item: Item): TableCellRenderer? = renderer(item)
    }
}
