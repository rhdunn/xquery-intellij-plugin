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
package uk.co.reecedunn.intellij.plugin.xpm.resources

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object XpmIcons {
    @Suppress("SameParameterValue")
    private fun getIcon(path: String): Icon = IconLoader.getIcon(path, XpmIcons::class.java)

    object W3 {
        val Product: Icon = getIcon("/icons/w3.png")
    }
}
