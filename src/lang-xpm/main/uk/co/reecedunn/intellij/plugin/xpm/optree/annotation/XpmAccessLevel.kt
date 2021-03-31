/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.optree.annotation

import com.intellij.util.PlatformIcons
import javax.swing.Icon

enum class XpmAccessLevel(val sortOrder: Int, val icon: Icon?) {
    Public(2, PlatformIcons.PUBLIC_ICON),
    Private(1, PlatformIcons.PRIVATE_ICON),
    Unknown(-1, null);

    companion object {
        fun get(annotated: XpmAnnotated): XpmAccessLevel = when {
            annotated.annotation(XpmAnnotated.PRIVATE) == null -> Public
            else -> Private
        }
    }
}
