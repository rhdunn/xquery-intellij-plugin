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
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathNCNamePsiImpl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Saxon
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryPsiTest : ParserTestCase() {
    // region XPathArgumentList

    fun testArgumentList() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(2))
    }

    fun testArgumentList_Empty() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(0))
    }

    fun testArgumentList_ArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(1))
    }

    // endregion
    // region XPathArrowFunctionSpecifier

    fun testArrowFunctionSpecifier() {
        val file = parseResource("tests/psi/xquery-3.1/ArrowExpr_MultipleArguments.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(4))
    }

    fun testArrowFunctionSpecifier_Empty() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(1))
    }

    fun testArrowFunctionSpecifier_MissingArgumentList() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(1))
    }

    // endregion
    // region XQueryConformance
    // region AllowingEmpty

    fun testAllowingEmpty() {
        val file = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val allowingEmptyPsi = forBindingPsi.children().filterIsInstance<XQueryAllowingEmpty>().first()
        val versioned = allowingEmptyPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ALLOWING))
    }

    // endregion
    // region Annotation

    fun testAnnotation() {
        val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val annotationPsi = annotatedDeclPsi.children().filterIsInstance<XQueryAnnotation>().first()
        val versioned = annotationPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.ANNOTATION_INDICATOR))
    }

    // endregion
    // region AnyArrayTest

    fun testAnyArrayTest() {
        val file = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val anyArrayTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyArrayTest>().first()
        val versioned = anyArrayTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ARRAY))
    }

    // endregion
    // region AnyFunctionTest

    fun testAnyFunctionTest() {
        val file = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val anyFunctionTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyFunctionTest>().first()
        val versioned = anyFunctionTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FUNCTION))
    }

    // endregion
    // region AnyKindTest

    fun testAnyKindTest() {
        val file = parseResource("tests/parser/xquery-1.0/AnyKindTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val anyKindTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyKindTest>().first()
        val versioned = anyKindTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NODE))
    }

    // endregion
    // region AnyMapTest

    fun testAnyMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val anyMapTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathAnyMapTest>().first()
        val versioned = anyMapTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_MAP))
    }

    // endregion
    // region ArgumentList

    fun testArgumentList_FunctionCall() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        val versioned = argumentListPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.PARENTHESIS_OPEN))
    }

    fun testArgumentList_PostfixExpr() {
        val file = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq")!!

        val postfixExprPsi = file.descendants().filterIsInstance<XPathPostfixExpr>().first()
        val argumentListPsi = postfixExprPsi.children().filterIsInstance<XPathArgumentList>().first()
        val versioned = argumentListPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.PARENTHESIS_OPEN))
    }

    // endregion
    // region ArgumentPlaceholder

    fun testArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        val argumentPsi = argumentListPsi.children().filterIsInstance<XPathArgument>().first()
        val argumentPlaceholderPsi = argumentPsi.descendants().filterIsInstance<XPathArgumentPlaceholder>().first()
        val versioned = argumentPlaceholderPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.OPTIONAL))
    }

    // endregion
    // region ArrowExpr

    fun testArrowExpr() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val versioned = arrowExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_9_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.ARROW))
    }

    fun testArrowExpr_NoMap() {
        val file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val versioned = arrowExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.UNARY_EXPR))
    }

    // endregion
    // region BracedURILiteral

    fun testBracedURILiteral() {
        val file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val qnamePsi = optionDeclPsi.children().filterIsInstance<XPathURIQualifiedName>().first()
        val bracedURILiteralPsi = qnamePsi.descendants().filterIsInstance<XPathBracedURILiteral>().first()
        val versioned = bracedURILiteralPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BRACED_URI_LITERAL_START))
    }

    // endregion
    // region CatchClause

    fun testCatchClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val versioned = catchClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_CATCH))
    }

    // endregion
    // region CompNamespaceConstructor

    fun testCompNamespaceConstructor() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")!!

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val versioned = compNamespaceConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NAMESPACE))
    }

    // endregion
    // region ContextItemDecl

    fun testContextItemDecl() {
        val file = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq")!!

        val contextItemDeclPsi = file.descendants().filterIsInstance<XQueryContextItemDecl>().first()
        val versioned = contextItemDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_CONTEXT))
    }

    // endregion
    // region SquareArrayConstructor

    fun testSquareArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq")!!

        val squareArrayConstructorPsi = file.descendants().filterIsInstance<XPathSquareArrayConstructor>().first()
        val versioned = squareArrayConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.SQUARE_OPEN))
    }

    // endregion
    // region CurlyArrayConstructor

    fun testCurlyArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")!!

        val curlyArrayConstructorPsi = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val versioned = curlyArrayConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ARRAY))
    }

    // endregion
    // region DecimalFormatDecl

    fun testDecimalFormatDecl() {
        val file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq")!!

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    fun testDecimalFormatDecl_XQuery30Properties() {
        val file = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq")!!

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_DECIMAL_FORMAT))
    }

    fun testDecimalFormatDecl_XQuery31Properties() {
        val file = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq")!!

        val decimalFormatDeclPsi = file.descendants().filterIsInstance<XQueryDecimalFormatDecl>().first()
        val versioned = decimalFormatDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.DF_PROPERTY_NAME))
        assertThat(versioned.conformanceElement.firstChild.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_EXPONENT_SEPARATOR))
    }

    // endregion
    // region EnclosedExpr (CatchClause)

    fun testEnclosedExpr_CatchClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CatchClause_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
        val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompAttrConstructor)

    fun testEnclosedExpr_CompAttrConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq")!!

        val compAttrConstructorPsi = file.descendants().filterIsInstance<XQueryCompAttrConstructor>().first()
        val enclosedExprPsi = compAttrConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompAttrConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq")!!

        val compAttrConstructorPsi = file.descendants().filterIsInstance<XQueryCompAttrConstructor>().first()
        val enclosedExprPsi = compAttrConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompCommentConstructor)

    fun testEnclosedExpr_CompCommentConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq")!!

        val compCommentConstructorPsi = file.descendants().filterIsInstance<XQueryCompCommentConstructor>().first()
        val enclosedExprPsi = compCommentConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompCommentConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq")!!

        val compCommentConstructorPsi = file.descendants().filterIsInstance<XQueryCompCommentConstructor>().first()
        val enclosedExprPsi = compCommentConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompDocConstructor)

    fun testEnclosedExpr_CompDocConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq")!!

        val compDocConstructorPsi = file.descendants().filterIsInstance<XQueryCompDocConstructor>().first()
        val enclosedExprPsi = compDocConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompDocConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq")!!

        val compDocConstructorPsi = file.descendants().filterIsInstance<XQueryCompDocConstructor>().first()
        val enclosedExprPsi = compDocConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompElemConstructor)

    fun testEnclosedExpr_CompElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq")!!

        val compElemConstructorPsi = file.descendants().filterIsInstance<XQueryCompElemConstructor>().first()
        val enclosedContentExprPsi = compElemConstructorPsi.children().filterIsInstance<XQueryEnclosedContentExpr>().first()
        val versioned = enclosedContentExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompElemConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq")!!

        val compElemConstructorPsi = file.descendants().filterIsInstance<XQueryCompElemConstructor>().first()
        val enclosedContentExprPsi = compElemConstructorPsi.children().filterIsInstance<XQueryEnclosedContentExpr>().first()
        val versioned = enclosedContentExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedPrefixExpr)

    fun testEnclosedExpr_CompNamespaceConstructor_PrefixExpr() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq")!!

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedPrefixExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompNamespaceConstructor_NoPrefixExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq")!!

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedPrefixExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompNamespaceConstructor + EnclosedURIExpr)

    fun testEnclosedExpr_CompNamespaceConstructor_UriExpr() {
        val file = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")!!

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedUriExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompNamespaceConstructor_NoUriExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq")!!

        val compNamespaceConstructorPsi = file.descendants().filterIsInstance<XQueryCompNamespaceConstructor>().first()
        val enclosedExprPsi = compNamespaceConstructorPsi.children().filterIsInstance<XQueryEnclosedUriExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompPIConstructor)

    fun testEnclosedExpr_CompPIConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq")!!

        val compPIConstructorPsi = file.descendants().filterIsInstance<XQueryCompPIConstructor>().first()
        val enclosedExprPsi = compPIConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompPIConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq")!!

        val compPIConstructorPsi = file.descendants().filterIsInstance<XQueryCompPIConstructor>().first()
        val enclosedExprPsi = compPIConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CompTextConstructor)

    fun testEnclosedExpr_CompTextConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq")!!

        val compTextConstructorPsi = file.descendants().filterIsInstance<XQueryCompTextConstructor>().first()
        val enclosedExprPsi = compTextConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CompTextConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq")!!

        val compTextConstructorPsi = file.descendants().filterIsInstance<XQueryCompTextConstructor>().first()
        val enclosedExprPsi = compTextConstructorPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (CurlyArrayConstructor)

    fun testEnclosedExpr_CurlyArrayConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")!!

        val curlyArrayConstructor = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val enclosedExprPsi = curlyArrayConstructor.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_CurlyArrayConstructor_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq")!!

        val curlyArrayConstructor = file.descendants().filterIsInstance<XPathCurlyArrayConstructor>().first()
        val enclosedExprPsi = curlyArrayConstructor.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (DirAttributeValue)

    fun testEnclosedExpr_DirAttributeValue() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val dirAttributeValuePsi = dirAttributeListPsi.children().filterIsInstance<XQueryDirAttributeValue>().first()
        val enclosedExprPsi = dirAttributeValuePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_DirAttributeValue_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val dirAttributeValuePsi = dirAttributeListPsi.children().filterIsInstance<XQueryDirAttributeValue>().first()
        val enclosedExprPsi = dirAttributeValuePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (DirElemContent)

    fun testEnclosedExpr_DirElemContent() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirElemContentPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirElemContent>().first()
        val enclosedExprPsi = dirElemContentPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_DirElemContent_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirElemContentPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirElemContent>().first()
        val enclosedExprPsi = dirElemContentPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (FunctionDecl)

    fun testEnclosedExpr_FunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val enclosedExprPsi = functionDeclPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_FunctionDecl_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val enclosedExprPsi = functionDeclPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (OrderedExpr)

    fun testEnclosedExpr_OrderedExpr() {
        val file = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq")!!

        val orderedExprPsi = file.descendants().filterIsInstance<XQueryOrderedExpr>().first()
        val enclosedExprPsi = orderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_OrderedExpr_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq")!!

        val orderedExprPsi = file.descendants().filterIsInstance<XQueryOrderedExpr>().first()
        val enclosedExprPsi = orderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (TryClause)

    fun testEnclosedExpr_TryClause() {
        val file = parseResource("tests/parser/xquery-3.0/TryClause.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val tryClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryTryClause>().first()
        val enclosedExprPsi = tryClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_TryClause_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq")!!

        val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
        val tryClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryTryClause>().first()
        val enclosedExprPsi = tryClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region EnclosedExpr (UnorderedExpr)

    fun testEnclosedExpr_UnorderedExpr() {
        val file = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq")!!

        val unorderedExprPsi = file.descendants().filterIsInstance<XQueryUnorderedExpr>().first()
        val enclosedExprPsi = unorderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.EXPR))
    }

    fun testEnclosedExpr_UnorderedExpr_NoExpr() {
        val file = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq")!!

        val unorderedExprPsi = file.descendants().filterIsInstance<XQueryUnorderedExpr>().first()
        val enclosedExprPsi = unorderedExprPsi.children().filterIsInstance<XPathEnclosedExpr>().first()
        val versioned = enclosedExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.BLOCK_OPEN))
    }

    // endregion
    // region ForwardAxis

    fun testForwardAxis_Attribute() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Attribute.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ATTRIBUTE))
    }

    fun testForwardAxis_Child() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Child.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_CHILD))
    }

    fun testForwardAxis_Descendant() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Descendant.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_DESCENDANT))
    }

    fun testForwardAxis_DescendantOrSelf() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_DescendantOrSelf.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_DESCENDANT_OR_SELF))
    }

    fun testForwardAxis_Following() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Following.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOLLOWING))
    }

    fun testForwardAxis_FollowingSibling() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_FollowingSibling.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOLLOWING_SIBLING))
    }

    fun testForwardAxis_Self() {
        val file = parseResource("tests/parser/xquery-1.0/ForwardAxis_Self.xq")!!

        val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
        val versioned = forwardAxisPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SELF))
    }

    // endregion
    // region InlineFunctionExpr

    fun testInlineFunctionExpr() {
        val file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq")!!

        val inlineFunctionExprPsi = file.descendants().filterIsInstance<XPathInlineFunctionExpr>().first()
        val versioned = inlineFunctionExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FUNCTION))
    }

    fun testInlineFunctionExpr_AnnotationOnly() {
        val file = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq")!!

        val inlineFunctionExprPsi = file.descendants().filterIsInstance<XPathInlineFunctionExpr>().first()
        val versioned = inlineFunctionExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.ANNOTATION))
    }

    // endregion
    // region TumblingWindowClause

    fun testTumblingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val versioned = tumblingWindowClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_TUMBLING))
    }

    // endregion
    // region SlidingWindowClause

    fun testSlidingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val versioned = slidingWindowClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SLIDING))
    }

    // endregion
    // region IntermediateClause (ForClause)

    fun testForClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOR))
    }

    fun testForClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOR))
    }

    fun testForClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOR))
    }

    fun testForClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOR))
    }

    fun testForClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FOR))
    }

    // endregion
    // region IntermediateClause (LetClause)

    fun testLetClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_LET))
    }

    fun testLetClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_LET))
    }

    fun testLetClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_LET))
    }

    fun testLetClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_LET))
    }

    fun testLetClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_LET))
    }

    // endregion
    // region IntermediateClause (OrderByClause)

    fun testOrderByClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ORDER))
    }

    fun testOrderByClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ORDER))
    }

    fun testOrderByClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ORDER))
    }

    fun testOrderByClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[4].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[4]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ORDER))
    }

    fun testOrderByClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ORDER))
    }

    // endregion
    // region IntermediateClause (WhereClause)

    fun testWhereClause_FirstIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_WHERE))
    }

    fun testWhereClause_AfterForIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryForClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_WHERE))
    }

    fun testWhereClause_AfterLetIntermediateClause() {
        val file = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryLetClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[3]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_WHERE))
    }

    fun testWhereClause_AfterWhereIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_WHERE))
    }

    fun testWhereClause_AfterOrderByIntermediateClause() {
        val file = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[1].firstChild,
                instanceOf<PsiElement>(XQueryOrderByClause::class.java))
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2].firstChild,
                instanceOf<PsiElement>(XQueryWhereClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().toList()[2]
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_WHERE))
    }

    // endregion
    // region IntermediateClause (CountClause)

    fun testCountClause() {
        val file = parseResource("tests/parser/xquery-3.0/CountClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryCountClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_COUNT))
    }

    // endregion
    // region IntermediateClause (GroupByClause)

    fun testGroupByClause() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        // prev == null
        assertThat(flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first().firstChild,
                instanceOf<PsiElement>(XQueryGroupByClause::class.java))

        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val versioned = intermediateClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_GROUP))
    }

    // endregion
    // region UnaryLookup

    fun testLookup() {
        val file = parseResource("tests/parser/xquery-3.1/Lookup.xq")!!

        val postfixExprPsi = file.descendants().filterIsInstance<XPathPostfixExpr>().first()
        val lookupPsi = postfixExprPsi.children().filterIsInstance<XPathLookup>().first()
        val versioned = lookupPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.OPTIONAL))
    }

    // endregion
    // region MapConstructor

    fun testMapConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/MapConstructor.xq")!!

        val objectConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val versioned = objectConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_MAP))
    }

    // endregion
    // region NamedFunctionRef

    fun testNamedFunctionRef_QName() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_QName.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.FUNCTION_REF_OPERATOR))
    }

    fun testNamedFunctionRef_NCName() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.FUNCTION_REF_OPERATOR))
    }

    fun testNamedFunctionRef_Keyword() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_Keyword.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        val versioned = namedFunctionRefPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.FUNCTION_REF_OPERATOR))
    }

    // endregion
    // region NamespaceNodeTest

    fun testNamespaceNodeTest() {
        val file = parseResource("tests/parser/xquery-3.0/NamespaceNodeTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val namespaceNodeTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathNamespaceNodeTest>().first()
        val versioned = namespaceNodeTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_NAMESPACE_NODE))
    }

    // endregion
    // region ParenthesizedItemType

    fun testParenthesizedItemType() {
        val file = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val parenthesizedItemTypePsi = sequenceTypePsi.descendants().filterIsInstance<XPathParenthesizedItemType>().first()
        val versioned = parenthesizedItemTypePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.PARENTHESIS_OPEN))
    }

    // endregion
    // region SequenceTypeUnion

    fun testSequenceTypeUnion() {
        val file = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq")!!

        val typeswitchExprPsi = file.descendants().filterIsInstance<XQueryTypeswitchExpr>().first()
        val caseClausePsi = typeswitchExprPsi.children().filterIsInstance<XQueryCaseClause>().first()
        val sequenceTypeUnionPsi = caseClausePsi.children().filterIsInstance<XQuerySequenceTypeUnion>().first()
        val versioned = sequenceTypeUnionPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.UNION))
    }

    fun testSequenceTypeUnion_NoUnion() {
        val file = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq")!!

        val typeswitchExprPsi = file.descendants().filterIsInstance<XQueryTypeswitchExpr>().first()
        val caseClausePsi = typeswitchExprPsi.children().filterIsInstance<XQueryCaseClause>().first()
        val sequenceTypeUnionPsi = caseClausePsi.children().filterIsInstance<XQuerySequenceTypeUnion>().first()
        val versioned = sequenceTypeUnionPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.SEQUENCE_TYPE))
    }

    // endregion
    // region SimpleMapExpr

    fun testSimpleMapExpr() {
        val file = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq")!!

        val simpleMapExprPsi = file.descendants().filterIsInstance<XPathSimpleMapExpr>().first()
        val versioned = simpleMapExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.MAP_OPERATOR))
    }

    fun testSimpleMapExpr_NoMap() {
        val file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")!!

        val simpleMapExprPsi = file.descendants().filterIsInstance<XPathSimpleMapExpr>().first()
        val versioned = simpleMapExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.PATH_EXPR))
    }

    // endregion
    // region StringConcatExpr

    fun testStringConcatExpr() {
        val file = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq")!!

        val stringConcatExprPsi = file.descendants().filterIsInstance<XPathStringConcatExpr>().first()
        val versioned = stringConcatExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.CONCATENATION))
    }

    fun testStringConcatExpr_NoConcatenation() {
        val file = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")!!

        val stringConcatExprPsi = file.descendants().filterIsInstance<XPathStringConcatExpr>().first()
        val versioned = stringConcatExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryElementType.RANGE_EXPR))
    }

    // endregion
    // region StringConstructor

    fun testStringConstructor() {
        val file = parseResource("tests/parser/xquery-3.1/StringConstructor.xq")!!

        val stringConstructorPsi = file.descendants().filterIsInstance<XQueryStringConstructor>().first()
        val versioned = stringConstructorPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.STRING_CONSTRUCTOR_START))
    }

    // endregion
    // region SwitchExpr

    fun testSwitchExpr() {
        val file = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")!!

        val switchExprPsi = file.descendants().filterIsInstance<XQuerySwitchExpr>().first()
        val versioned = switchExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_SWITCH))
    }

    // endregion
    // region TextTest

    fun testTextTest() {
        val file = parseResource("tests/parser/xquery-1.0/TextTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val textTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathTextTest>().first()
        val versioned = textTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_TEXT))
    }

    // endregion
    // region TryClause

    fun testTryClause() {
        val file = parseResource("tests/parser/xquery-3.0/CatchClause.xq")!!

        val tryClausePsi = file.descendants().filterIsInstance<XQueryTryClause>().first()
        val versioned = tryClausePsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_TRY))
    }

    // endregion
    // region TypedArrayTest

    fun testTypedArrayTest() {
        val file = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val typedArrayTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathTypedArrayTest>().first()
        val versioned = typedArrayTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ARRAY))
    }

    // endregion
    // region TypedFunctionTest

    fun testTypedFunctionTest() {
        val file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val typedFunctionTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathTypedFunctionTest>().first()
        val versioned = typedFunctionTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_FUNCTION))
    }

    // endregion
    // region TypedMapTest

    fun testTypedMapTest() {
        val file = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val typeDeclarationPsi = varDeclPsi.children().filterIsInstance<XPathTypeDeclaration>().first()
        val sequenceTypePsi = typeDeclarationPsi.children().filterIsInstance<XPathSequenceType>().first()
        val typedMapTestPsi = sequenceTypePsi.descendants().filterIsInstance<XPathTypedMapTest>().first()
        val versioned = typedMapTestPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))
        assertThat(versioned.requiresConformance[1], `is`<Version>(Saxon.VERSION_9_4))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_MAP))
    }

    // endregion
    // region UnaryLookup

    fun testUnaryLookup() {
        val file = parseResource("tests/parser/xquery-3.1/UnaryLookup.xq")!!

        val simpleMapExprPsi = file.descendants().filterIsInstance<XPathSimpleMapExpr>().first()
        val pathExprPsi = simpleMapExprPsi.children().filterIsInstance<XPathPathExpr>().toList()[1]
        val unaryLookupPsi = pathExprPsi.descendants().filterIsInstance<XPathUnaryLookup>().first()
        val versioned = unaryLookupPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_1_20170321))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.OPTIONAL))
    }

    // endregion
    // region ValidateExpr

    fun testValidateExpr() {
        val file = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq")!!

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_VALIDATE))
    }

    fun testValidateExpr_Type() {
        val file = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq")!!

        val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
        val versioned = validateExprPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_TYPE))
    }

    // endregion
    // region VarDecl

    fun testVarDecl() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_VARIABLE))
    }

    fun testVarDecl_External() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_VARIABLE))
    }

    fun testVarDecl_External_DefaultValue() {
        val file = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val versioned = varDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(2))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))
        assertThat(versioned.requiresConformance[1], `is`<Version>(MarkLogic.VERSION_6_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.ASSIGN_EQUAL))
    }

    // endregion
    // region VersionDecl

    fun testVersionDecl_Conformance() {
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_XQUERY))
    }

    fun testVersionDecl_WithEncoding_Conformance() {
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_XQUERY))
    }

    fun testVersionDecl_EncodingOnly_Conformance() {
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        val versioned = versionDeclPsi as XQueryConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`<Version>(XQuery.REC_3_0_20140408))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_ENCODING))
    }

    // endregion
    // endregion
    // region XQueryDefaultNamespaceDecl

    fun testDefaultNamespaceDecl_Element() {
        val file = parseText("declare default element namespace 'http://www.w3.org/1999/xhtml';")!!
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(decl.defaultValue?.lexicalRepresentation, `is`("http://www.w3.org/1999/xhtml"))
    }

    fun testDefaultNamespaceDecl_Function() {
        val file = parseText("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';")!!
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.Function))
        assertThat(decl.defaultValue?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(decl.defaultValue?.lexicalRepresentation, `is`("http://www.w3.org/2005/xpath-functions/math"))
    }

    fun testDefaultNamespaceDecl_EmptyNamespace() {
        val file = parseText("declare default element namespace '';")!!
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue, `is`(nullValue()))
    }

    fun testDefaultNamespaceDecl_MissingNamespace() {
        val file = parseText("declare default element namespace;")!!
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue, `is`(nullValue()))
    }

    // endregion
    // region XQueryDirElemConstructor

    fun testDirElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.lexicalRepresentation, `is`("a"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName!!.lexicalRepresentation, `is`("b"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(notNullValue()))
        assertThat(close!!.prefix, `is`(notNullValue()))
        assertThat(close.prefix!!.lexicalRepresentation, `is`("a"))
        assertThat(close.localName, `is`(notNullValue()))
        assertThat(close.localName!!.lexicalRepresentation, `is`("b"))
    }

    fun testDirElemConstructor_SelfClosing() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(true))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.lexicalRepresentation, `is`("h"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName!!.lexicalRepresentation, `is`("br"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(nullValue()))
    }

    fun testDirElemConstructor_MissingClosingTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.lexicalRepresentation, `is`("a"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName!!.lexicalRepresentation, `is`("b"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(nullValue()))
    }

    // endregion
    // region XPathEQName
    // region EQName

    fun testEQName_QName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("xs"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    fun testEQName_KeywordLocalPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordLocalPart.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("sort"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("least"))
    }

    fun testEQName_MissingLocalPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_MissingLocalPart.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("xs"))

        assertThat(eqnamePsi.localName, `is`(nullValue()))
    }

    fun testEQName_KeywordPrefixPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordPrefixPart.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("order"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("column"))
    }

    fun testEQName_NCName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_NCName.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(nullValue()))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    fun testEQName_URIQualifiedName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_URIQualifiedName.xq")!!

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(eqnamePsi.prefix!!.text, `is`("Q{http://www.w3.org/2001/XMLSchema}"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    // endregion
    // region NCName

    fun testNCName() {
        val file = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(nullValue()))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryTokenType.K_COLLATION))
        assertThat(eqnamePsi.localName!!.text, `is`("collation"))
    }

    // endregion
    // region QName

    fun testQName() {
        val file = parseResource("tests/parser/xquery-1.0/QName.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("one"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("two"))
    }

    fun testQName_KeywordLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("sort"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("least"))
    }

    fun testQName_MissingLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("one"))

        assertThat(eqnamePsi.localName, `is`(nullValue()))
    }

    fun testQName_KeywordPrefixPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("order"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("two"))
    }

    fun testQName_DirElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val eqnamePsi = dirElemConstructorPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("a"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("b"))
    }

    fun testQName_DirAttributeList() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val eqnamePsi = dirAttributeListPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("xml"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("id"))
    }

    // endregion
    // region Type :: Function :: FunctionCall

    fun testEQNameType_FunctionCall_NCName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_NCName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_FunctionCall_QName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_FunctionCall_EQName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_EQName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Function :: NamedFunctionRef

    fun testEQNameType_NamedFunctionRef_NCName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_NCName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_NamedFunctionRef_QName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_QName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_NamedFunctionRef_EQName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_EQName.xq")!!

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Function :: ArrowFunctionSpecifier

    fun testEQNameType_ArrowFunctionSpecifier_NCName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_NCName.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_ArrowFunctionSpecifier_QName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_QName.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    fun testEQNameType_ArrowFunctionSpecifier_EQName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_EQName.xq")!!

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Variable :: VarDecl

    fun testEQNameType_VarDecl_NCName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_NCName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val name = varDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    fun testEQNameType_VarDecl_QName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_QName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val name = varDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.prefix!!.text, `is`("local"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    fun testEQNameType_VarDecl_EQName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_EQName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val name = varDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xquery-local-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    // endregion
    // region resolveFunctionDecls

    fun testQName_resolveFunctionDecls_SingleDeclMatch() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName.xq")!!

        val fn = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val fnName = fn.children().filterIsInstance<XPathQName>().first()

        val decls = fnName.resolveFunctionDecls().toList()
        assertThat(decls.size, `is`(1))

        val functionName = decls[0].children().filterIsInstance<XPathQName>().first()
        assertThat(functionName.text, `is`("fn:true"))
        assertThat(decls[0].arity, `is`(0))
    }

    fun testQName_resolveFunctionDecls_MultipleDeclMatch() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName_Arity.xq")!!

        val fn = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val fnName = fn.children().filterIsInstance<XPathQName>().first()

        val decls = fnName.resolveFunctionDecls().toList()
        assertThat(decls.size, `is`(2))

        var functionName = decls[0].children().filterIsInstance<XPathQName>().first()
        assertThat(functionName.text, `is`("fn:data"))
        assertThat(decls[0].arity, `is`(0))

        functionName = decls[1].children().filterIsInstance<XPathQName>().first()
        assertThat(functionName.text, `is`("fn:data"))
        assertThat(decls[1].arity, `is`(1))
    }

    // endregion
    // endregion
    // region XQueryModule

    fun testFile_Empty() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseText("")!!

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

        settings.XQueryVersion = XQuery.REC_3_1_20170321.label

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
    }

    fun testFile() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")!!

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

        settings.XQueryVersion = XQuery.REC_3_1_20170321.label

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
    }

    // endregion
    // region XPathFunctionCall

    fun testFunctionCall() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(2))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("math"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("pow"))
    }

    fun testFunctionCall_Empty() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(0))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("fn"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    fun testFunctionCall_ArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(1))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("math"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("sin"))
    }

    fun testFunctionCall_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-1.0/FunctionCall_NoFunctionEQName.xq")!!

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(0))

        assertThat(functionCallPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XQueryFunctionDecl

    fun testFunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(0))

        assertThat(functionDeclPsi.functionName?.prefix?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionDeclPsi.functionName?.prefix?.node?.text, `is`("fn"))

        assertThat(functionDeclPsi.functionName?.localName?.node?.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(functionDeclPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    fun testFunctionDecl_ParamList() {
        val file = parseResource("tests/parser/xquery-1.0/ParamList.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(2))

        assertThat(functionDeclPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(functionDeclPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionDeclPsi.functionName?.localName?.node?.text, `is`("test"))
    }

    fun testFunctionDecl_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-1.0/FunctionDecl_NoFunctionEQName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(0))

        assertThat(functionDeclPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XPathMapConstructorEntry

    fun testMapConstructorEntry() {
        val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")!!

        val mapConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XPathMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`<IElementType>(XQueryTokenType.QNAME_SEPARATOR))
    }

    fun testMapConstructorEntry_NoValueAssignmentOperator() {
        val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")!!

        val mapConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XPathMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`<IElementType>(XQueryElementType.MAP_KEY_EXPR))
    }

    // endregion
    // region XQueryPrologResolver
    // region Module

    fun testModule_PrologResolver_NoProlog() {
        val file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")!!

        val modules = file.children().filterIsInstance<XQueryLibraryModule>().toList()
        assertThat(modules.size, `is`(1))

        val provider = modules.get(0) as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(nullValue()))
    }

    fun testModule_PrologResolver() {
        val file = parseResource("tests/resolve/namespaces/ModuleDecl.xq")!!

        val modules = file.children().filterIsInstance<XQueryLibraryModule>().toList()
        assertThat(modules.size, `is`(1))

        val provider = modules.get(0) as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    // endregion
    // region ModuleDecl

    fun testModuleDecl_PrologResolver_NoProlog() {
        val file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleDecl>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(nullValue()))
    }

    fun testModuleDecl_PrologResolver() {
        val file = parseResource("tests/resolve/namespaces/ModuleDecl.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleDecl>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    // endregion
    // region ModuleImport

    fun testModuleImport_EmptyUri() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(nullValue()))
    }

    fun testModuleImport_LocalPath_NoModule() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(nullValue()))
    }

    fun testModuleImport_LocalPath_Module() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ParentDirectory.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    fun testModuleImport_ResourceFile() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq")!!

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat<XQueryProlog>(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.children()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("array:append"))
    }

    // endregion
    // endregion
    // region XPathNamedFunctionRef

    fun testNamedFunctionRef() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(3))

        assertThat(namedFunctionRefPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    fun testNamedFunctionRef_MissingArity() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(0))

        assertThat(namedFunctionRefPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    fun testNamedFunctionRef_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_NoFunctionEQName.xq")!!

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(0))

        assertThat(namedFunctionRefPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XQueryNamespaceResolver
    // region DirAttributeList

    fun testDirAttributeList() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirAttributeListPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("a"), `is`(nullValue()))
    }

    fun testDirAttributeList_XmlNamespace() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirAttributeListPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeValue::class.java)))
        assertThat(ns.uri!!.text, `is`("\"http://www.example.com/a\""))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    fun testDirAttributeList_XmlNamespace_MissingValue() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingValue.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirAttributeListPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`(nullValue()))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    fun testDirAttributeList_XmlNamespace_MissingMiddleValue() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingMiddleValue.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirAttributeListPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`(nullValue()))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    // endregion
    // region DirElemConstructor

    fun testDirElemConstructor_NamespaceResolver() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val provider = dirElemConstructorPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("a"), `is`(nullValue()))
    }

    fun testDirElemConstructor_AttributeList_NamespaceResolver() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val provider = dirElemConstructorPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("a"), `is`(nullValue()))
    }

    fun testDirElemConstructor_XmlNamespace_NamespaceResolver() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirElemConstructorPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeValue::class.java)))
        assertThat(ns.uri!!.text, `is`("\"http://www.example.com/a\""))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    fun testDirElemConstructor_XmlNamespace_MissingValue_NamespaceResolver() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingValue.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirElemConstructorPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`(nullValue()))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    fun testDirElemConstructor_XmlNamespace_MissingMiddleValue_NamespaceResolver() {
        val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute_MissingMiddleValue.xq")!!

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val provider = dirElemConstructorPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("a")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(XPathNCNamePsiImpl::class.java)))
        assertThat(ns.prefix!!.text, `is`("a"))

        assertThat(ns.uri, `is`(nullValue()))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDirAttributeList::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(dirAttributeListPsi))
    }

    // endregion
    // region Module

    fun testModule() {
        val file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")!!

        val provider = file as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("test"), `is`(nullValue()))

        val ns = provider.resolveNamespace("local")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`(notNullValue()))
        assertThat(ns.prefix!!.text, `is`("local"))

        assertThat(ns.uri, `is`<PsiElement>(instanceOf<PsiElement>(XQueryUriLiteral::class.java)))
        assertThat((ns.uri as XdmLexicalValue).lexicalRepresentation,
                `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))
    }

    // endregion
    // region Prolog

    fun testProlog_NoNamespaceProviders() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")!!

        val prologPsi = file.descendants().filterIsInstance<XQueryProlog>().first()
        val provider = prologPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("test"), `is`(nullValue()))
    }

    fun testProlog_NamespaceDecl() {
        val file = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq")!!

        val prologPsi = file.descendants().filterIsInstance<XQueryProlog>().first()
        val namespaceDeclPsi = file.descendants().filterIsInstance<XQueryNamespaceDecl>().first()
        val provider = prologPsi as XQueryNamespaceResolver

        assertThat<XQueryNamespace>(provider.resolveNamespace(null), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("abc"), `is`(nullValue()))
        assertThat<XQueryNamespace>(provider.resolveNamespace("testing"), `is`(nullValue()))

        val ns = provider.resolveNamespace("test")
        assertThat<XQueryNamespace>(ns, `is`(notNullValue()))

        assertThat(ns!!.prefix, `is`<PsiElement>(instanceOf<PsiElement>(LeafPsiElement::class.java)))
        assertThat(ns.prefix!!.text, `is`("test"))

        assertThat(ns.uri, `is`<PsiElement>(instanceOf<PsiElement>(XQueryUriLiteral::class.java)))
        assertThat((ns.uri as XdmLexicalValue).lexicalRepresentation, `is`("http://www.example.org/test"))

        assertThat(ns.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))
        assertThat(ns.declaration, `is`<PsiElement>(namespaceDeclPsi))
    }

    // endregion
    // endregion
    // region XPathParamList

    fun testParamList() {
        val file = parseResource("tests/parser/xquery-1.0/ParamList.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val paramListPsi = functionDeclPsi.children().filterIsInstance<XPathParamList>().first()
        assertThat(paramListPsi, `is`(notNullValue()))
        assertThat(paramListPsi.arity, `is`(2))
    }

    // endregion
    // region XPathURIQualifiedName

    fun testURIQualifiedName() {
        val file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq")!!

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val qnamePsi = optionDeclPsi.children().filterIsInstance<XPathURIQualifiedName>().first()

        assertThat(qnamePsi.prefix, `is`(notNullValue()))
        assertThat(qnamePsi.prefix!!.node.elementType, `is`<IElementType>(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(qnamePsi.prefix!!.text, `is`("Q{one{two}"))

        assertThat(qnamePsi.localName, `is`(notNullValue()))
        assertThat(qnamePsi.localName!!.node.elementType, `is`<IElementType>(XQueryElementType.NCNAME))
        assertThat(qnamePsi.localName!!.text, `is`("three"))
    }

    // endregion
    // region XQueryVariableResolver
    // region AnnotatedDecl

    fun testAnnotatedDecl_VarDecl() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varNamePsi = varDeclPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = annotatedDeclPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryVarDecl::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(varDeclPsi))
    }

    fun testAnnotatedDecl_FunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val functionNamePsi = functionDeclPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = annotatedDeclPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))
        assertThat(provider.resolveVariable(functionNamePsi), `is`(nullValue()))
    }

    // endregion
    // region CaseClause

    fun testCaseClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq")!!

        val typeswitchExprPsi = file.descendants().filterIsInstance<XQueryTypeswitchExpr>().first()
        val caseClausePsi = typeswitchExprPsi.children().filterIsInstance<XQueryCaseClause>().first()
        val varNamePsi = caseClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = caseClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCaseClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(caseClausePsi))
    }

    // endregion
    // region CountClause

    fun testCountClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/CountClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val countClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryCountClause>().first()
        val varNamePsi = countClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = intermediateClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCountClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(countClausePsi))
    }

    // endregion
    // region ForBinding

    fun testForBinding_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = forBindingPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryForBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(forBindingPsi))
    }

    fun testForBinding_PositionalVar_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/PositionalVar.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()

        val positionalVarPsi = forBindingPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()

        val provider = forBindingPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // bound variable

        var variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryForBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(forBindingPsi))

        // positional variable

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))
    }

    // endregion
    // region ForClause

    fun testForClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = forClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryForBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(forBindingPsi))
    }

    // endregion
    // region FunctionDecl

    fun testFunctionDecl_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/Param.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val paramListPsi = functionDeclPsi.children().filterIsInstance<XPathParamList>().first()
        val paramPsi = paramListPsi.children().filterIsInstance<XPathParam>().first()
        val paramNamePsi = paramPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = functionDeclPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(paramNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(paramNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XPathParam::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(paramPsi))
    }

    // endregion
    // region GroupByClause

    fun testGroupByClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val groupByClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryGroupByClause>().first()
        val groupingSpecListPsi = groupByClausePsi.children().filterIsInstance<XQueryGroupingSpecList>().first()
        val groupingSpecPsi = groupingSpecListPsi.children().filterIsInstance<XQueryGroupingSpec>().first()
        val groupingVariablePsi = groupingSpecPsi.children().filterIsInstance<XQueryGroupingVariable>().first()
        val varNamePsi = groupingVariablePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = groupByClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryGroupingVariable::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(groupingVariablePsi))
    }

    // endregion
    // region GroupingSpec

    fun testGroupingSpec_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val groupByClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryGroupByClause>().first()
        val groupingSpecListPsi = groupByClausePsi.children().filterIsInstance<XQueryGroupingSpecList>().first()
        val groupingSpecPsi = groupingSpecListPsi.children().filterIsInstance<XQueryGroupingSpec>().first()
        val groupingVariablePsi = groupingSpecPsi.children().filterIsInstance<XQueryGroupingVariable>().first()
        val varNamePsi = groupingVariablePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = groupingSpecPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryGroupingVariable::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(groupingVariablePsi))
    }

    // endregion
    // region GroupingSpecList

    fun testGroupingSpecList_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val groupByClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryGroupByClause>().first()
        val groupingSpecListPsi = groupByClausePsi.children().filterIsInstance<XQueryGroupingSpecList>().first()
        val groupingSpecPsi = groupingSpecListPsi.children().filterIsInstance<XQueryGroupingSpec>().first()
        val groupingVariablePsi = groupingSpecPsi.children().filterIsInstance<XQueryGroupingVariable>().first()
        val varNamePsi = groupingVariablePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = groupingSpecListPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryGroupingVariable::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(groupingVariablePsi))
    }

    // endregion
    // region GroupingVariable

    fun testGroupingVariable_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val groupByClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryGroupByClause>().first()
        val groupingSpecListPsi = groupByClausePsi.children().filterIsInstance<XQueryGroupingSpecList>().first()
        val groupingSpecPsi = groupingSpecListPsi.children().filterIsInstance<XQueryGroupingSpec>().first()
        val groupingVariablePsi = groupingSpecPsi.children().filterIsInstance<XQueryGroupingVariable>().first()
        val varNamePsi = groupingVariablePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = groupingVariablePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryGroupingVariable::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(groupingVariablePsi))
    }

    // endregion
    // region IntermediateClause

    fun testIntermediateClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val forClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = intermediateClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryForBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(forBindingPsi))
    }

    // endregion
    // region LetBinding

    fun testLetBinding_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause.xq")!!

        val letClausePsi = file.descendants().filterIsInstance<XQueryLetClause>().first()
        val letBindingPsi = letClausePsi.children().filterIsInstance<XQueryLetBinding>().first()
        val varNamePsi = letBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = letBindingPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryLetBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(letBindingPsi))
    }

    // endregion
    // region LetClause

    fun testLetClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause.xq")!!

        val letClausePsi = file.descendants().filterIsInstance<XQueryLetClause>().first()
        val letBindingPsi = letClausePsi.children().filterIsInstance<XQueryLetBinding>().first()
        val varNamePsi = letBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = letClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryLetBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(letBindingPsi))
    }

    // endregion
    // region Param

    fun testParam_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/Param.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val paramListPsi = functionDeclPsi.children().filterIsInstance<XPathParamList>().first()
        val paramPsi = paramListPsi.children().filterIsInstance<XPathParam>().first()
        val paramNamePsi = paramPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = paramPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(paramNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(paramNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XPathParam::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(paramPsi))
    }

    // endregion
    // region ParamList

    fun testParamList_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/Param.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val paramListPsi = functionDeclPsi.children().filterIsInstance<XPathParamList>().first()
        val paramPsi = paramListPsi.children().filterIsInstance<XPathParam>().first()
        val paramNamePsi = paramPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = paramListPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(paramNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(paramNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XPathParam::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(paramPsi))
    }

    // endregion
    // region PositionalVar

    fun testPositionalVar_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/PositionalVar.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val positionalVarPsi = forBindingPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val varNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = positionalVarPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))
    }

    // endregion
    // region Prolog

    fun testProlog_VarDecl() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varNamePsi = varDeclPsi.children().filterIsInstance<XPathEQName>().first()

        val prologPsi = file.descendants().filterIsInstance<XQueryProlog>().first()
        val provider = prologPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryVarDecl::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(varDeclPsi))
    }

    fun testProlog_FunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val functionNamePsi = functionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        val prologPsi = file.descendants().filterIsInstance<XQueryProlog>().first()
        val provider = prologPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))
        assertThat(provider.resolveVariable(functionNamePsi), `is`(nullValue()))
    }

    // endregion
    // region QuantifiedExpr

    fun testQuantifiedExpr_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq")!!

        val quantifiedExprPsi = file.descendants().filterIsInstance<XPathQuantifiedExpr>().first()
        val quantifiedExprBindingPsi = quantifiedExprPsi.children().filterIsInstance<XPathQuantifiedExprBinding>().first()
        val varNamePsi = quantifiedExprBindingPsi.children().filterIsInstance<XPathVarName>().first()
        val provider = quantifiedExprPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XPathQuantifiedExprBinding::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(quantifiedExprBindingPsi))
    }

    // endregion
    // region SlidingWindowClause

    fun testSlidingWindowClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val varNamePsi = slidingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = slidingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQuerySlidingWindowClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(slidingWindowClausePsi))
    }

    fun testSlidingWindowClause_WindowEndCondition_VariableProvider() {
        val file = parseResource("tests/psi/xquery-3.0/SlidingWindowClause_EndCondition_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val windowEndConditionPsi = slidingWindowClausePsi.children().filterIsInstance<XQueryWindowEndCondition>().first()
        val windowVarsPsi = windowEndConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = slidingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    fun testSlidingWindowClause_WindowStartCondition_VariableProvider() {
        val file = parseResource("tests/psi/xquery-3.0/SlidingWindowClause_StartCondition_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val windowStartConditionPsi = slidingWindowClausePsi.children().filterIsInstance<XQueryWindowStartCondition>().first()
        val windowVarsPsi = windowStartConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = slidingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    // endregion
    // region TumblingWindowClause

    fun testTumblingWindowClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val varNamePsi = tumblingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = tumblingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryTumblingWindowClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(tumblingWindowClausePsi))
    }

    fun testTumblingWindowClause_WindowEndCondition_VariableProvider() {
        val file = parseResource("tests/psi/xquery-3.0/TumblingWindowClause_EndCondition_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val windowEndConditionPsi = tumblingWindowClausePsi.children().filterIsInstance<XQueryWindowEndCondition>().first()
        val windowVarsPsi = windowEndConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = tumblingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    fun testTumblingWindowClause_WindowStartCondition_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val windowStartConditionPsi = tumblingWindowClausePsi.children().filterIsInstance<XQueryWindowStartCondition>().first()
        val windowVarsPsi = windowStartConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = tumblingWindowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    // endregion
    // region TypeswitchExpr

    fun testTypeswitchExpr_Default_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.xq")!!

        val typeswitchExprPsi = file.descendants().filterIsInstance<XQueryTypeswitchExpr>().first()
        val defaultCasePsi = typeswitchExprPsi.children().filterIsInstance<XQueryDefaultCaseClause>().first()
        val varNamePsi = defaultCasePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = typeswitchExprPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryDefaultCaseClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(defaultCasePsi))
    }

    // endregion
    // region VarDecl

    fun testVarDecl_VariableProvider() {
        val file = parseResource("tests/parser/xquery-1.0/VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varNamePsi = varDeclPsi.children().filterIsInstance<XPathEQName>().first()
        val provider = varDeclPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathEQName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryVarDecl::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(varDeclPsi))
    }

    // endregion
    // region WindowClause

    fun testWindowClause_SlidingWindowClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val varNamePsi = slidingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = windowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQuerySlidingWindowClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(slidingWindowClausePsi))
    }

    fun testWindowClause_TumblingWindowClause_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val varNamePsi = tumblingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()
        val provider = windowClausePsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        val variable = provider.resolveVariable(varNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(varNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryTumblingWindowClause::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(tumblingWindowClausePsi))
    }

    // endregion
    // region WindowEndCondition

    fun testWindowEndCondition_VariableProvider() {
        val file = parseResource("tests/psi/xquery-3.0/TumblingWindowClause_EndCondition_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val windowEndConditionPsi = tumblingWindowClausePsi.children().filterIsInstance<XQueryWindowEndCondition>().first()
        val windowVarsPsi = windowEndConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = windowEndConditionPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    // endregion
    // region WindowStartCondition

    fun testWindowStartCondition_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val windowStartConditionPsi = tumblingWindowClausePsi.children().filterIsInstance<XQueryWindowStartCondition>().first()
        val windowVarsPsi = windowStartConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = windowStartConditionPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    // endregion
    // region WindowVars

    fun testWindowVars_VariableProvider() {
        val file = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val windowStartConditionPsi = tumblingWindowClausePsi.children().filterIsInstance<XQueryWindowStartCondition>().first()
        val windowVarsPsi = windowStartConditionPsi.children().filterIsInstance<XQueryWindowVars>().first()
        val currentItemPsi = windowVarsPsi.children().filterIsInstance<XQueryCurrentItem>().first()
        val positionalVarPsi = windowVarsPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val posVarNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()
        val previousItemPsi = windowVarsPsi.children().filterIsInstance<XQueryPreviousItem>().first()
        val nextItemPsi = windowVarsPsi.children().filterIsInstance<XQueryNextItem>().first()
        val provider = windowVarsPsi as XQueryVariableResolver

        assertThat(provider.resolveVariable(null), `is`(nullValue()))

        // current

        var variable = provider.resolveVariable(currentItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryCurrentItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(currentItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // positional

        variable = provider.resolveVariable(posVarNamePsi)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(posVarNamePsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPositionalVar::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(positionalVarPsi))

        // previous

        variable = provider.resolveVariable(previousItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryPreviousItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(previousItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))

        // next

        variable = provider.resolveVariable(nextItemPsi.firstChild as XPathEQName)!!

        assertThat(variable.variable, `is`<PsiElement>(instanceOf<PsiElement>(XQueryNextItem::class.java)))
        assertThat(variable.variable, `is`<PsiElement>(nextItemPsi))

        assertThat(variable.declaration, `is`<PsiElement>(instanceOf<PsiElement>(XQueryWindowVars::class.java)))
        assertThat(variable.declaration, `is`<PsiElement>(windowVarsPsi))
    }

    // endregion
    // endregion
    // region XQueryVersionDecl

    fun testVersionDecl() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("1.0"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_CommentBeforeDecl() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_CommentBeforeDecl.xq")!!

        val versionDeclPsi = file.children().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("1.0"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_EmptyVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_EmptyVersion.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`(""))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_WithEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("1.0"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmLexicalValue).lexicalRepresentation, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_WithEncoding_CommentsAsWhitespace() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_CommentsAsWhitespace.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("1.0"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmLexicalValue).lexicalRepresentation, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_WithEmptyEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_EmptyEncoding.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("1.0"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmLexicalValue).lexicalRepresentation, `is`(""))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_NoVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_NoVersion.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(nullValue()))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    fun testVersionDecl_UnsupportedVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_UnsupportedVersion.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmLexicalValue).lexicalRepresentation, `is`("9.4"))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`<XPathStringLiteral>(versionDeclPsi.version))
    }

    fun testVersionDecl_EncodingOnly() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(nullValue()))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmLexicalValue).lexicalRepresentation, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    fun testVersionDecl_EncodingOnly_EmptyEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-3.0/VersionDecl_EncodingOnly_EmptyEncoding.xq")!!

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat<XPathStringLiteral>(versionDeclPsi.version, `is`(nullValue()))
        assertThat<XPathStringLiteral>(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmLexicalValue).lexicalRepresentation, `is`(""))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat<XPathStringLiteral>(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    // endregion
}
