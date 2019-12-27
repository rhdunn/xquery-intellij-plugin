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
package uk.co.reecedunn.intellij.plugin.w3.documentation

import uk.co.reecedunn.intellij.plugin.intellij.lang.FunctionsAndOperatorsSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSourceProvider

data class W3CSpecificationDocument(
    val spec: Specification,
    override val href: String,
    override val version: String
) : XdmDocumentationSource {
    override val name: String = spec.kind.name

    override val path: String = "w3/${spec.kind.id}/${spec.id}.html"
}

object FunctionsAndOperatorsDocumentation : XdmDocumentationSourceProvider {
    val WD_1_0_20030502 = W3CSpecificationDocument(
        FunctionsAndOperatorsSpec.WD_1_0_20030502, "https://www.w3.org/TR/2003/WD-xpath-functions-20030502/",
        "1.0 (Working Draft 2003 May 02)"
    )

    val REC_1_0_20070123 = W3CSpecificationDocument(
        FunctionsAndOperatorsSpec.REC_1_0_20070123, "https://www.w3.org/TR/2007/REC-xpath-functions-20070123/",
        "1.0 (First Edition)"
    )

    val REC_1_0_20101214 = W3CSpecificationDocument(
        FunctionsAndOperatorsSpec.REC_1_0_20101214, "https://www.w3.org/TR/2010/REC-xpath-functions-20101214/",
        "1.0 (Second Edition)"
    )

    val REC_3_0_20140408 = W3CSpecificationDocument(
        FunctionsAndOperatorsSpec.REC_3_0_20140408, "https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/",
        "3.0"
    )

    val REC_3_1_20170321 = W3CSpecificationDocument(
        FunctionsAndOperatorsSpec.REC_3_1_20170321, "https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/",
        "3.1"
    )

    override val sources: List<XdmDocumentationSource> = listOf(
        WD_1_0_20030502,
        REC_1_0_20070123,
        REC_1_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321
    )
}
