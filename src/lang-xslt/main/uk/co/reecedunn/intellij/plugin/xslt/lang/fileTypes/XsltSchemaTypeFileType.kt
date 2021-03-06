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
package uk.co.reecedunn.intellij.plugin.xslt.lang.fileTypes

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import uk.co.reecedunn.intellij.plugin.xslt.resources.XsltBundle
import uk.co.reecedunn.intellij.plugin.xslt.resources.XsltIcons
import javax.swing.Icon

class XsltSchemaTypeFileType(language: Language) : LanguageFileType(language) {
    override fun getName(): String = language.id

    override fun getDescription(): String = XsltBundle.message("language.schema-types.file-type.description", name)

    override fun getDefaultExtension(): String = ""

    override fun getIcon(): Icon = XsltIcons.FileType
}
