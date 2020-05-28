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
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.fileTypes

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.JavaScriptModule
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicBundle
import javax.swing.Icon

object JavaScriptModuleFileType : LanguageFileType(JavaScriptModule) {
    override fun getName(): String = MarkLogicBundle.message("language.mjs.display-name")

    override fun getDescription(): String = MarkLogicBundle.message("language.mjs.display-name")

    override fun getDefaultExtension(): String = "mjs"

    override fun getIcon(): Icon? = AllIcons.FileTypes.JavaScript
}
