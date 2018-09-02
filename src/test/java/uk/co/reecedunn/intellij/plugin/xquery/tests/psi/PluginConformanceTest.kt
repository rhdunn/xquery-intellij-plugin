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

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathForwardAxis
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTContainsExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTMatchOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTPrimaryWithOptions
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTSelection
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCatchClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryTryCatchExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class PluginConformanceTest : ParserTestCase() {
    // region AnyArrayNodeTest

    @Test
    fun testAnyArrayNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest.xq")

        val arrayTestPsi = file.walkTree().filterIsInstance<PluginAnyArrayNodeTest>().first()
        val conformance = arrayTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region AnyBooleanNodeTest

    @Test
    fun testAnyBooleanNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest.xq")

        val booleanTestPsi = file.walkTree().filterIsInstance<PluginAnyBooleanNodeTest>().first()
        val conformance = booleanTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType, `is`(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region AnyNullNodeTest

    @Test
    fun testAnyNullNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest.xq")

        val nullTestPsi = file.walkTree().filterIsInstance<PluginAnyNullNodeTest>().first()
        val conformance = nullTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region AnyNumberNodeTest

    @Test
    fun testAnyNumberNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest.xq")

        val numberTestPsi = file.walkTree().filterIsInstance<PluginAnyNumberNodeTest>().first()
        val conformance = numberTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region AttributeDeclTest

    @Test
    fun testAttributeDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest.xq")

        val attributeDeclTestPsi = file.walkTree().filterIsInstance<PluginAttributeDeclTest>().first()
        val conformance = attributeDeclTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_ATTRIBUTE_DECL))
    }

    // endregion
    // region BinaryConstructor

    @Test
    fun testBinaryConstructor() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

        val binaryKindTestPsi = file.descendants().filterIsInstance<PluginBinaryConstructor>().first()
        val conformance = binaryKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_BINARY))
    }

    // endregion
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
    // region BooleanConstructor

    @Test
    fun testBooleanConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")

        val booleanConstructorPsi = file.descendants().filterIsInstance<PluginBooleanConstructor>().first()
        val conformance = booleanConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region CatchClause

    @Test
    fun testCatchClause() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val versioned = catchClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_CATCH))
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
    // region ComplexTypeTest

    @Test
    fun testComplexTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest.xq")

        val complexTypeTestPsi = file.walkTree().filterIsInstance<PluginComplexTypeTest>().first()
        val conformance = complexTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_COMPLEX_TYPE))
    }

    // endregion
    // region ElementDeclTest

    @Test
    fun testElementDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ElementDeclTest.xq")

        val elementDeclTestPsi = file.walkTree().filterIsInstance<PluginElementDeclTest>().first()
        val conformance = elementDeclTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_ELEMENT_DECL))
    }

    // endregion
    // region EnclosedExpr (CatchClause)

    @Test
    fun testEnclosedExpr_CatchClause() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
            `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CatchClause_NoExpr() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
            `is`(XQueryTokenType.BLOCK_OPEN))
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
    // region NamedArrayNodeTest

    @Test
    fun testNamedArrayNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.xq")

        val arrayTestPsi = file.walkTree().filterIsInstance<PluginNamedArrayNodeTest>().first()
        val conformance = arrayTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region NamedBooleanNodeTest

    @Test
    fun testNamedBooleanNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.xq")

        val booleanTestPsi = file.walkTree().filterIsInstance<PluginNamedBooleanNodeTest>().first()
        val conformance = booleanTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType, `is`(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region NamedNullNodeTest

    @Test
    fun testNamedNullNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest.xq")

        val nullTestPsi = file.walkTree().filterIsInstance<PluginNamedNullNodeTest>().first()
        val conformance = nullTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NamedNumberNodeTest

    @Test
    fun testNamedNumberNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.xq")

        val numberTestPsi = file.walkTree().filterIsInstance<PluginNamedNumberNodeTest>().first()
        val conformance = numberTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NUMBER_NODE))
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
    // region NullConstructor

    @Test
    fun testNullConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")

        val nullKindTestPsi = file.descendants().filterIsInstance<PluginNullConstructor>().first()
        val conformance = nullKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NumberConstructor

    @Test
    fun testNumberConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")

        val numberConstructorPsi = file.descendants().filterIsInstance<PluginNumberConstructor>().first()
        val conformance = numberConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region SchemaComponentTest

    @Test
    fun testSchemaComponentTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest.xq")

        val schemaComponentTestPsi = file.walkTree().filterIsInstance<PluginSchemaComponentTest>().first()
        val conformance = schemaComponentTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_COMPONENT))
    }

    // endregion
    // region SchemaFacetTest

    @Test
    fun testSchemaFacetTest() {
        val file = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest.xq")

        val schemaFacetTestPsi = file.walkTree().filterIsInstance<PluginSchemaFacetTest>().first()
        val conformance = schemaFacetTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_FACET))
    }

    // endregion
    // region SchemaParticleTest

    @Test
    fun testSchemaParticleTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest.xq")

        val schemaParticleTestPsi = file.walkTree().filterIsInstance<PluginSchemaParticleTest>().first()
        val conformance = schemaParticleTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_PARTICLE))
    }

    // endregion
    // region SchemaRootTest

    @Test
    fun testSchemaRootTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

        val schemaRootTestPsi = file.walkTree().filterIsInstance<PluginSchemaRootTest>().first()
        val conformance = schemaRootTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_ROOT))
    }

    // endregion
    // region SchemaTypeTest

    @Test
    fun testSchemaTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest.xq")

        val schemaTypeTestPsi = file.walkTree().filterIsInstance<PluginSchemaTypeTest>().first()
        val conformance = schemaTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SCHEMA_TYPE))
    }

    // endregion
    // region SimpleTypeTest

    @Test
    fun testSimpleTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest.xq")

        val simpleTypeTestPsi = file.walkTree().filterIsInstance<PluginSimpleTypeTest>().first()
        val conformance = simpleTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SIMPLE_TYPE))
    }

    // endregion
    // region StylesheetImport

    @Test
    fun testStylesheetImport() {
        val file = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq")

        val stylesheetImportPsi = file.descendants().filterIsInstance<PluginStylesheetImport>().first()
        val conformance = stylesheetImportPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.K_IMPORT))
    }

    // endregion
    // region Transactions + TransactionSeparator

    @Test
    fun testTransactions_Single_NoSemicolon() {
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().firstOrNull()

        assertThat(transactionSeparatorPsi, `is`(CoreMatchers.nullValue()))
    }

    @Test
    fun testTransactions_Single_Semicolon() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.SEPARATOR))
    }

    @Test
    fun testTransactions_Multiple_First() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(3))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuery.MARKLOGIC_0_9))
        assertThat(conformance.requiresConformance[2], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.SEPARATOR))
    }

    @Test
    fun testTransactions_Multiple_Last() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().last()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.SEPARATOR))
    }

    @Test
    fun testTransactions_Multiple_NoSemicolonAtEnd_Last() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().last()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryElementType.TRANSACTION_SEPARATOR))
    }

    @Test
    fun testTransactions_Multiple_WithProlog() {
        val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

        val transactionSeparatorPsi = file.children().filterIsInstance<PluginTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
            `is`(XQueryTokenType.SEPARATOR))
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
