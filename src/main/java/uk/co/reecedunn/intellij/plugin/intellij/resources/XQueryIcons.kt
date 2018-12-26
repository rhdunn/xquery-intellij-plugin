/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.resources

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object XQueryIcons {
    private fun getIcon(path: String): Icon = IconLoader.getIcon(path, XQueryIcons::class.java)

    private fun getIcon(oldPath: String, newPath: String): Icon {
        return if (ApplicationInfo.getInstance().build.baselineVersion >= 163)
            getIcon(newPath)
        else
            getIcon(oldPath)
    }

    val XQueryFile: Icon = getIcon("/icons/xquery.png", "/icons/xquery-163.png")
}
