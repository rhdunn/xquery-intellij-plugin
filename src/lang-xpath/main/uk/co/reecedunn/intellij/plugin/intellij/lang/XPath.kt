/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XPathFileType

/**
 * XML Path Language
 *
 * NOTE: The 'XPath' language ID is used by the IntelliJ plugin for the built-in
 * XPath 1.0 and 2.0 support. Using that causes those plugins to fail.
 */
object XPath : Language("XMLPath"), LanguageExtensions {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = "XPath"

    override fun getAssociatedFileType(): LanguageFileType? = XPathFileType

    // endregion
    // region LanguageExtensions

    override val scriptExtensions: Array<String> = arrayOf(
        "xp",
        "xpath",
        "xpath1",
        "xpath2",
        "xpath3",
        "xpath31",
        "xpa"
    )

    override val defaultExtension: String = "xpath"

    // endregion
}
