/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAnyKindTest
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class MarkLogicPsiTest : ParserTestCase() {
    // region XQueryConformance
    // region AnyKindTest

    fun testAnyKindTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val anyKindTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.STRING_LITERAL))
    }

    fun testAnyKindTest_Wildcard() {
        val file = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val anyKindTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.STAR))
    }

    // endregion
    // region ArrayConstructor

    fun testArrayConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/ArrayConstructor.xq")!!

        val arrayConstructorPsi = file.descendants().filterIsInstance<XQueryArrayConstructor>().first()
        val versioned = arrayConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region ArrayTest

    fun testArrayTest() {
        val file = parseResource("tests/parser/marklogic-8.0/ArrayTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val arrayTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicArrayTest>().first()
        val conformance = arrayTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region AttributeDeclTest

    fun testAttributeDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val attributeDeclTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicAttributeDeclTest>().first()
        val conformance = attributeDeclTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ATTRIBUTE_DECL))
    }

    // endregion
    // region BinaryConstructor

    fun testBinaryConstructor() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")!!

        val binaryKindTestPsi = file.descendants().filterIsInstance<MarkLogicBinaryConstructor>().first()
        val conformance = binaryKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`<Version>(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_BINARY))
    }

    // endregion
    // region BinaryTest

    fun testBinaryTest() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val binaryKindTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicBinaryTest>().first()
        val conformance = binaryKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`<Version>(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_BINARY))
    }

    // endregion
    // region BooleanConstructor

    fun testBooleanConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")!!

        val booleanConstructorPsi = file.descendants().filterIsInstance<MarkLogicBooleanConstructor>().first()
        val conformance = booleanConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region BooleanTest

    fun testBooleanTest() {
        val file = parseResource("tests/parser/marklogic-8.0/BooleanTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val booleanTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicBooleanTest>().first()
        val conformance = booleanTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_BOOLEAN_NODE))
    }

    // endregion
    // region CatchClause

    fun testCatchClause() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val versioned = catchClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_CATCH))
    }

    // endregion
    // region CompatibilityAnnotation

    fun testCompatibilityAnnotation_FunctionDecl() {
        val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<MarkLogicCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_PRIVATE))
    }

    fun testCompatibilityAnnotation_VarDecl() {
        val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<MarkLogicCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_PRIVATE))
    }

    // endregion
    // region ComplexTypeTest

    fun testComplexTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val complexTypeTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicComplexTypeTest>().first()
        val conformance = complexTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_COMPLEX_TYPE))
    }

    // endregion
    // region ElementDeclTest

    fun testElementDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ElementDeclTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val elementDeclTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicElementDeclTest>().first()
        val conformance = elementDeclTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ELEMENT_DECL))
    }

    // endregion
    // region EnclosedExpr (CatchClause)

    fun testEnclosedExpr_CatchClause() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XQueryEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CatchClause_NoExpr() {
        val file = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XQueryEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region ForwardAxis

    fun testForwardAxis_Namespace() {
        val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XQueryForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NAMESPACE))
    }

    fun testForwardAxis_Property() {
        val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XQueryForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_PROPERTY))
    }

    // endregion
    // region MapConstructor

    fun testMapConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/MapConstructor.xq")!!

        val objectConstructorPsi = file.descendants().filterIsInstance<XQueryMapConstructor>().first()
        val versioned = objectConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_OBJECT_NODE))
    }

    // endregion
    // region MapTest

    fun testMapTest() {
        val file = parseResource("tests/parser/marklogic-8.0/MapTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val objectTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicMapTest>().first()
        val conformance = objectTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_OBJECT_NODE))
    }

    // endregion
    // region NullConstructor

    fun testNullConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")!!

        val nullKindTestPsi = file.descendants().filterIsInstance<MarkLogicNullConstructor>().first()
        val conformance = nullKindTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NullTest

    fun testNullTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NullTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val nullTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicNullTest>().first()
        val conformance = nullTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NULL_NODE))
    }

    // endregion
    // region NumberConstructor

    fun testNumberConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")!!

        val numberConstructorPsi = file.descendants().filterIsInstance<MarkLogicNumberConstructor>().first()
        val conformance = numberConstructorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region NumberTest

    fun testNumberTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val numberTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicNumberTest>().first()
        val conformance = numberTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NUMBER_NODE))
    }

    // endregion
    // region SchemaComponentTest

    fun testSchemaComponentTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val schemaComponentTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSchemaComponentTest>().first()
        val conformance = schemaComponentTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCHEMA_COMPONENT))
    }

    // endregion
    // region SchemaFacetTest

    fun testSchemaFacetTest() {
        val file = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val schemaFacetTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSchemaFacetTest>().first()
        val conformance = schemaFacetTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCHEMA_FACET))
    }

    // endregion
    // region SchemaParticleTest

    fun testSchemaParticleTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val schemaParticleTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSchemaParticleTest>().first()
        val conformance = schemaParticleTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCHEMA_PARTICLE))
    }

    // endregion
    // region SchemaRootTest

    fun testSchemaRootTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val schemaRootTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSchemaRootTest>().first()
        val conformance = schemaRootTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCHEMA_ROOT))
    }

    // endregion
    // region SchemaTypeTest

    fun testSchemaTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val schemaTypeTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSchemaTypeTest>().first()
        val conformance = schemaTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SCHEMA_TYPE))
    }

    // endregion
    // region SimpleTypeTest

    fun testSimpleTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest.xq")!!

        val annotationDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotationDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val simpleTypeTestPsi = sequenceTypePsi.descendants().filterIsInstance<MarkLogicSimpleTypeTest>().first()
        val conformance = simpleTypeTestPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SIMPLE_TYPE))
    }

    // endregion
    // region StylesheetImport

    fun testStylesheetImport() {
        val file = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq")!!

        val stylesheetImportPsi = file.descendants().filterIsInstance<MarkLogicStylesheetImport>().first()
        val conformance = stylesheetImportPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_IMPORT))
    }

    // endregion
    // region TextTest

    fun testTextTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XQueryTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XQuerySequenceType>().first()
        val textTestPsi = sequenceTypePsi.descendants().filterIsInstance<XQueryTextTest>().first()
        val versioned = textTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.STRING_LITERAL))
    }

    // endregion
    // region Transactions + TransactionSeparator

    fun testTransactions_Single_NoSemicolon() {
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")!!

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<MarkLogicTransactionSeparator>().firstOrNull()

        assertThat(transactionSeparatorPsi, `is`(nullValue()))
    }

    fun testTransactions_Single_Semicolon() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")!!

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<MarkLogicTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.SEPARATOR))
    }

    fun testTransactions_Multiple_First() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")!!

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<MarkLogicTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(3))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`<Version>(XQuery.MARKLOGIC_0_9))
        assertThat(conformance.requiresConformance[2], `is`<Version>(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.SEPARATOR))
    }

    fun testTransactions_Multiple_Last() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")!!

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<MarkLogicTransactionSeparator>().last()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.SEPARATOR))
    }

    fun testTransactions_Multiple_NoSemicolonAtEnd_Last() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")!!

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val transactionSeparatorPsi = applyExpr.children().filterIsInstance<MarkLogicTransactionSeparator>().last()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.TRANSACTION_SEPARATOR))
    }

    fun testTransactions_Multiple_WithProlog() {
        val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")!!

        val transactionSeparatorPsi = file.children().filterIsInstance<MarkLogicTransactionSeparator>().first()
        val conformance = transactionSeparatorPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`<Version>(XQuery.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.SEPARATOR))
    }

    // endregion
    // region ValidateExpr

    fun testValidateExpr_ValidateAs() {
        val file = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq")!!

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_AS))
    }

    // endregion
    // endregion
    // region XQueryMapConstructorEntry

    fun testMapConstructorEntry() {
        val file = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")!!

        val mapConstructorPsi = file.descendants().filterIsInstance<XQueryMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XQueryMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`<IElementType>(XQueryTokenType.QNAME_SEPARATOR))
    }

    // endregion
}
