/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationReference
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue

data class ApiDocsModule(private val xml: XmlElement) : XdmDocumentationReference {
    // region apidoc:module

    val name: String by lazy { xml.attribute("name")!! }

    val category: String by lazy { xml.attribute("category")!! }

    val lib: String? by lazy { xml.attribute("lib") }

    val bucket: String? by lazy { xml.attribute("bucket") }

    private val importDecl: MatchResult? by lazy {
        xml.child("apidoc:summary")?.children("p")?.mapNotNull { p ->
            p.child("code")?.text()?.let { text -> RE_IMPORT_DECL.find(text) }
        }?.firstOrNull()
    }

    val namespaceUri: XsAnyUriValue? by lazy {
        importDecl?.groups?.get(2)?.value?.let {
            XsAnyUri(it, XdmUriContext.Namespace, XdmModuleType.MODULE, null as PsiElement?)
        }
    }

    val locationUri: String? by lazy { importDecl?.groups?.get(3)?.value }

    val functions: List<ApiDocsFunction> by lazy {
        xml.children("apidoc:function").mapNotNull {
            when {
                it.attribute("name") == null -> null
                it.attribute("lib") == lib -> ApiDocsFunction(it, namespaceUri)
                else -> ApiDocsFunction(it, null)
            }
        }.toList()
    }

    // endregion
    // region XdmDocumentationReference

    override val href: String? = lib?.let { "https://docs.marklogic.com/$it" }

    override val summary: String? by lazy {
        val summary = xml.child("apidoc:summary")
        summary?.child("p")?.xml() ?: summary?.text()
    }

    // endregion

    companion object {
        private val RE_IMPORT_DECL =
            "^import module namespace ([a-zA-Z0-9\\-]+) = \"([^\"]+)\"\\s*at\\s*\"([^\"]+)\"\\s*;\\s*$".toRegex()
    }
}
