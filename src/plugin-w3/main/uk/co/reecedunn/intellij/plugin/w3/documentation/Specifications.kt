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
package uk.co.reecedunn.intellij.plugin.w3.documentation

import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.w3.intellij.resources.W3Icons
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType
import javax.swing.Icon

object FunctionsAndOperatorsDocumentation :
    ItemPresentation, XpmSpecificationType, XQDocDocumentationSourceProvider, XQDocDocumentationIndex {
    // region Namespaces

    private val NAMESPACES_10_20030502 = mapOf(
        "http://www.w3.org/2003/05/xpath-functions" to "fn"
    )

    private val NAMESPACES_10 = mapOf(
        "http://www.w3.org/2005/xpath-functions" to "fn"
    )

    private val NAMESPACES_30 = mapOf(
        "http://www.w3.org/2005/xpath-functions" to "fn",
        "http://www.w3.org/2005/xpath-functions/math" to "math"
    )

    private val NAMESPACES_31 = mapOf(
        "http://www.w3.org/2005/xpath-functions/array" to "array",
        "http://www.w3.org/2005/xpath-functions" to "fn",
        "http://www.w3.org/2005/xpath-functions/map" to "map",
        "http://www.w3.org/2005/xpath-functions/math" to "math"
    )

    // endregion
    // region Specifications

    val WD_1_0_20030502: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2003/WD-xpath-functions-20030502/",
        "1.0-20030502", "1.0 (Working Draft 02 May 2003)",
        NAMESPACES_10_20030502 // Used by the MarkLogic 0.9-ml XQuery version.
    )

    val REC_1_0_20070123: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2007/REC-xpath-functions-20070123/",
        "1.0-20070123", "1.0 (First Edition)", NAMESPACES_10
    )

    val REC_1_0_20101214: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2010/REC-xpath-functions-20101214/",
        "1.0-20101214", "1.0 (Second Edition)", NAMESPACES_10
    )

    val WD_3_0_20111213: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "http://www.w3.org/TR/2011/WD-xpath-functions-30-20111213/",
        "3.0-20111213", "3.0 (Working Draft 13 Dec 2011)", NAMESPACES_30 // Used by the MarkLogic 1.0-ml XQuery version.
    )

    val REC_3_0_20140408: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/",
        "3.0-20140408", "3.0", NAMESPACES_30
    )

    val REC_3_1_20170321: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/",
        "3.1-20170321", "3.1", NAMESPACES_31
    )

    // endregion
    // region ItemPresentation

    override fun getPresentableText(): String? = "XQuery and XPath Functions and Operators"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon? = W3Icons.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xpath-functions"

    override val presentation: ItemPresentation get() = this

    // endregion
    // region XQDocDocumentationSourceProvider

    override val sources: List<XQDocDocumentationSource> = listOf(
        WD_1_0_20030502,
        REC_1_0_20070123,
        REC_1_0_20101214,
        WD_3_0_20111213,
        REC_3_0_20140408,
        REC_3_1_20170321
    )

    // endregion
    // region XQDocDocumentationIndex

    override fun invalidate() {}

    override fun lookup(ref: XdmFunctionReference): XQDocFunctionDocumentation? {
        return (REC_3_1_20170321 as XQDocDocumentationIndex).lookup(ref)
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XQDocDocumentation? {
        return (REC_3_1_20170321 as XQDocDocumentationIndex).lookup(decl)
    }

    // endregion
}

object XsltDocumentation : ItemPresentation, XpmSpecificationType, XQDocDocumentationSourceProvider {
    // region Namespaces

    private val NAMESPACES = mapOf<String, String>()

    // endregion
    // region Specifications

    val REC_1_0_19991116: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "http://www.w3.org/TR/1999/REC-xslt-19991116/", "1.0-19991116", "1.0", NAMESPACES
    )

    val REC_2_0_20070123: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "http://www.w3.org/TR/2007/REC-xslt20-20070123/", "2.0-20070123", "2.0", NAMESPACES
    )

    val REC_3_0_20170608: XQDocDocumentationSource = W3CSpecificationDocument(
        this, "https://www.w3.org/TR/2017/REC-xslt-30-20170608/", "3.0-20170608", "3.0", NAMESPACES
    )

    // endregion
    // region ItemPresentation

    override fun getPresentableText(): String? = "XSL Transformations (XSLT)"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon? = W3Icons.Product

    // endregion
    // region XpmSpecificationType

    override val id: String = "xslt"

    override val presentation: ItemPresentation get() = this

    // endregion
    // region XQDocDocumentationSourceProvider

    override val sources: List<XQDocDocumentationSource> = listOf(
        REC_1_0_19991116,
        REC_2_0_20070123,
        REC_3_0_20170608
    )

    // endregion
}
