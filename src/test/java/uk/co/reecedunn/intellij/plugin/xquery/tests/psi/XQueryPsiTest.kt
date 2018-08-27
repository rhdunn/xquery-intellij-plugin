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

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class XQueryPsiTest : ParserTestCase() {
    // region XPathArgumentList

    @Test
    fun testArgumentList() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(2))
    }

    @Test
    fun testArgumentList_Empty() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(0))
    }

    @Test
    fun testArgumentList_ArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val argumentListPsi = functionCallPsi.children().filterIsInstance<XPathArgumentList>().first()
        assertThat(argumentListPsi, `is`(notNullValue()))
        assertThat(argumentListPsi.arity, `is`(1))
    }

    // endregion
    // region XPathArrowFunctionSpecifier

    @Test
    fun testArrowFunctionSpecifier() {
        val file = parseResource("tests/psi/xquery-3.1/ArrowExpr_MultipleArguments.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(4))
    }

    @Test
    fun testArrowFunctionSpecifier_Empty() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(1))
    }

    @Test
    fun testArrowFunctionSpecifier_MissingArgumentList() {
        val file = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        assertThat(arrowFunctionSpecifierPsi.arity, `is`(1))
    }

    // endregion
    // region XQueryDefaultNamespaceDecl

    @Test
    fun testDefaultNamespaceDecl_Element() {
        val file = parseText("declare default element namespace 'http://www.w3.org/1999/xhtml';")
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue?.staticType, `is`(XsAnyURI))
        assertThat(decl.defaultValue?.staticValue as String, `is`("http://www.w3.org/1999/xhtml"))
    }

    @Test
    fun testDefaultNamespaceDecl_Function() {
        val file = parseText("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';")
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.Function))
        assertThat(decl.defaultValue?.staticType, `is`(XsAnyURI))
        assertThat(decl.defaultValue?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions/math"))
    }

    @Test
    fun testDefaultNamespaceDecl_EmptyNamespace() {
        val file = parseText("declare default element namespace '';")
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue, `is`(nullValue()))
    }

    @Test
    fun testDefaultNamespaceDecl_MissingNamespace() {
        val file = parseText("declare default element namespace;")
        val decl = file.descendants().filterIsInstance<XQueryDefaultNamespaceDecl>().first()

        assertThat(decl.type, `is`(XQueryDefaultNamespaceType.ElementOrType))
        assertThat(decl.defaultValue, `is`(nullValue()))
    }

    // endregion
    // region XQueryDirElemConstructor

    @Test
    fun testDirElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.staticValue as String, `is`("a"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName.staticValue as String, `is`("b"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(notNullValue()))
        assertThat(close!!.prefix, `is`(notNullValue()))
        assertThat(close.prefix!!.staticValue as String, `is`("a"))
        assertThat(close.localName, `is`(notNullValue()))
        assertThat(close.localName.staticValue as String, `is`("b"))
    }

    @Test
    fun testDirElemConstructor_SelfClosing() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(true))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.staticValue as String, `is`("h"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName.staticValue as String, `is`("br"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(nullValue()))
    }

    @Test
    fun testDirElemConstructor_MissingClosingTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.staticValue as String, `is`("a"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName.staticValue as String, `is`("b"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(nullValue()))
    }

    @Test
    fun testDirElemConstructor_IncompleteOpenTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(nullValue()))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(notNullValue()))
        assertThat(close!!.prefix, `is`(notNullValue()))
        assertThat(close.prefix!!.staticValue as String, `is`("a"))
        assertThat(close.localName, `is`(notNullValue()))
        assertThat(close.localName.staticValue as String, `is`("b"))
    }

    @Test
    fun testDirElemConstructor_IncompleteCloseTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()

        assertThat(dirElemConstructorPsi.isSelfClosing, `is`(false))

        val open = dirElemConstructorPsi.openTag
        assertThat(open, `is`(notNullValue()))
        assertThat(open!!.prefix, `is`(notNullValue()))
        assertThat(open.prefix!!.staticValue as String, `is`("a"))
        assertThat(open.localName, `is`(notNullValue()))
        assertThat(open.localName.staticValue as String, `is`("b"))

        val close = dirElemConstructorPsi.closeTag
        assertThat(close, `is`(nullValue()))
    }

    // endregion
    // region XPathEQName
    // region EQName

    @Test
    fun testEQName_QName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("xs"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    @Test
    fun testEQName_KeywordLocalPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordLocalPart.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("sort"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.K_LEAST))
        assertThat(eqnamePsi.localName!!.text, `is`("least"))
    }

    @Test
    fun testEQName_MissingLocalPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_MissingLocalPart.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(nullValue()))
        assertThat(eqnamePsi.localName, `is`(nullValue()))
    }

    @Test
    fun testEQName_KeywordPrefixPart() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_QName_KeywordPrefixPart.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.K_ORDER))
        assertThat(eqnamePsi.prefix!!.text, `is`("order"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("column"))
    }

    @Test
    fun testEQName_NCName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_NCName.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(nullValue()))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    @Test
    fun testEQName_URIQualifiedName() {
        val file = parseResource("tests/psi/xquery-3.0/SimpleTypeName_URIQualifiedName.xq")

        val castExprPsi = file.descendants().filterIsInstance<XPathCastExpr>().first()
        val singleTypePsi = castExprPsi.children().filterIsInstance<XPathSingleType>().first()
        val simpleTypeNamePsi = singleTypePsi.descendants().filterIsInstance<XPathSimpleTypeName>().first()
        val eqnamePsi = simpleTypeNamePsi.firstChild as XPathEQName

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(eqnamePsi.prefix!!.text, `is`("Q{http://www.w3.org/2001/XMLSchema}"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("double"))
    }

    // endregion
    // region NCName

    @Test
    fun testNCName() {
        val file = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(nullValue()))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.K_COLLATION))
        assertThat(eqnamePsi.localName!!.text, `is`("collation"))
    }

    // endregion
    // region QName

    @Test
    fun testQName() {
        val file = parseResource("tests/parser/xquery-1.0/QName.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("one"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("two"))
    }

    @Test
    fun testQName_KeywordLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("sort"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.K_LEAST))
        assertThat(eqnamePsi.localName!!.text, `is`("least"))
    }

    @Test
    fun testQName_MissingLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(nullValue()))
        assertThat(eqnamePsi.localName, `is`(nullValue()))
    }

    @Test
    fun testQName_KeywordPrefixPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val eqnamePsi = optionDeclPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.K_ORDER))
        assertThat(eqnamePsi.prefix!!.text, `is`("order"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("two"))
    }

    @Test
    fun testQName_DirElemConstructor() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val eqnamePsi = dirElemConstructorPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.XML_TAG_NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("a"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.XML_TAG_NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("b"))
    }

    @Test
    fun testQName_DirAttributeList() {
        val file = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")

        val dirElemConstructorPsi = file.descendants().filterIsInstance<XQueryDirElemConstructor>().first()
        val dirAttributeListPsi = dirElemConstructorPsi.children().filterIsInstance<XQueryDirAttributeList>().first()
        val dirAttributePsi = dirAttributeListPsi.children().filterIsInstance<PluginDirAttribute>().first()
        val eqnamePsi = dirAttributePsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(eqnamePsi.prefix, `is`(notNullValue()))
        assertThat(eqnamePsi.prefix!!.node.elementType, `is`(XQueryTokenType.XML_ATTRIBUTE_NCNAME))
        assertThat(eqnamePsi.prefix!!.text, `is`("xml"))

        assertThat(eqnamePsi.localName, `is`(notNullValue()))
        assertThat(eqnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.XML_ATTRIBUTE_NCNAME))
        assertThat(eqnamePsi.localName!!.text, `is`("id"))
    }

    // endregion
    // region Type :: Function :: FunctionCall

    @Test
    fun testEQNameType_FunctionCall_NCName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_NCName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_FunctionCall_QName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_FunctionCall_EQName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_EQName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Function :: NamedFunctionRef

    @Test
    fun testEQNameType_NamedFunctionRef_NCName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_NCName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_NamedFunctionRef_QName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_QName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_NamedFunctionRef_EQName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_EQName.xq")

        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("true"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Function :: ArrowFunctionSpecifier

    @Test
    fun testEQNameType_ArrowFunctionSpecifier_NCName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_NCName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_ArrowFunctionSpecifier_QName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_QName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.prefix!!.text, `is`("fn"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_ArrowFunctionSpecifier_EQName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_EQName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xpath-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.localName!!.text, `is`("upper-case"))

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Variable :: VarDecl

    @Test
    fun testEQNameType_VarDecl_NCName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_NCName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclQName = varDeclPsi.children().filterIsInstance<XPathVarName>().first()
        val name = varDeclQName.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(nullValue()))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.K_VALUE))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    @Test
    fun testEQNameType_VarDecl_QName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_QName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclQName = varDeclPsi.children().filterIsInstance<XPathVarName>().first()
        val name = varDeclQName.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(name.prefix!!.text, `is`("local"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.K_VALUE))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    @Test
    fun testEQNameType_VarDecl_EQName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_EQName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclQName = varDeclPsi.children().filterIsInstance<XPathVarName>().first()
        val name = varDeclQName.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.prefix, `is`(notNullValue()))
        assertThat(name.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(name.prefix!!.text, `is`("Q{http://www.w3.org/2005/xquery-local-functions}"))

        assertThat(name.localName, `is`(notNullValue()))
        assertThat(name.localName!!.node.elementType, `is`(XQueryTokenType.K_VALUE))
        assertThat(name.localName!!.text, `is`("value"))

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    // endregion
    // region resolveFunctionDecls

    @Test
    fun testQName_resolveFunctionDecls_SingleDeclMatch() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName.xq")

        val fn = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        val fnName = fn.children().filterIsInstance<XPathQName>().first()

        val decls = fnName.resolveFunctionDecls().toList()
        assertThat(decls.size, `is`(1))

        val functionName = decls[0].children().filterIsInstance<XPathQName>().first()
        assertThat(functionName.text, `is`("fn:true"))
        assertThat(decls[0].arity, `is`(0))
    }

    @Test
    fun testQName_resolveFunctionDecls_MultipleDeclMatch() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName_Arity.xq")

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

    @Test
    fun testFile_Empty() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseText("")

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

        settings.XQueryVersion = XQuery.REC_3_1_20170321.label

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
    }

    @Test
    fun testFile() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

        settings.XQueryVersion = XQuery.REC_3_1_20170321.label

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
    }

    // endregion
    // region XPathFunctionCall

    @Test
    fun testFunctionCall() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(2))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("math"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("pow"))
    }

    @Test
    fun testFunctionCall_Empty() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(0))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("fn"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    @Test
    fun testFunctionCall_ArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(1))

        assertThat(functionCallPsi.functionName?.prefix?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.prefix?.node?.text, `is`("math"))

        assertThat(functionCallPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionCallPsi.functionName?.localName?.node?.text, `is`("sin"))
    }

    @Test
    fun testFunctionCall_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-1.0/FunctionCall_NoFunctionEQName.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(0))

        assertThat(functionCallPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XQueryFunctionDecl

    @Test
    fun testFunctionDecl() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(0))

        assertThat(functionDeclPsi.functionName?.prefix?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionDeclPsi.functionName?.prefix?.node?.text, `is`("fn"))

        assertThat(functionDeclPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionDeclPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    @Test
    fun testFunctionDecl_ParamList() {
        val file = parseResource("tests/parser/xquery-1.0/ParamList.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(2))

        assertThat(functionDeclPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(functionDeclPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(functionDeclPsi.functionName?.localName?.node?.text, `is`("test"))
    }

    @Test
    fun testFunctionDecl_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-1.0/FunctionDecl_NoFunctionEQName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(0))

        assertThat(functionDeclPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XPathMapConstructorEntry

    @Test
    fun testMapConstructorEntry() {
        val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")

        val mapConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XPathMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`(XQueryTokenType.QNAME_SEPARATOR))
    }

    @Test
    fun testMapConstructorEntry_NoValueAssignmentOperator() {
        val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")

        val mapConstructorPsi = file.descendants().filterIsInstance<XPathMapConstructor>().first()
        val mapConstructorEntryPsi = mapConstructorPsi.children().filterIsInstance<XPathMapConstructorEntry>().first()

        assertThat(mapConstructorEntryPsi.separator.node.elementType,
                `is`(XQueryElementType.MAP_KEY_EXPR))
    }

    // endregion
    // region XQueryPrologResolver
    // region Module

    @Test
    fun testModule_PrologResolver_NoProlog() {
        val file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")

        val modules = file.children().filterIsInstance<XQueryLibraryModule>().toList()
        assertThat(modules.size, `is`(1))

        val provider = modules[0] as XQueryPrologResolver
        assertThat(provider.prolog, `is`(nullValue()))
    }

    @Test
    fun testModule_PrologResolver() {
        val file = parseResource("tests/resolve/namespaces/ModuleDecl.xq")

        val modules = file.children().filterIsInstance<XQueryLibraryModule>().toList()
        assertThat(modules.size, `is`(1))

        val provider = modules[0] as XQueryPrologResolver
        assertThat(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    // endregion
    // region ModuleDecl

    @Test
    fun testModuleDecl_PrologResolver_NoProlog() {
        val file = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleDecl>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(nullValue()))
    }

    @Test
    fun testModuleDecl_PrologResolver() {
        val file = parseResource("tests/resolve/namespaces/ModuleDecl.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleDecl>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    // endregion
    // region ModuleImport

    @Test
    fun testModuleImport_EmptyUri() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(nullValue()))
    }

    @Test
    fun testModuleImport_LocalPath_NoModule() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(nullValue()))
    }

    @Test
    fun testModuleImport_LocalPath_Module() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ParentDirectory.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.descendants()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("test:func"))
    }

    @Test
    fun testModuleImport_ResourceFile() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq")

        val provider = file.descendants().filterIsInstance<XQueryModuleImport>().first() as XQueryPrologResolver
        assertThat(provider.prolog, `is`(notNullValue()))

        val annotation = provider.prolog?.children()?.filterIsInstance<XQueryAnnotatedDecl>()?.first()
        val function = annotation?.children()?.filterIsInstance<XQueryFunctionDecl>()?.first()
        val functionName = function?.children()?.filterIsInstance<XPathQName>()?.first()
        assertThat(functionName?.text, `is`("array:append"))
    }

    // endregion
    // endregion
    // region XPathNamedFunctionRef

    @Test
    fun testNamedFunctionRef() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(3))

        assertThat(namedFunctionRefPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    @Test
    fun testNamedFunctionRef_MissingArity() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(0))

        assertThat(namedFunctionRefPsi.functionName?.prefix, `is`(nullValue()))

        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(namedFunctionRefPsi.functionName?.localName?.node?.text, `is`("true"))
    }

    @Test
    fun testNamedFunctionRef_NoFunctionEQName() {
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_NoFunctionEQName.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(0))

        assertThat(namedFunctionRefPsi.functionName, `is`(nullValue()))
    }

    // endregion
    // region XPathURIQualifiedName

    @Test
    fun testURIQualifiedName() {
        val file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq")

        val optionDeclPsi = file.descendants().filterIsInstance<XQueryOptionDecl>().first()
        val qnamePsi = optionDeclPsi.children().filterIsInstance<XPathURIQualifiedName>().first()

        assertThat(qnamePsi.prefix, `is`(notNullValue()))
        assertThat(qnamePsi.prefix!!.node.elementType, `is`(XQueryElementType.BRACED_URI_LITERAL))
        assertThat(qnamePsi.prefix!!.text, `is`("Q{one{two}"))

        assertThat(qnamePsi.localName, `is`(notNullValue()))
        assertThat(qnamePsi.localName!!.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(qnamePsi.localName!!.text, `is`("three"))
    }

    // endregion
    // region XQueryVersionDecl

    @Test
    fun testVersionDecl() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
        assertThat(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_CommentBeforeDecl() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_CommentBeforeDecl.xq")

        val versionDeclPsi = file.children().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
        assertThat(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_EmptyVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_EmptyVersion.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`(""))
        assertThat(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_WithEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
        assertThat(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_WithEncoding_CommentsAsWhitespace() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_CommentsAsWhitespace.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
        assertThat(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_WithEmptyEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_WithEncoding_EmptyEncoding.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
        assertThat(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmStaticValue).staticValue as String, `is`(""))

        assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_NoVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_NoVersion.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(nullValue()))
        assertThat(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    @Test
    fun testVersionDecl_UnsupportedVersion() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-1.0/VersionDecl_UnsupportedVersion.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(notNullValue()))
        assertThat((versionDeclPsi.version!! as XdmStaticValue).staticValue as String, `is`("9.4"))
        assertThat(versionDeclPsi.encoding, `is`(nullValue()))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat(file.XQueryVersion.declaration, `is`(versionDeclPsi.version))
    }

    @Test
    fun testVersionDecl_EncodingOnly() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(nullValue()))
        assertThat(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    @Test
    fun testVersionDecl_EncodingOnly_EmptyEncoding() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-3.0/VersionDecl_EncodingOnly_EmptyEncoding.xq")

        val versionDeclPsi = file.descendants().filterIsInstance<XQueryVersionDecl>().first()
        assertThat(versionDeclPsi.version, `is`(nullValue()))
        assertThat(versionDeclPsi.encoding, `is`(notNullValue()))
        assertThat((versionDeclPsi.encoding!! as XdmStaticValue).staticValue as String, `is`(""))

        assertThat(file.XQueryVersion.version, `is`(nullValue()))
        assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))
        assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
    }

    // endregion
}
