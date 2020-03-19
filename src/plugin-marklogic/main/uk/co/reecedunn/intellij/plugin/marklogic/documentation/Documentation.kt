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

import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApis
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQueryProvider
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.lang.XdmProductType
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration
import java.io.File

private class FunctionDocumentation(docs: List<String?>) : XQDocFunctionDocumentation {
    override val moduleTypes: Array<XdmModuleType> = arrayOf(XdmModuleType.XQuery, XdmModuleType.XPath)
    override val href: String? = null
    override val summary: String? = docs[0]
    override val notes: String? = null
    override val examples: Sequence<String> = sequenceOf()

    override val operatorMapping: String? = null
    override val signatures: String? = docs[1]
    override val parameters: String? = docs[2]
    override val properties: String? = null
    override val privileges: String? = null
    override val rules: String? = null
    override val errorConditions: String? = null
}

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

    private val query = CacheableProperty {
        val s9api = QueryProcessorApis.find { api -> api.id == "saxon.s9api" }!!
        val saxon = s9api.newInstanceManager(javaClass.classLoader, null).create() as RunnableQueryProvider
        val query = saxon.createRunnableQuery(MarkLogicQueries.ApiDocs, XQuery)
        query
    }

    private val apidocs = CacheableProperty {
        XQDocDocumentationDownloader.getInstance().load(this, download = true)?.let {
            val docs = XmlDocument.parse(ML_DOC_TEMPLATE, NAMESPACES)
            it.inputStream.unzip { entry, stream ->
                if (entry.name.contains("pubs/raw/apidoc") && entry.name.endsWith(".xml")) {
                    val xml = XmlDocument.parse(stream, NAMESPACES)
                    if (xml.root.`is`("apidoc:module")) {
                        val node = docs.doc.importNode(xml.root.element, true)
                        docs.root.appendChild(node)
                    }
                }
            }
            docs.save(File(it.path.replace("\\.zip$".toRegex(), ".xml")))
            query.get()!!.bindContextItem(docs, "document-node()")
            docs
        }
    }

    override fun invalidate() {
        apidocs.invalidate()
    }

    override fun lookup(ref: XdmFunctionReference): XQDocFunctionDocumentation? {
        apidocs.get()
        return ref.functionName?.let {
            query.get()?.bindVariable("namespace", it.namespace?.data, "xs:string")
            query.get()?.bindVariable("local-name", it.localName?.data, "xs:string")
            val ret = query.get()!!.run().results.map { result -> (result.value as String).nullize() }
            ret.takeIf { results -> results.isNotEmpty() }?.let { results ->
                FunctionDocumentation(results)
            }
        }
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XQDocDocumentation? {
        apidocs.get()
        return null
    }

    // endregion

    companion object {
        private val NAMESPACES = mapOf(
            "apidoc" to "http://marklogic.com/xdmp/apidoc"
        )

        // language=xml
        private const val ML_DOC_TEMPLATE = "<apidoc:apidoc xmlns:apidoc=\"http://marklogic.com/xdmp/apidoc\"/>"
    }
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

    override fun invalidate() {}

    override fun lookup(ref: XdmFunctionReference): XQDocFunctionDocumentation? {
        return (MARKLOGIC_10 as XQDocDocumentationIndex).lookup(ref)
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XQDocDocumentation? {
        return (MARKLOGIC_10 as XQDocDocumentationIndex).lookup(decl)
    }

    // endregion
}
