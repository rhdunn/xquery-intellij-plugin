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

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionFilter
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.eqname
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup

object MethodValueTypeFilter : CompletionFilter {
    override fun accepts(element: PsiElement, context: ProcessingContext): Boolean {
        val attribute = element.toXmlAttributeValue()?.attribute ?: return false
        return attribute.localName == "any-of" && attribute.parent.eqname(Rewriter.NAMESPACES) == "rw:match-method"
    }
}

object MethodValueTypeProvider : CompletionProviderEx {
    private val METHOD_VALUE_TYPES = listOf(
        XPathKeywordLookup("ACL"),
        XPathKeywordLookup("CONNECT"),
        XPathKeywordLookup("COPY"),
        XPathKeywordLookup("DELETE"),
        XPathKeywordLookup("GET"),
        XPathKeywordLookup("HEAD"),
        XPathKeywordLookup("LOCK"),
        XPathKeywordLookup("MKCALENDAR"),
        XPathKeywordLookup("MKCOL"),
        XPathKeywordLookup("MOVE"),
        XPathKeywordLookup("OPTIONS"),
        XPathKeywordLookup("PATCH"),
        XPathKeywordLookup("POST"),
        XPathKeywordLookup("PROPFIND"),
        XPathKeywordLookup("PROPPATCH"),
        XPathKeywordLookup("PUT"),
        XPathKeywordLookup("REPORT"),
        XPathKeywordLookup("TRACE"),
        XPathKeywordLookup("UNLOCK")
    )

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        result.addAllElements(METHOD_VALUE_TYPES)
    }
}
