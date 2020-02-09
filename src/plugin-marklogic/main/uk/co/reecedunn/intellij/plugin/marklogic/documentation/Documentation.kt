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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.lang.XdmProductType
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

private data class MarkLogicZippedDocumentation(
    override val version: String,
    private val zip: String
) : XQDocDocumentationSource, XQDocDocumentationIndex {
    // region XdmDocumentationSource

    override val name: String = "MarkLogic"

    override val href: String = "https://docs.marklogic.com/$zip"

    override val path: String = "marklogic/$zip"

    // endregion
    // region XdmDocumentationIndex

    private val apidocs = CacheableProperty {
        val file = XQDocDocumentationDownloader.getInstance().load(this)
        file?.let { ApiDocs.create(it) }
    }

    override fun invalidate() {
        apidocs.invalidate()
    }

    override fun lookup(ref: XdmFunctionReference): XdmFunctionDocumentation? {
        return apidocs.get()?.lookup(ref)
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XdmDocumentation? {
        return apidocs.get()?.lookup(decl)
    }

    // endregion
}

object MarkLogicProductDocumentation : XdmProductType, XQDocDocumentationSourceProvider, XQDocDocumentationIndex {
    // region XdmProductType

    override val id: String = "marklogic"

    override val name = "MarkLogic"

    override val moduleTypes: Array<XdmModuleType> = arrayOf(
        XdmModuleType.XQuery,
        XdmModuleType.XPath,
        XdmModuleType.JavaScript
    )

    // endregion
    // region XdmDocumentationSourceProvider

    val MARKLOGIC_6: XQDocDocumentationSource = MarkLogicZippedDocumentation("6.0", "MarkLogic_6_pubs.zip")
    val MARKLOGIC_7: XQDocDocumentationSource = MarkLogicZippedDocumentation("7.0", "MarkLogic_7_pubs.zip")
    val MARKLOGIC_8: XQDocDocumentationSource = MarkLogicZippedDocumentation("8.0", "MarkLogic_8_pubs.zip")
    val MARKLOGIC_9: XQDocDocumentationSource = MarkLogicZippedDocumentation("9.0", "MarkLogic_9_pubs.zip")
    val MARKLOGIC_10: XQDocDocumentationSource = MarkLogicZippedDocumentation("10.0", "MarkLogic_10_pubs.zip")

    override val sources: List<XQDocDocumentationSource> = listOf(
        MARKLOGIC_6,
        MARKLOGIC_7,
        MARKLOGIC_8,
        MARKLOGIC_9,
        MARKLOGIC_10
    )

    // endregion
    // region XdmDocumentationIndex

    override fun invalidate() = (MARKLOGIC_10 as XQDocDocumentationIndex).invalidate()

    override fun lookup(ref: XdmFunctionReference): XdmFunctionDocumentation? {
        return (MARKLOGIC_10 as XQDocDocumentationIndex).lookup(ref)
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XdmDocumentation? {
        return (MARKLOGIC_10 as XQDocDocumentationIndex).lookup(decl)
    }

    // endregion
}
