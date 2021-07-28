/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("Reformat", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Implementation Conformance Checks")
class XQueryConformanceTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryConformanceTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    // region AnyMapTest

    @Test
    fun testAnyMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq")

        val anyMapTestPsi = file.walkTree().filterIsInstance<XPathAnyMapTest>().first()
        val versioned = anyMapTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region DecimalFormatDecl

    @Test
    fun testDecimalFormatDecl() {
        val file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq")

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    @Test
    fun testDecimalFormatDecl_XQuery30Properties() {
        val file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq")

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    @Test
    fun testDecimalFormatDecl_XQuery31Properties() {
        val file = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq")

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryElementType.DF_PROPERTY_NAME))
        assertThat(versioned.conformanceElement.firstChild.elementType, `is`(XQueryTokenType.K_EXPONENT_SEPARATOR))
    }

    // endregion
    // region IntermediateClause (ForClause)

    @Test
    fun testForClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FOR))
    }

    @Test
    fun testForClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FOR))
    }

    @Test
    fun testForClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FOR))
    }

    @Test
    fun testForClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FOR))
    }

    @Test
    fun testForClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FOR))
    }

    // endregion
    // region IntermediateClause (LetClause)

    @Test
    fun testLetClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_LET))
    }

    @Test
    fun testLetClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_LET))
    }

    @Test
    fun testLetClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_LET))
    }

    @Test
    fun testLetClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_LET))
    }

    @Test
    fun testLetClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_LET))
    }

    // endregion
    // region IntermediateClause (OrderByClause)

    @Test
    fun testOrderByClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_ORDER))
    }

    @Test
    fun testOrderByClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_ORDER))
    }

    @Test
    fun testOrderByClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_ORDER))
    }

    @Test
    fun testOrderByClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[4].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[4]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_ORDER))
    }

    @Test
    fun testOrderByClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_ORDER))
    }

    // endregion
    // region IntermediateClause (WhereClause)

    @Test
    fun testWhereClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_WHERE))
    }

    @Test
    fun testWhereClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_WHERE))
    }

    @Test
    fun testWhereClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_WHERE))
    }

    @Test
    fun testWhereClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_WHERE))
    }

    @Test
    fun testWhereClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_WHERE))
    }

    // endregion
    // region IntermediateClause (CountClause)

    @Test
    fun testCountClause() {
        val file = parseResource("tests/parser/xquery-3.0/CountClause.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryCountClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_COUNT))
    }

    // endregion
    // region IntermediateClause (GroupByClause)

    @Test
    fun testGroupByClause() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryGroupByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_GROUP))
    }

    // endregion
    // region MapConstructor

    @Test
    fun testMapConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/MapConstructor.xq")

        val objectConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val versioned = objectConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region ParenthesizedItemType

    @Nested
    @DisplayName("XQuery 3.1 EBNF (216) ParenthesizedItemType")
    internal inner class ParenthesizedItemType {
        @Test
        @DisplayName("item type")
        fun itemType() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
            assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }

        @Test
        @DisplayName("empty sequence")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(FormalSemanticsSpec.REC_1_0_20070123))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }

        @Test
        @DisplayName("occurrence indicator")
        fun occurrenceIndicator() {
            val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.xq")

            val parenthesizedItemTypePsi = file.walkTree().filterIsInstance<XPathParenthesizedItemType>().first()
            val versioned = parenthesizedItemTypePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(FormalSemanticsSpec.REC_1_0_20070123))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }
    }

    // endregion
    // region TypedMapTest

    @Test
    fun testTypedMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq")

        val typedMapTestPsi = file.walkTree().filterIsInstance<XPathTypedMapTest>().first()
        val versioned = typedMapTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_MAP))
    }

    // endregion

    @Nested
    @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; working draft syntax")
        fun workingDraft() {
            val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(3))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.WD_1_0_20030502))
            assertThat(versioned.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(versioned.requiresConformance[2], `is`(until(EXistDB.VERSION_4_0)))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY))
        }

        @Test
        @DisplayName("empty sequence; recommendation syntax")
        fun rec() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_1_0_20070123))
            assertThat(versioned.requiresConformance[1], `is`(EXistDB.VERSION_4_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY_SEQUENCE))
        }
    }
}
