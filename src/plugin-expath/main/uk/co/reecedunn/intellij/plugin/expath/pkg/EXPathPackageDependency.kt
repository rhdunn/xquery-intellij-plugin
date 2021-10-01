/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.expath.pkg

import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri

data class EXPathPackageDependency(private val xml: XmlElement) {
    val pkg: XsAnyUriValue? by lazy {
        xml.attribute("package")?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE)
        }
    }

    val processor: String? by lazy { xml.attribute("processor") }

    val versions: List<String>? by lazy { xml.attribute("versions")?.split(" ") }

    val semver: String? by lazy { xml.attribute("semver") }

    val semverMin: String? by lazy { xml.attribute("semver-min") }

    val semverMax: String? by lazy { xml.attribute("semver-max") }
}
