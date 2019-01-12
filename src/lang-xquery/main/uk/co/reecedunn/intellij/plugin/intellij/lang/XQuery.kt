/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher
import com.intellij.openapi.fileTypes.FileNameMatcher
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageExtensions

/**
 * XML Query Language
 */
object XQuery : Language(findLanguageByID(XPath.id), "XQuery", "application/xquery"), LanguageExtensions {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    // endregion
    // region LanguageExtensions

    override val associations: List<FileNameMatcher> = listOf(
        ExtensionFileNameMatcher("xq"),     // standard extension
        ExtensionFileNameMatcher("xqy"),    // standard extension
        ExtensionFileNameMatcher("xquery"), // standard extension
        ExtensionFileNameMatcher("xql")     // XQuery Language (main) file [eXist-db; BaseX]
    )

    // endregion
}
