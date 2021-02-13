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
package uk.co.reecedunn.intellij.plugin.w3.lang.requires

import uk.co.reecedunn.intellij.plugin.xpm.intellij.resources.XpmBundle
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresConformanceTo
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresLanguageVersion
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery

class XpmRequiresLanguageOrMarkLogic(
    private val languageVersion: XpmRequiresLanguageVersion,
    private val requires: XpmRequiresConformanceTo
) : XpmRequiresConformanceTo {
    override fun conformanceTo(configuration: XpmLanguageConfiguration): Boolean = when (configuration.language) {
        XQuery.VERSION_1_0_ML -> requires.conformanceTo(configuration)
        else -> languageVersion.conformanceTo(configuration)
    }

    override fun message(
        configuration: XpmLanguageConfiguration,
        conformanceName: String?
    ): String = when (configuration.language) {
        XQuery.VERSION_1_0_ML -> when (conformanceName) {
            null -> XpmBundle.message("diagnostic.unsupported-syntax", configuration.product, this)
            else -> XpmBundle.message("diagnostic.unsupported-syntax-name", configuration.product, this, conformanceName)
        }
        else -> XpmBundle.message(
            "diagnostic.unsupported-language-version",
            languageVersion.requires.language.displayName,
            configuration.language.version,
            this
        )
    }

    override fun toString(): String = sequenceOf(languageVersion, requires).joinToString(XpmBundle.message("diagnostic.or"))
}
