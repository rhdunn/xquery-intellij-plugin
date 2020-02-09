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
package uk.co.reecedunn.intellij.plugin.intellij.tests.documentation

import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

object DocumentationSourceProvider : XQDocDocumentationSourceProvider, XdmDocumentationIndex {
    // region XdmDocumentationSourceProvider

    override val sources: List<XQDocDocumentationSource> = listOf()

    // endregion
    // region XdmDocumentationIndex

    override fun invalidate() {}

    override fun lookup(ref: XdmFunctionReference): XdmFunctionDocumentation? {
        return object : XdmFunctionDocumentation {
            override val moduleTypes: Array<XdmModuleType> = XdmModuleType.XPATH_OR_XQUERY

            val name: String = ref.functionName?.let {
                "[prefix=${it.prefix?.data ?: "(null)"} namespace=${it.namespace?.data ?: "(null)"} localname=${it.localName?.data ?: "(null)"}]"
            } ?: "(null)"

            override fun href(moduleType: XdmModuleType): String = "function href=${name}#${ref.arity}"

            override fun summary(moduleType: XdmModuleType): String = "function summary=${name}#${ref.arity}"

            override fun notes(moduleType: XdmModuleType): String? = null

            override fun examples(moduleType: XdmModuleType): Sequence<String> = emptySequence()

            override val operatorMapping: String? = null

            override val signatures: String? = null

            override val properties: String? = null

            override val privileges: String? = null

            override fun rules(moduleType: XdmModuleType): String? = null

            override val errorConditions: String? = null
        }
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XdmDocumentation? {
        return object : XdmDocumentation {
            override val moduleTypes: Array<XdmModuleType> = XdmModuleType.XPATH_OR_XQUERY

            val name: String =
                "[prefix=${decl.namespacePrefix?.data ?: "(null)"} namespace=${decl.namespaceUri?.data ?: "(null)"}]"

            override fun href(moduleType: XdmModuleType): String = "module href=${name}"

            override fun summary(moduleType: XdmModuleType): String = "module summary=${name}"

            override fun notes(moduleType: XdmModuleType): String? = null

            override fun examples(moduleType: XdmModuleType): Sequence<String> = emptySequence()
        }
    }

    // endregion
}
