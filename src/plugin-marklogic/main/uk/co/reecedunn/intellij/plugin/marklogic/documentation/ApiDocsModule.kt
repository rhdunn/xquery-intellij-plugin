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

import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class ApiDocsModule(private val xml: XmlElement) : XQDocDocumentation {
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

    val namespaceUri: String? by lazy { importDecl?.groups?.get(2)?.value }

    val locationUri: String? by lazy { importDecl?.groups?.get(3)?.value }

    val functions: List<ApiDocsFunction> by lazy {
        xml.children("apidoc:function").mapNotNull {
            val prefix = it.attribute("lib")!!
            when {
                it.attribute("name") == null -> null
                prefix == lib -> ApiDocsFunction(it, namespaceUri ?: BUILTIN_NAMESPACES[prefix])
                else -> ApiDocsFunction(it, BUILTIN_NAMESPACES[prefix])
            }
        }.toList()
    }

    // endregion
    // region XdmDocumentation

    override val moduleTypes: Array<XdmModuleType> = arrayOf()

    override fun href(moduleType: XdmModuleType): String? = lib?.let {
        when (moduleType) {
            XdmModuleType.XPath, XdmModuleType.XQuery -> "https://docs.marklogic.com/$it"
            XdmModuleType.JavaScript -> "https://docs.marklogic.com/js/$it"
            else -> throw UnsupportedOperationException("No href for $moduleType.")
        }
    }

    override fun summary(moduleType: XdmModuleType): String? = xml.child("apidoc:summary")?.innerXml()

    override fun notes(moduleType: XdmModuleType): String? = null

    override fun examples(moduleType: XdmModuleType): Sequence<String> = emptySequence()

    // endregion

    companion object {
        private val RE_IMPORT_DECL =
            "^import module namespace ([a-zA-Z0-9\\-]+) = \"([^\"]+)\"\\s*at\\s*\"([^\"]+)\"\\s*;\\s*$".toRegex()

        // These are the namespaces that have builtin functions in MarkLogic.
        private val BUILTIN_NAMESPACES = mapOf(
            "cntk" to "http://marklogic.com/cntk", // MarkLogic 10.0
            "cts" to "http://marklogic.com/cts", // MarkLogic 5.0
            "xdmp" to "http://marklogic.com/xdmp" // MarkLogic 5.0
        )
    }
}
