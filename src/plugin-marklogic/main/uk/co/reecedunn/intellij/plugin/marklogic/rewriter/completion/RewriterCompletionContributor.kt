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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter

class RewriterCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val attribute = parameters.position.toXmlAttributeValue()?.attribute ?: return

        val element = attribute.parent
        if (element.namespace != Rewriter.NAMESPACE) return

        when {
            acceptsHttpMethod(attribute, element) -> result.addAllElements(HTTP_METHOD)
        }
    }

    private fun acceptsHttpMethod(attribute: XmlAttribute, element: XmlTag): Boolean {
        return attribute.localName == ANY_OF && element.localName == MATCH_METHOD
    }

    companion object {
        const val MATCH_METHOD = "match-method"

        const val ANY_OF = "any-of"

        private val HTTP_METHOD = listOf(
            LookupElementBuilder.create("ACL"),
            LookupElementBuilder.create("CONNECT"),
            LookupElementBuilder.create("COPY"),
            LookupElementBuilder.create("DELETE"),
            LookupElementBuilder.create("GET"),
            LookupElementBuilder.create("HEAD"),
            LookupElementBuilder.create("LOCK"),
            LookupElementBuilder.create("MKCALENDAR"),
            LookupElementBuilder.create("MKCOL"),
            LookupElementBuilder.create("MOVE"),
            LookupElementBuilder.create("OPTIONS"),
            LookupElementBuilder.create("PATCH"),
            LookupElementBuilder.create("POST"),
            LookupElementBuilder.create("PROPFIND"),
            LookupElementBuilder.create("PROPPATCH"),
            LookupElementBuilder.create("PUT"),
            LookupElementBuilder.create("REPORT"),
            LookupElementBuilder.create("TRACE"),
            LookupElementBuilder.create("UNLOCK")
        )
    }
}
