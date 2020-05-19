/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
/**
 * IntelliJ defines a DSL for creating UIs, but currently has several disadvantages:
 * 1.  It is not supported on IntelliJ 2019.1 or earlier;
 * 2.  It is classified as being in active development, with potential breaking changes between releases;
 * 3.  It is not necessarily comparable to the swing API in terms of available functionality and flexibility.
 *
 * As such, a swing compatible DSL is defined in this file.
 */
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.ui.ColoredListCellRenderer
import javax.swing.*

// region colored list cell renderer

fun <T> coloredListCellRenderer(
    customize: ColoredListCellRenderer<T>.(
        list: JList<out T>, value: T?, index: Int, selected: Boolean, hasFocus: Boolean
    ) -> Unit
): ColoredListCellRenderer<T> {
    return object : ColoredListCellRenderer<T>() {
        override fun customizeCellRenderer(
            list: JList<out T>,
            value: T?,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) {
            customize(list, value, index, selected, hasFocus)
        }
    }
}

// endregion
