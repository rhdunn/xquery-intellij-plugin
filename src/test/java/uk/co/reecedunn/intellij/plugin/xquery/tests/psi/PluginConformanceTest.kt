/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathForwardAxis
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTMatchOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTPrimaryWithOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTSelection
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.BaseX
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Saxon
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class PluginConformanceTest : ParserTestCase() {
    // region BinaryTest

    @Test
    fun testBinaryTest() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq")

        val binaryKindTestPsi = file.walkTree().filterIsInstance<PluginBinaryTest>().first()
        val conformance = binaryKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_BINARY))
    }

    // endregion
    // region CompatibilityAnnotation

    @Test
    fun testCompatibilityAnnotation_FunctionDecl() {
        val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_PRIVATE))
    }

    @Test
    fun testCompatibilityAnnotation_VarDecl() {
        val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_PRIVATE))
    }

    // endregion
    // region ForwardAxis

    @Test
    fun testForwardAxis_Namespace() {
        val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_NAMESPACE))
    }

    @Test
    fun testForwardAxis_Property() {
        val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_PROPERTY))
    }

    // endregion
    // region FTFuzzyOption

    @Test
    fun testFTFuzzyOption() {
        val file = parseResource("tests/parser/basex-6.1/FTFuzzyOption.xq")

        val containsExpr = file.descendants().filterIsInstance<FTContainsExpr>().first()
        val selection = containsExpr.children().filterIsInstance<FTSelection>().first()
        val primaryWithOptions = selection.descendants().filterIsInstance<FTPrimaryWithOptions>().first()
        val matchOptions = primaryWithOptions.children().filterIsInstance<FTMatchOptions>().first()
        val fuzzyOption = matchOptions.children().filterIsInstance<PluginFTFuzzyOption>().first()
        val conformance = fuzzyOption as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_6_1))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_FUZZY))
    }

    // endregion
    // region NonDeterministicFunctionCall

    @Test
    fun testNonDeterministicFunctionCall() {
        val file = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.xq")

        val nonDeterministicFunctionCall = file.walkTree().filterIsInstance<PluginNonDeterministicFunctionCall>().first()
        val conformance = nonDeterministicFunctionCall as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_8_4))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NON_DETERMINISTIC))
    }

    // endregion
    // region TupleType

    @Test
    fun testTupleType() {
        val file = parseResource("tests/parser/saxon-9.8/TupleType.xq")

        val tupleTypePsi = file.walkTree().filterIsInstance<PluginTupleType>().first()
        val conformance = tupleTypePsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_TUPLE))
    }

    // endregion
    // region TypeDecl

    @Test
    fun testTypeDecl() {
        val file = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")

        val typeDeclPsi = file.descendants().filterIsInstance<PluginTypeDecl>().first()
        val conformance = typeDeclPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_TYPE))
    }

    // endregion
    // region UnionType

    @Test
    fun testUnionType() {
        val file = parseResource("tests/parser/saxon-9.8/UnionType.xq")

        val unionTypePsi = file.walkTree().filterIsInstance<PluginUnionType>().first()
        val conformance = unionTypePsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UNION))
    }

    // endregion
    // region UpdateExpr

    @Test
    fun testUpdateExpr() {
        val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

        val updateExpr = file.descendants().filterIsInstance<PluginUpdateExpr>().first()
        val conformance = updateExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_7_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UPDATE))
    }

    @Test
    fun testUpdateExpr_Block() {
        val file = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")

        val updateExpr = file.descendants().filterIsInstance<PluginUpdateExpr>().first()
        val conformance = updateExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(BaseX.VERSION_8_5))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region ValidateExpr

    @Test
    fun testValidateExpr_ValidateAs() {
        val file = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq")

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_AS))
    }

    // endregion
}
