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
package uk.co.reecedunn.intellij.plugin.xqdoc.documentation

import com.intellij.openapi.extensions.ExtensionPointName
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmNamespaceDeclaration

interface XQDocDocumentationSourceProvider {
    companion object {
        val EP_NAME: ExtensionPointName<XQDocDocumentationSourceProviderBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.documentationSourceProvider"
        )

        private val providers: Sequence<XQDocDocumentationSourceProvider>
            get() = EP_NAME.extensionList.asSequence().map { it.getInstance() }

        val allSources: Sequence<XQDocDocumentationSource>
            get() = providers.flatMap { it.sources.asSequence() }

        fun lookup(ref: XpmFunctionReference): Sequence<XQDocDocumentation> = providers.mapNotNull {
            (it as? XQDocDocumentationIndex)?.lookup(ref)
        }

        fun lookup(decl: XpmNamespaceDeclaration): Sequence<XQDocDocumentation> = providers.mapNotNull {
            (it as? XQDocDocumentationIndex)?.lookup(decl)
        }
    }

    val sources: List<XQDocDocumentationSource>
}
