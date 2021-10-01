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

data class EXPathPackageDtd(private val xml: XmlElement) : EXPathPackageComponent {
    val publicId: String? by lazy { xml.children("pkg:public-id").firstOrNull()?.text() }

    val systemId: String? by lazy { xml.children("pkg:system-id").firstOrNull()?.text() }

    override val moduleType: XdmModuleType = XdmModuleType.DTD

    override val file: String? by lazy { xml.children("pkg:file").firstOrNull()?.text() }
}
