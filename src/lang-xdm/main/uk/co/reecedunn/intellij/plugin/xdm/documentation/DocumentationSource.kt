/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.documentation

import com.intellij.openapi.extensions.ExtensionPointName
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

interface XdmDocumentationSource {
    val name: String

    val version: String

    val href: String

    val path: String
}

interface XdmDocumentationSourceProvider {
    companion object {
        val EP_NAME = ExtensionPointName.create<XdmDocumentationSourceProvider>(
            "uk.co.reecedunn.intellij.documentationSourceProvider"
        )

        val allSources: Sequence<XdmDocumentationSource>
            get() = EP_NAME.extensions.asSequence().flatMap { it.sources.asSequence() }

        fun lookup(ref: XdmFunctionReference): Sequence<XdmDocumentation> {
            return EP_NAME.extensions.asSequence().mapNotNull {
                (it as? XdmDocumentationIndex)?.lookup(ref)
            }
        }

        fun lookup(decl: XdmNamespaceDeclaration): Sequence<XdmDocumentation> {
            return EP_NAME.extensions.asSequence().mapNotNull {
                (it as? XdmDocumentationIndex)?.lookup(decl)
            }
        }
    }

    val sources: List<XdmDocumentationSource>
}
