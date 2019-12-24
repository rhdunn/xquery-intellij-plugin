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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class EXPathPackageDescriptor(private val xml: XmlDocument) {
    constructor(file: VirtualFile) : this(XmlDocument.parse(file, NAMESPACES))

    val name: XsAnyUriValue? by lazy {
        xml.root.attribute("name")?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE, null as? PsiElement?)
        }
    }

    val abbrev: String? by lazy { xml.root.attribute("abbrev") }

    val version: String? by lazy { xml.root.attribute("version") }

    val spec: String? by lazy { xml.root.attribute("spec") }

    val title: String? by lazy { xml.root.children("title").firstOrNull()?.text() }

    companion object {
        val NAMESPACES = mapOf("pkg" to "http://expath.org/ns/pkg")
    }
}
