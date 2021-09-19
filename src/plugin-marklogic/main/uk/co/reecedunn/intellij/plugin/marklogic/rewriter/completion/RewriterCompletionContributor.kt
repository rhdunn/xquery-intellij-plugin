/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.core.completion.schemaListCompletions
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.Rewriter
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider

class RewriterCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val (attribute, accessors) = XmlAccessorsProvider.attribute(parameters.position) ?: return

        val element = accessors.parent(attribute) ?: return
        if (accessors.namespaceUri(element) != Rewriter.NAMESPACE) return

        when (accessors.localName(attribute)) {
            "all-of" -> when (accessors.localName(element)) {
                "match-execute-privilege" -> result.addAllElements(EXECUTE_PRIVILEGE)
            }
            "any-of" -> when (accessors.localName(element)) {
                "match-accept" -> result.addAllElements(MIMETYPE)
                "match-content-type" -> result.addAllElements(MIMETYPE)
                "match-execute-privilege" -> result.addAllElements(EXECUTE_PRIVILEGE)
                "match-method" -> result.addAllElements(parameters.schemaListCompletions(HTTP_METHOD))
            }
            "name" -> when (accessors.localName(element)) {
                "match-header" -> result.addAllElements(HTTP_HEADER)
            }
        }
    }

    companion object {
        private fun completionList(filename: String): Sequence<String> {
            return ResourceVirtualFile.create(this::class.java.classLoader, filename).decode()!!.lineSequence()
        }

        private val EXECUTE_PRIVILEGE: List<LookupElement> by lazy {
            completionList("code-completion/execute-privilege.lst").map { LookupElementBuilder.create(it) }.toList()
        }

        private val HTTP_HEADER: List<LookupElement> by lazy {
            completionList("code-completion/http-header.lst").map { LookupElementBuilder.create(it) }.toList()
        }

        private val HTTP_METHOD: List<String> by lazy {
            completionList("code-completion/http-method.lst").toList()
        }

        private val MIMETYPE: List<LookupElement> by lazy {
            val mimetypes = HashSet<String>()

            Language.getRegisteredLanguages().forEach { language ->
                mimetypes.addAll(language.mimeTypes)

                mimetypes.add("application/rdf+json")
                mimetypes.add("application/rdf+xml")
                mimetypes.add("application/sparql-query")
                mimetypes.add("application/sparql-update")
                mimetypes.add("application/sql")
                mimetypes.add("application/trig")
                mimetypes.add("application/xslt+xml")

                mimetypes.add("text/csv")
                mimetypes.add("text/n-quads")
                mimetypes.add("text/n-triples")
                mimetypes.add("text/n3")
                mimetypes.add("text/turtle")
            }

            mimetypes.map { LookupElementBuilder.create(it) }
        }
    }
}
