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

data class EXPathPackageImport(
    private val xml: XmlElement,
    override val moduleType: XdmModuleType
) : EXPathPackageComponent {
    val importUri: XsAnyUriValue? by lazy {
        xml.children().map {
            when (it.element.localName) {
                "import-uri" -> it.text()
                "namespace" -> {
                    if (moduleType === XdmModuleType.XQuery || moduleType === XdmModuleType.XMLSchema)
                        it.text()
                    else
                        null
                }
                else -> null
            }
        }.filterNotNull().firstOrNull()?.let {
            XsAnyUri(it, XdmUriContext.Namespace, XdmModuleType.NONE)
        }
    }

    override val file: String? by lazy { xml.children("pkg:file").firstOrNull()?.text() }
}
