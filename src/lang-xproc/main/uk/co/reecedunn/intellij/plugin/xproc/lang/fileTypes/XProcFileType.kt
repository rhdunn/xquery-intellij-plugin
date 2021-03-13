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
package uk.co.reecedunn.intellij.plugin.xproc.lang.fileTypes

import com.intellij.ide.highlighter.XmlLikeFileType
import uk.co.reecedunn.intellij.plugin.xproc.lang.XProc
import uk.co.reecedunn.intellij.plugin.xproc.resources.XProcBundle
import javax.swing.Icon

object XProcFileType : XmlLikeFileType(XProc) {
    override fun getName(): String = "XProc"

    override fun getDescription(): String = XProcBundle.message("language.xproc.file-type.description")

    override fun getDefaultExtension(): String = "xpl"

    override fun getIcon(): Icon? = null
}
