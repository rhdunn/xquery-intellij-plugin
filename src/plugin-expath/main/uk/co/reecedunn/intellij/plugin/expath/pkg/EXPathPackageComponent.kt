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

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class EXPathPackageComponent(private val xml: XmlElement, val moduleType: XdmModuleType) {
    val importUri: XsAnyUriValue? by lazy {
        xml.children().map {
            when (it.element.localName) {
                "import-uri" -> it.text()
                "namespace" -> if (moduleType === XdmModuleType.XQuery) it.text() else null
                else -> null
            }
        }.filterNotNull().firstOrNull()?.let {
            XsAnyUri(it, XdmUriContext.Namespace, XdmModuleType.NONE, null as? PsiElement?)
        }
    }

    val file: String? by lazy { xml.children("file").firstOrNull()?.text() }
}
