/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Implementation Conformance Checks")
private class XQueryConformanceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(XQueryConformanceTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    // region AllowingEmpty

    @Test
    fun testAllowingEmpty() {
        val file = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq")

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val allowingEmptyPsi = forBindingPsi.children().filterIsInstance<XQueryAllowingEmpty>().first()
        val versioned = allowingEmptyPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ALLOWING))
    }

    // endregion
    // region Annotation

    @Test
    fun testAnnotation() {
        val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val annotationPsi = annotatedDeclPsi.children().filterIsInstance<XQueryAnnotation>().first()
        val versioned = annotationPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.ANNOTATION_INDICATOR))
    }

    // endregion
    // region AnyArrayTest

    @Test
    fun testAnyArrayTest() {
        val file = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq")

        val anyArrayTestPsi = file.walkTree().filterIsInstance<XPathAnyArrayTest>().first()
        val versioned = anyArrayTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_ARRAY))
    }

    // endregion
    // region AnyFunctionTest

    @Test
    fun testAnyFunctionTest_NoAnnotations() {
        val file = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq")

        val anyFunctionTestPsi = file.walkTree().filterIsInstance<XPathFunctionTest>().first()
        val versioned = anyFunctionTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FUNCTION))
    }

    @Test
    fun testAnyFunctionTest_WithAnnotations() {
        val file = parseResource("tests/parser/xquery-3.0/FunctionTest.xq")

        val anyFunctionTestPsi = file.walkTree().filterIsInstance<XPathFunctionTest>().first()
        val versioned = anyFunctionTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XQueryElementType.ANNOTATION))
    }

    // endregion
    // region AnyKindTest

    @Test
    fun testAnyKindTest() {
        val file = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")

        val anyKindTestPsi = file.walkTree().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_NODE))
    }

    // endregion
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region ArgumentList

    @Test
    fun testArgumentList_FunctionCall() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        val versioned = argumentListPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
    }

    @Test
    fun testArgumentList_PostfixExpr() {
        val file = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq")

        val postfixExprPsi = file.descendants().filterIsInstance<XPathPostfixExpr>().first()
        val argumentListPsi = postfixExprPsi.children().filterIsInstance<XPathArgumentList>().first()
        val versioned = argumentListPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
    }

    // endregion
    // region ArgumentPlaceholder

    @Test
    fun testArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        val argumentPlaceholderPsi = argumentListPsi.children().filterIsInstance<XPathArgumentPlaceholder>().first()
        val versioned = argumentPlaceholderPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.OPTIONAL))
    }

    // endregion
    // region ArrowExpr

    @Test
    fun testArrowExpr() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr_EQName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val versioned = arrowExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_9_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.ARROW))
    }

    // endregion
    // region BracedURILiteral

    @Test
    fun testBracedURILiteral() {
        val file = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_NCNameLocalPart.xq")
        val bracedURILiteralPsi = file.walkTree().filterIsInstance<XPathBracedURILiteral>().first()
        val versioned = bracedURILiteralPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BRACED_URI_LITERAL_START))
    }

    // endregion
    // region CatchClause

    @Test
    fun testCatchClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val versioned = catchClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_CATCH))
    }

    // endregion
    // region CompNamespaceConstructor

    @Test
    fun testCompNamespaceConstructor() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val versioned = compNamespaceConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_NAMESPACE))
    }

    // endregion
    // region ContextItemDecl

    @Test
    fun testContextItemDecl() {
        val file = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq")

        val contextItemDeclPsi = file.descendants().filterIsInstance<XQueryContextItemDecl>().first()
        val versioned = contextItemDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_CONTEXT))
    }

    // endregion
    // region SquareArrayConstructor

    @Test
    fun testSquareArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq")

        val squareArrayConstructorPsi = file.descendants().filterIsInstance<XPathSquareArrayConstructor>().first()
        val versioned = squareArrayConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.SQUARE_OPEN))
    }

    // endregion
    // region CurlyArrayConstructor

    @Test
    fun testCurlyArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")

        val curlyArrayConstructorPsi = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val versioned = curlyArrayConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_ARRAY))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    @Test
    fun testDecimalFormatDecl_XQuery30Properties() {
        val file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq")

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    @Test
    fun testDecimalFormatDecl_XQuery31Properties() {
        val file = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq")

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.DF_PROPERTY_NAME))
        assertThat(versioned.conformanceElement.firstChild.node.elementType,
                `is`(XQueryTokenType.K_EXPONENT_SEPARATOR))
    }

    // endregion
    // region EnclosedExpr (CatchClause)

    @Test
    fun testEnclosedExpr_CatchClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CatchClause_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompAttrConstructor)

    @Test
    fun testEnclosedExpr_CompAttrConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq")

        val compAttrConstructorPsi = file.descendants().filterIsInstance<XQueryCompAttrConstructor>().first()
        val enclosedExprPsi = compAttrConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompAttrConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq")

        val compAttrConstructorPsi = file.descendants().filterIsInstance<XQueryCompAttrConstructor>().first()
        val enclosedExprPsi = compAttrConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompCommentConstructor)

    @Test
    fun testEnclosedExpr_CompCommentConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq")

        val compCommentConstructorPsi = file.descendants().filterIsInstance<XQueryCompCommentConstructor>().first()
        val enclosedExprPsi = compCommentConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompCommentConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq")

        val compCommentConstructorPsi = file.descendants().filterIsInstance<XQueryCompCommentConstructor>().first()
        val enclosedExprPsi = compCommentConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompDocConstructor)

    @Test
    fun testEnclosedExpr_CompDocConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq")

        val compDocConstructorPsi = file.descendants().filterIsInstance<XQueryCompDocConstructor>().first()
        val enclosedExprPsi = compDocConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompDocConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq")

        val compDocConstructorPsi = file.descendants().filterIsInstance<XQueryCompDocConstructor>().first()
        val enclosedExprPsi = compDocConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompElemConstructor)

    @Test
    fun testEnclosedExpr_CompElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq")

        val compElemConstructorPsi = file.descendants().filterIsInstance<XQueryCompElemConstructor>().first()
        val enclosedContentExprPsi = compElemConstructorPsi.children().filterIsInstance<XQueryEnclosedContentExpr>().first()
        val versioned = enclosedContentExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompElemConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq")

        val compElemConstructorPsi = file.descendants().filterIsInstance<XQueryCompElemConstructor>().first()
        val enclosedContentExprPsi = compElemConstructorPsi.children().filterIsInstance<XQueryEnclosedContentExpr>().first()
        val versioned = enclosedContentExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedPrefixExpr)

    @Test
    fun testEnclosedExpr_CompNamespaceConstructor_PrefixExpr() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq")

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedPrefixExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompNamespaceConstructor_NoPrefixExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq")

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedPrefixExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedURIExpr)

    @Test
    fun testEnclosedExpr_CompNamespaceConstructor_UriExpr() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedUriExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompNamespaceConstructor_NoUriExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq")

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedUriExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompPIConstructor)

    @Test
    fun testEnclosedExpr_CompPIConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq")

        val compPIConstructorPsi = file.descendants().filterIsInstance<XQueryCompPIConstructor>().first()
        val enclosedExprPsi = compPIConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompPIConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq")

        val compPIConstructorPsi = file.descendants().filterIsInstance<XQueryCompPIConstructor>().first()
        val enclosedExprPsi = compPIConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompTextConstructor)

    @Test
    fun testEnclosedExpr_CompTextConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq")

        val compTextConstructorPsi = file.descendants().filterIsInstance<XQueryCompTextConstructor>().first()
        val enclosedExprPsi = compTextConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CompTextConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq")

        val compTextConstructorPsi = file.descendants().filterIsInstance<XQueryCompTextConstructor>().first()
        val enclosedExprPsi = compTextConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CurlyArrayConstructor)

    @Test
    fun testEnclosedExpr_CurlyArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")

        val curlyArrayConstructor = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val enclosedExprPsi = curlyArrayConstructor.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_CurlyArrayConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq")

        val curlyArrayConstructor = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val enclosedExprPsi = curlyArrayConstructor.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (DirAttributeValue)

    @Test
    fun testEnclosedExpr_DirAttributeValue() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val dirAttributePsi = dirAttributeListPsi.children().filterIsInstance<PluginDirAttribute>().first()
        val dirAttributeValuePsi = dirAttributePsi.children().filterIsInstance<XQueryDirAttributeValue>().first()
        val enclosedExprPsi = dirAttributeValuePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_DirAttributeValue_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val dirAttributePsi = dirAttributeListPsi.children().filterIsInstance<PluginDirAttribute>().first()
        val dirAttributeValuePsi = dirAttributePsi.children().filterIsInstance<XQueryDirAttributeValue>().first()
        val enclosedExprPsi = dirAttributeValuePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (DirElemContent)

    @Test
    fun testEnclosedExpr_DirElemContent() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirElemContentPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirElemContent>().first()
        val enclosedExprPsi = dirElemContentPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_DirElemContent_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirElemContentPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirElemContent>().first()
        val enclosedExprPsi = dirElemContentPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (FunctionDecl)

    @Test
    fun testEnclosedExpr_FunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val enclosedExprPsi = functionDeclPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_FunctionDecl_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val enclosedExprPsi = functionDeclPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (OrderedExpr)

    @Test
    fun testEnclosedExpr_OrderedExpr() {
        val file = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq")

        val orderedExprPsi = file.descendants().filterIsInstance<XQueryOrderedExpr>().first()
        val enclosedExprPsi = orderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_OrderedExpr_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq")

        val orderedExprPsi = file.descendants().filterIsInstance<XQueryOrderedExpr>().first()
        val enclosedExprPsi = orderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (TryClause)

    @Test
    fun testEnclosedExpr_TryClause() {
        val file = parseResource("tests/parser/xquery-3.0/TryClause.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val tryClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryTryClause>().first()
        val enclosedExprPsi = tryClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_TryClause_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq")

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val tryClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryTryClause>().first()
        val enclosedExprPsi = tryClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (UnorderedExpr)

    @Test
    fun testEnclosedExpr_UnorderedExpr() {
        val file = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq")

        val unorderedExprPsi = file.descendants().filterIsInstance<XQueryUnorderedExpr>().first()
        val enclosedExprPsi = unorderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.EXPR))
    }

    @Test
    fun testEnclosedExpr_UnorderedExpr_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq")

        val unorderedExprPsi = file.descendants().filterIsInstance<XQueryUnorderedExpr>().first()
        val enclosedExprPsi = unorderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.BLOCK_OPEN))
    }

    // endregion
    // region ForwardAxis

    @Test
    fun testForwardAxis_Attribute() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_ATTRIBUTE))
    }

    @Test
    fun testForwardAxis_Child() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_CHILD))
    }

    @Test
    fun testForwardAxis_Descendant() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_DESCENDANT))
    }

    @Test
    fun testForwardAxis_DescendantOrSelf() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_DESCENDANT_OR_SELF))
    }

    @Test
    fun testForwardAxis_Following() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOLLOWING))
    }

    @Test
    fun testForwardAxis_FollowingSibling() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOLLOWING_SIBLING))
    }

    @Test
    fun testForwardAxis_Self() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self.xq")

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_SELF))
    }

    // endregion
    // region InlineFunctionExpr

    @Test
    fun testInlineFunctionExpr() {
        val file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq")

        val inlineFunctionExprPsi = file.descendants().filterIsInstance<XPathInlineFunctionExpr>().first()
        val versioned = inlineFunctionExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_FUNCTION))
    }

    @Test
    fun testInlineFunctionExpr_AnnotationOnly() {
        val file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq")

        val inlineFunctionExprPsi = file.descendants().filterIsInstance<XPathInlineFunctionExpr>().first()
        val versioned = inlineFunctionExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryElementType.ANNOTATION))
    }

    // endregion
    // region TumblingWindowClause

    @Test
    fun testTumblingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val versioned = tumblingWindowClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_TUMBLING))
    }

    // endregion
    // region SlidingWindowClause

    @Test
    fun testSlidingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val versioned = slidingWindowClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SLIDING))
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
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOR))
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
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOR))
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
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOR))
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
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOR))
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
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FOR))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_LET))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_LET))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_LET))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_LET))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_LET))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ORDER))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ORDER))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ORDER))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ORDER))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ORDER))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHERE))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHERE))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHERE))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHERE))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHERE))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_COUNT))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_GROUP))
    }

    // endregion
    // region UnaryLookup

    @Test
    fun testLookup() {
        val file = parseResource("tests/parser/xquery-3.1/Lookup.xq")

        val postfixExprPsi = file.descendants().filterIsInstance<XPathPostfixExpr>().first()
        val lookupPsi = postfixExprPsi.children().filterIsInstance<XPathLookup>().first()
        val versioned = lookupPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.OPTIONAL))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region NamedFunctionRef

    @Test
    fun testNamedFunctionRef_QName() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_QName.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.FUNCTION_REF_OPERATOR))
    }

    @Test
    fun testNamedFunctionRef_NCName() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.FUNCTION_REF_OPERATOR))
    }

    @Test
    fun testNamedFunctionRef_Keyword() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_Keyword.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.FUNCTION_REF_OPERATOR))
    }

    // endregion
    // region NamespaceNodeTest

    @Test
    fun testNamespaceNodeTest() {
        val file = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest.xq")

        val namespaceNodeTestPsi = file.walkTree().filterIsInstance<XPathNamespaceNodeTest>().first()
        val versioned = namespaceNodeTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_NAMESPACE_NODE))
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
            assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
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
            assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
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
            assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.PARENTHESIS_OPEN))
        }
    }

    // endregion
    // region SequenceTypeUnion

    @Test
    fun testSequenceTypeUnion() {
        val file = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq")

        val typeswitchExprPsi = file.descendants().filterIsInstance<XQueryTypeswitchExpr>().first()
        val caseClausePsi = typeswitchExprPsi.children().filterIsInstance<XQueryCaseClause>().first()
        val sequenceTypeUnionPsi = caseClausePsi.children().filterIsInstance<XQuerySequenceTypeUnion>().first()
        val versioned = sequenceTypeUnionPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.UNION))
    }

    // endregion
    // region SimpleMapExpr

    @Test
    fun testSimpleMapExpr() {
        val file = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq")

        val simpleMapExprPsi = file.descendants().filterIsInstance<XPathSimpleMapExpr>().first()
        val versioned = simpleMapExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.MAP_OPERATOR))
    }

    // endregion
    // region StringConcatExpr

    @Test
    fun testStringConcatExpr() {
        val file = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq")

        val stringConcatExprPsi = file.descendants().filterIsInstance<XPathStringConcatExpr>().first()
        val versioned = stringConcatExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.CONCATENATION))
    }

    // endregion
    // region StringConstructor

    @Test
    fun testStringConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/StringConstructor.xq")

        val stringConstructorPsi = file.descendants().filterIsInstance<XQueryStringConstructor>().first()
        val versioned = stringConstructorPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.STRING_CONSTRUCTOR_START))
    }

    // endregion
    // region SwitchExpr

    @Test
    fun testSwitchExpr() {
        val file = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")

        val switchExprPsi = file.descendants().filterIsInstance<XQuerySwitchExpr>().first()
        val versioned = switchExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SWITCH))
    }

    // endregion
    // region TryClause

    @Test
    fun testTryClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")

        val tryClausePsi = file.descendants().filterIsInstance<XQueryTryClause>().first()
        val versioned = tryClausePsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_TRY))
    }

    // endregion
    // region TypedArrayTest

    @Test
    fun testTypedArrayTest() {
        val file = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq")

        val typedArrayTestPsi = file.walkTree().filterIsInstance<XPathTypedArrayTest>().first()
        val versioned = typedArrayTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_ARRAY))
    }

    // endregion
    // region TypedFunctionTest

    @Test
    fun testTypedFunctionTest_NoAnnotations() {
        val file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")

        val typedFunctionTestPsi = file.walkTree().filterIsInstance<XPathFunctionTest>().first()
        val versioned = typedFunctionTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_FUNCTION))
    }

    @Test
    fun testTypedFunctionTest_WithAnnotations() {
        val file = parseResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.xq")

        val typedFunctionTestPsi = file.walkTree().filterIsInstance<XPathFunctionTest>().first()
        val versioned = typedFunctionTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XQueryElementType.ANNOTATION))
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
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.K_MAP))
    }

    // endregion
    // region UnaryLookup

    @Test
    fun testUnaryLookup() {
        val file = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq")

        val unaryLookupPsi = file.walkTree().filterIsInstance<XPathUnaryLookup>().first()
        val versioned = unaryLookupPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.OPTIONAL))
    }

    // endregion
    // region ValidateExpr

    @Test
    fun testValidateExpr() {
        val file = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq")

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_VALIDATE))
    }

    @Test
    fun testValidateExpr_Type() {
        val file = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq")

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_TYPE))
    }

    // endregion
    // region VarDecl

    @Test
    fun testVarDecl() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_VARIABLE))
    }

    @Test
    fun testVarDecl_External() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_VARIABLE))
    }

    @Test
    fun testVarDecl_External_DefaultValue() {
        val file = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XPathTokenType.ASSIGN_EQUAL))
    }

    // endregion
    // region VersionDecl

    @Test
    fun testVersionDecl_Conformance() {
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_XQUERY))
    }

    @Test
    fun testVersionDecl_WithEncoding_Conformance() {
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_XQUERY))
    }

    @Test
    fun testVersionDecl_EncodingOnly_Conformance() {
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ENCODING))
    }

    // endregion

    @Test
    @DisplayName("XQuery 3.1 EBNF (33) ParamList")
    fun paramList() {
        val file = parseResource("tests/parser/xquery-1.0/ParamList.xq")
        val conformance = file.walkTree().filterIsInstance<XPathParamList>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType, `is`(XPathElementType.PARAM))
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (77) IfExpr")
    fun ifExpr() {
        val file = parseResource("tests/parser/xquery-1.0/IfExpr.xq")
        val versioned = file.walkTree().filterIsInstance<XPathIfExpr>().first() as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_ELSE))
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; recommendation syntax")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_1_0_20070123))
            assertThat(versioned.requiresConformance[1], `is`(EXistDB.VERSION_4_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.node.elementType, `is`(XPathTokenType.K_EMPTY_SEQUENCE))
        }

        @Test
        @DisplayName("occurrence indicator; one or more")
        fun occurrenceIndicator_oneOrMore() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_OneOrMore.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(
                versioned.conformanceElement.node.elementType,
                `is`(XPathElementType.ATOMIC_OR_UNION_TYPE)
            )
        }

        @Test
        @DisplayName("occurrence indicator; optional")
        fun occurrenceIndicator_optional() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(
                versioned.conformanceElement.node.elementType,
                `is`(XPathElementType.ATOMIC_OR_UNION_TYPE)
            )
        }

        @Test
        @DisplayName("occurrence indicator; zero or more")
        fun occurrenceIndicator_zeroOrMore() {
            val file = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(
                versioned.conformanceElement.node.elementType,
                `is`(XPathElementType.ATOMIC_OR_UNION_TYPE)
            )
        }
    }
}
