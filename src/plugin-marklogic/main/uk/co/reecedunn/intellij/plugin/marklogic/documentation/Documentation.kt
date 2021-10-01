/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.run.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.run.RunnableQueryProvider
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.lang.documentation.XpmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import java.io.File
import java.util.*

private class FunctionDocumentation(docs: List<String?>) : XQDocFunctionDocumentation {
    override val moduleTypes: Array<XdmModuleType> = arrayOf(XdmModuleType.XQuery, XdmModuleType.XPath)
    override val href: String? = docs[0]
    override val summary: String? = docs[1]
    override val notes: String? = docs[5]
    override val examples: String? = docs[6]

    override val operatorMapping: String? = null // W3C docs only.
    override val signatures: String? = docs[2]
    override val parameters: String? = docs[3]
    override val properties: String? = null // W3C docs only.
    override val privileges: String? = docs[4]
    override val rules: String? = null // W3C docs only.
    override val errorConditions: String? = null // W3C/xqDocs docs only.
}

private data class MarkLogicZippedDocumentation(
    override val version: String,
    private val zip: String
) : UserDataHolderBase(), XpmDocumentationSource {
    companion object {
        private val APIDOCS = Key.create<Optional<XmlDocument>>("APIDOCS")

        private val NAMESPACES = mapOf(
            "apidoc" to "http://marklogic.com/xdmp/apidoc"
        )

        // language=xml
        private const val ML_DOC_TEMPLATE = "<apidoc:apidoc xmlns:apidoc=\"http://marklogic.com/xdmp/apidoc\"/>"
    }
    // region XQDocDocumentationSource

    override val presentation: ItemPresentation = MarkLogic

    override val href: String = "https://docs.marklogic.com/$zip"

    override val path: String = "marklogic/$zip"

    // endregion
    // region XQDocDocumentationIndex

    private val query: RunnableQuery by lazy {
        val s9api = QueryProcessorApi.apis.find { api -> api.id == "saxon.s9api" }!!
        val saxon = s9api.newInstanceManager(javaClass.classLoader, null).create() as RunnableQueryProvider
        val query = saxon.createRunnableQuery(MarkLogicQueries.ApiDocs, XQuery)
        query
    }

    private val apidocs: XmlDocument?
        get() = computeUserDataIfAbsent(APIDOCS) {
            Optional.ofNullable(XQDocDocumentationDownloader.getInstance().load(this, download = true)?.let {
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
                query.bindContextItem(docs, "document-node()")
                query.bindVariable("marklogic-version", version, "xs:string")
                docs
            })
        }.orElse(null)

    fun invalidate() {
        clearUserData(APIDOCS)
        query.bindContextItem("1", "xs:integer") // Can't assign an empty sequence.
    }

    private fun isMarkLogicNamespace(namespace: String?): Boolean = when {
        namespace == null -> false
        namespace.startsWith("http://marklogic.com/") -> true
        namespace == "http://exslt.org/common" -> true
        namespace == "http://www.georss.org/georss" -> true
        namespace == "http://www.w3.org/1999/02/22-rdf-syntax-ns#" -> true
        // NOTE: The fn namespace is handled by the W3C documentation.
        else -> false
    }

    fun lookup(ref: XpmFunctionReference): XQDocFunctionDocumentation? = ref.functionName?.let {
        if (isMarkLogicNamespace(it.namespace?.data)) {
            apidocs // Ensure the apidocs are loaded
            query.bindVariable("namespace", it.namespace?.data, "xs:string")
            query.bindVariable("local-name", it.localName?.data, "xs:string")
            val ret = query.run().results.map { result -> (result.value as String).nullize() }
            ret.takeIf { results -> results.isNotEmpty() }?.let { results ->
                FunctionDocumentation(results)
            }
        } else
            null
    }

    // endregion
}

object MarkLogicProductDocumentation : XQDocDocumentationSourceProvider, XQDocDocumentationIndex {
    // region XQDocDocumentationSourceProvider

    val MARKLOGIC_6: XpmDocumentationSource = MarkLogicZippedDocumentation("6.0", "MarkLogic_6_pubs.zip")
    val MARKLOGIC_7: XpmDocumentationSource = MarkLogicZippedDocumentation("7.0", "MarkLogic_7_pubs.zip")
    val MARKLOGIC_8: XpmDocumentationSource = MarkLogicZippedDocumentation("8.0", "MarkLogic_8_pubs.zip")
    val MARKLOGIC_9: XpmDocumentationSource = MarkLogicZippedDocumentation("9.0", "MarkLogic_9_pubs.zip")
    val MARKLOGIC_10: XpmDocumentationSource = MarkLogicZippedDocumentation("10.0", "MarkLogic_10_pubs.zip")

    override val sources: List<XpmDocumentationSource> = listOf(
        MARKLOGIC_6,
        MARKLOGIC_7,
        MARKLOGIC_8,
        MARKLOGIC_9,
        MARKLOGIC_10
    )

    // endregion
    // region XQDocDocumentationIndex

    override fun invalidate(source: XpmDocumentationSource) {
        (source as? MarkLogicZippedDocumentation)?.invalidate()
    }

    override fun lookup(ref: XpmFunctionReference): XQDocFunctionDocumentation? {
        return (MARKLOGIC_10 as MarkLogicZippedDocumentation).lookup(ref)
    }

    override fun lookup(decl: XpmNamespaceDeclaration): XQDocDocumentation? = null

    // endregion
}
