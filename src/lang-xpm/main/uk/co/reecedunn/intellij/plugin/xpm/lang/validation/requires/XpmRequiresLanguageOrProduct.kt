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
package uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires

import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.resources.XpmBundle

class XpmRequiresLanguageOrProduct(
    private val languageVersion: XpmRequiresLanguageVersion,
    private val requires: XpmRequiresConformanceTo
) : XpmRequiresConformanceTo {
    override fun conformanceTo(configuration: XpmLanguageConfiguration): Boolean {
        return requires.conformanceTo(configuration) || languageVersion.conformanceTo(configuration)
    }

    override fun message(
        configuration: XpmLanguageConfiguration,
        conformanceName: String?
    ): String {
        return when (conformanceName) {
            null -> XpmBundle.message(
                "diagnostic.unsupported-language-version",
                languageVersion.requires.language.displayName,
                configuration.language.version,
                this
            )
            else -> XpmBundle.message(
                "diagnostic.unsupported-language-version-name",
                languageVersion.requires.language.displayName,
                configuration.language.version,
                this,
                conformanceName
            )
        }
    }

    override fun or(requires: XpmRequiresConformanceTo): XpmRequiresConformanceTo {
        throw UnsupportedOperationException()
    }

    override fun toString(): String = sequenceOf(languageVersion, requires).joinToString(XpmBundle.message("diagnostic.or"))
}
