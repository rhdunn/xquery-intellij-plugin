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
package uk.co.reecedunn.intellij.plugin.marklogic.log.fileTypes

import com.intellij.openapi.fileTypes.LanguageFileType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import javax.swing.Icon

object MarkLogicErrorLogFileType : LanguageFileType(MarkLogicErrorLog) {
    override fun getName(): String = "MLErrorLog"

    override fun getDescription(): String = MarkLogicBundle.message("language.error-log.file-type.description")

    override fun getDefaultExtension(): String = ""

    override fun getIcon(): Icon = MarkLogicIcons.Product
}
