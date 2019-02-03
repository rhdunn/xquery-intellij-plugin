/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import uk.co.reecedunn.intellij.plugin.core.xml.qname

private val XSL_EXPRESSION_ATTRIBUTES = listOf(
    qname("xsl:apply-templates") to qname("select"), // XSLT 1.0 [node-set; sequence]
    qname("xsl:attribute") to qname("select"), // XSLT 2.0
    qname("xsl:comment") to qname("select"), // XSLT 2.0
    qname("xsl:copy-of") to qname("select"), // XSLT 1.0
    qname("xsl:for-each") to qname("select"), // XSLT 1.0 [node-set; sequence]
    qname("xsl:if") to qname("test"), // XSLT 1.0 [boolean]
    qname("xsl:key") to qname("use"), // XSLT 1.0
    qname("xsl:namespace") to qname("select"), // XSLT 2.0
    qname("xsl:number") to qname("value"), // XSLT 1.0 [number]
    qname("xsl:param") to qname("select"), // XSLT 1.0
    qname("xsl:processing-instruction") to qname("select"), // XSLT 2.0
    qname("xsl:sort") to qname("select"), // XSLT 1.0 [string]
    qname("xsl:value-of") to qname("select"), // XSLT 1.0 [string]
    qname("xsl:variable") to qname("select"), // XSLT 1.0
    qname("xsl:when") to qname("test"), // XSLT 1.0 [boolean]
    qname("xsl:with-param") to qname("select") // XSLT 1.0
)

private val XSL_PATTERN_ATTRIBUTES = listOf(
    qname("xsl:accumulator-role") to qname("match"), // XSLT 3.0
    qname("xsl:for-each-group") to qname("group-ending-with"), // XSLT 2.0
    qname("xsl:for-each-group") to qname("group-starting-with"), // XSLT 2.0
    qname("xsl:key") to qname("match"), // XSLT 1.0
    qname("xsl:number") to qname("count"), // XSLT 1.0
    qname("xsl:number") to qname("from"), // XSLT 1.0
    qname("xsl:template") to qname("match") // XSLT 1.0
)

fun PsiElement.isXslExpression(): Boolean {
    return (this as? XmlAttribute)?.let {
        XSL_EXPRESSION_ATTRIBUTES.contains(it.parent.qname() to it.qname())
    } ?: false
}

fun PsiElement.isXslPattern(): Boolean {
    return (this as? XmlAttribute)?.let {
        XSL_PATTERN_ATTRIBUTES.contains(it.parent.qname() to it.qname())
    } ?: false
}
