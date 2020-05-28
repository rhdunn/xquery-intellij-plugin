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
package uk.co.reecedunn.intellij.plugin.xslt.completion.xpath.property

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.core.xml.attribute
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT

object XPathSyntaxSubset : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.XPATH_SUBSET] == null) {
            context.put(XPathCompletionProperty.XPATH_SUBSET, get(element))
        }
    }

    fun get(element: PsiElement): XPathSubset {
        val attribute = element.toXmlAttributeValue()?.attribute ?: return XPathSubset.Unknown
        val parent = attribute.parent
        if (parent.namespace != XSLT.NAMESPACE) return XPathSubset.Unknown
        return when (attribute.localName) {
            "context-item" -> when (parent.localName) {
                "evaluate" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "count" -> when (parent.localName) {
                "number" -> XPathSubset.XsltPattern // XSLT 1.0
                else -> XPathSubset.Unknown
            }
            "for-each-item" -> when (parent.localName) {
                "merge-source" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "for-each-source" -> when (parent.localName) {
                "merge-source" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "from" -> when (parent.localName) {
                "number" -> XPathSubset.XsltPattern // XSLT 1.0
                else -> XPathSubset.Unknown
            }
            "group-adjacent" -> when (parent.localName) {
                "for-each-group" -> XPathSubset.XPath // XSLT 2.0
                else -> XPathSubset.Unknown
            }
            "group-by" -> when (parent.localName) {
                "for-each-group" -> XPathSubset.XPath // XSLT 2.0
                else -> XPathSubset.Unknown
            }
            "group-ending-with" -> when (parent.localName) {
                "for-each-group" -> XPathSubset.XsltPattern // XSLT 2.0
                else -> XPathSubset.Unknown
            }
            "group-starting-with" -> when (parent.localName) {
                "for-each-group" -> XPathSubset.XsltPattern // XSLT 2.0
                else -> XPathSubset.Unknown
            }
            "initial-value" -> when (parent.localName) {
                "accumulator" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "key" -> when (parent.localName) {
                "map-entry" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "match" -> when (parent.localName) {
                "accumulator-rule" -> XPathSubset.XsltPattern // XSLT 3.0
                "key" -> XPathSubset.XsltPattern // XSLT 1.0
                "template" -> XPathSubset.XsltPattern // XSLT 1.0
                else -> XPathSubset.Unknown
            }
            "namespace-context" -> when (parent.localName) {
                "evaluate" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "select" -> when (parent.localName) {
                "accumulator-rule" -> XPathSubset.XPath // XSLT 3.0
                "analyze-string" -> XPathSubset.XPath // XSLT 2.0
                "apply-templates" -> XPathSubset.XPath // XSLT 1.0 [node-set; sequence]
                "assert" -> XPathSubset.XPath // XSLT 3.0
                "attribute" -> XPathSubset.XPath // XSLT 2.0
                "break" -> XPathSubset.XPath // XSLT 3.0
                "catch" -> XPathSubset.XPath // XSLT 3.0
                "comment" -> XPathSubset.XPath // XSLT 2.0
                "copy" -> XPathSubset.XPath // XSLT 3.0
                "copy-of" -> XPathSubset.XPath // XSLT 1.0
                "for-each" -> XPathSubset.XPath // XSLT 1.0 [node-set; sequence]
                "for-each-group" -> XPathSubset.XPath // XSLT 2.0
                "iterate" -> XPathSubset.XPath // XSLT 3.0
                "map-entry" -> XPathSubset.XPath // XSLT 3.0
                "merge-key" -> XPathSubset.XPath // XSLT 3.0
                "merge-source" -> XPathSubset.XPath // XSLT 3.0
                "message" -> XPathSubset.XPath // XSLT 2.0
                "namespace" -> XPathSubset.XPath // XSLT 2.0
                "number" -> XPathSubset.XPath // XSLT 2.0
                "on-completion" -> XPathSubset.XPath // XSLT 3.0
                "on-empty" -> XPathSubset.XPath // XSLT 3.0
                "on-non-empty" -> XPathSubset.XPath // XSLT 3.0
                "param" -> XPathSubset.XPath // XSLT 1.0
                "perform-sort" -> XPathSubset.XPath // XSLT 2.0
                "processing-instruction" -> XPathSubset.XPath // XSLT 2.0
                "sequence" -> XPathSubset.XPath // XSLT 2.0
                "sort" -> XPathSubset.XPath // XSLT 1.0 [string]
                "try" -> XPathSubset.XPath // XSLT 3.0
                "value-of" -> XPathSubset.XPath // XSLT 1.0 [string]
                "variable" -> XPathSubset.XPath // XSLT 1.0
                "with-param" -> XPathSubset.XPath // XSLT 1.0
                else -> XPathSubset.Unknown
            }
            "test" -> when (parent.localName) {
                "assert" -> XPathSubset.XPath // XSLT 3.0
                "if" -> XPathSubset.XPath // XSLT 1.0 [boolean]
                "when" -> XPathSubset.XPath // XSLT 1.0 [boolean]
                else -> XPathSubset.Unknown
            }
            "use" -> when (parent.localName) {
                "key" -> XPathSubset.XPath // XSLT 1.0
                else -> XPathSubset.Unknown
            }
            "use-when" -> XPathSubset.XPath
            "value" -> when (parent.localName) {
                "number" -> XPathSubset.XPath // XSLT 1.0 [number]
                else -> XPathSubset.Unknown
            }
            "with-params" -> when (parent.localName) {
                "evaluate" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            "xpath" -> when (parent.localName) {
                "evaluate" -> XPathSubset.XPath // XSLT 3.0
                else -> XPathSubset.Unknown
            }
            else -> XPathSubset.Unknown
        }
    }
}
