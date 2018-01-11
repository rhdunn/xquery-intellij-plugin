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
package uk.co.reecedunn.intellij.plugin.xquery.tests.model

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryModelTest : ParserTestCase() {
    private inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().toList()
    }

    private inline fun <reified T> parseLiteral(xquery: String): XdmStaticValue {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .first() as XdmStaticValue
    }

    // region Lexical Values
    // region BracedUriLiteral (XdmStaticValue)

    fun testBracedUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("<áā\uD835\uDD04≪̸&;&gt"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testBracedUriLiteral_CharRef() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region StringLiteral (XdmStaticValue)

    fun testStringLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_CharRef() {
        val literal = parseLiteral<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region UriLiteral (XdmStaticValue)

    fun testUriLiteral() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com\uFFFF\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_Unclosed() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_EscapeApos() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = '''\"\"'")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_EscapeQuot() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"''\"\"\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("''\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_CharRef() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DirAttributeValue (XdmStaticValue)

    fun testDirAttributeValue() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com\uFFFF\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_Unclosed() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("http://www.example.com"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_EscapeApos() {
        val literal = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("'\"\"{}"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_EscapeQuot() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("''\"{}"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_CharRef() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
        assertThat(literal.staticValue as String, `is`("\u00A0\u00A0\u0020"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDirAttributeValue_EnclosedExpr() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"x{\$y}z\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsUntyped as XdmSequenceType))
        assertThat(literal.staticValue, `is`(nullValue()))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Namespaces
    // region DirAttribute (XPathNamespaceDeclaration)

    fun testDirAttribute_Xmlns() {
        val expr = parse<XQueryDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("b"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testDirAttribute_Xmlns_NoNamespaceUri() {
        val expr = parse<XQueryDirAttribute>("<a xmlns:b=>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("b"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    fun testDirAttribute() {
        val expr = parse<XQueryDirAttribute>("<a b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsString as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    // endregion
    // region ModuleDecl (XPathNamespaceDeclaration)

    fun testModuleDecl() {
        val expr = parse<XQueryModuleDecl>("module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testModuleDecl_NoNamespacePrefix() {
        val expr = parse<XQueryModuleDecl>("module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testModuleDecl_NoNamespaceUri() {
        val expr = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region ModuleImport (XPathNamespaceDeclaration)

    fun testModuleImport() {
        val expr = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testModuleImport_NoNamespacePrefix() {
        val expr = parse<XQueryModuleImport>("import module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testModuleImport_NoNamespaceUri() {
        val expr = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region NamespaceDecl (XPathNamespaceDeclaration)

    fun testNamespaceDecl() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testNamespaceDecl_NoNamespacePrefix() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testNamespaceDecl_NoNamespaceUri() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region SchemaImport (XPathNamespaceDeclaration)

    fun testSchemaImport() {
        val expr = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testSchemaImport_NoNamespacePrefix() {
        val expr = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    fun testSchemaImport_NoNamespaceUri() {
        val expr = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region Variables
    // region CaseClause (XPathVariableBinding)

    fun testCaseClause_NCName() {
        val expr = parse<XQueryCaseClause>("typeswitch (\$x) case \$y as xs:string return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCaseClause_QName() {
        val expr = parse<XQueryCaseClause>("typeswitch (\$a:x) case \$a:y as xs:string return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCaseClause_URIQualifiedName() {
        val expr = parse<XQueryCaseClause>(
                "typeswitch (\$Q{http://www.example.com}x) " +
                "case \$Q{http://www.example.com}y as xs:string " +
                "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testCaseClause_NoVarName() {
        val expr = parse<XQueryCaseClause>("typeswitch (\$x) case xs:string return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region CountClause (XPathVariableBinding)

    fun testCountClause_NCName() {
        val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCountClause_QName() {
        val expr = parse<XQueryCountClause>("for \$a:x in \$a:y count \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCountClause_URIQualifiedName() {
        val expr = parse<XQueryCountClause>(
                "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                        "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testCountClause_MissingVarName() {
        val expr = parse<XQueryCountClause>("for \$x in \$y count \$")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region CurrentItem (XPathVariableBinding)

    fun testCurrentItem_NCName() {
        val expr = parse<XQueryCurrentItem>("for sliding window \$x in \$y start \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCurrentItem_QName() {
        val expr = parse<XQueryCurrentItem>("for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testCurrentItem_URIQualifiedName() {
        val expr = parse<XQueryCurrentItem>(
                "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                "start \$Q{http://www.example.com}w when true() " +
                "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DefaultCaseClause (XPathVariableBinding)

    fun testDefaultCaseClause_NCName() {
        val expr = parse<XQueryDefaultCaseClause>("typeswitch (\$x) default \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testDefaultCaseClause_QName() {
        val expr = parse<XQueryDefaultCaseClause>("typeswitch (\$a:x) default \$a:y return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testDefaultCaseClause_URIQualifiedName() {
        val expr = parse<XQueryDefaultCaseClause>(
                "typeswitch (\$Q{http://www.example.com}x) " +
                "default \$Q{http://www.example.com}y " +
                "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testDefaultCaseClause_NoVarName() {
        val expr = parse<XQueryDefaultCaseClause>("typeswitch (\$x) default return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region ForBinding (XPathVariableBinding)

    fun testForBinding_NCName() {
        val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testForBinding_QName() {
        val expr = parse<XQueryForBinding>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testForBinding_URIQualifiedName() {
        val expr = parse<XQueryForBinding>(
            "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
            "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testForBinding_MissingVarName() {
        val expr = parse<XQueryForBinding>("for \$ \$y return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region GroupingSpec (XPathVariableBinding)

    fun testGroupingSpec_NCName() {
        val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val groupvar = (expr as PsiElement).children().filterIsInstance<XQueryGroupingVariable>().first()
        val varname = groupvar.children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testGroupingSpec_QName() {
        val expr = parse<XQueryGroupingSpec>("for \$a:x in \$a:y group by \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val groupvar = (expr as PsiElement).children().filterIsInstance<XQueryGroupingVariable>().first()
        val varname = groupvar.children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testGroupingSpec_URIQualifiedName() {
        val expr = parse<XQueryGroupingSpec>(
                "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                "group by \$Q{http://www.example.com}z " +
                "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val groupvar = (expr as PsiElement).children().filterIsInstance<XQueryGroupingVariable>().first()
        val varname = groupvar.children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testGroupingSpec_MissingVarName() {
        val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region GroupingVariable (XPathVariableName)

    fun testGroupingVariable_NCName() {
        val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testGroupingVariable_QName() {
        val expr = parse<XQueryGroupingVariable>("for \$a:x in \$a:y group by \$a:z return \$a:w")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testGroupingVariable_URIQualifiedName() {
        val expr = parse<XQueryGroupingVariable>(
                "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                "group by \$Q{http://www.example.com}z " +
                "return \$Q{http://www.example.com}w")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testGroupingVariable_MissingVarName() {
        val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region LetBinding (XPathVariableBinding)

    fun testLetBinding_NCName() {
        val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testLetBinding_QName() {
        val expr = parse<XQueryLetBinding>("let \$a:x := 2 return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testLetBinding_URIQualifiedName() {
        val expr = parse<XQueryLetBinding>(
                "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLetBinding_MissingVarName() {
        val expr = parse<XQueryLetBinding>("let \$ := 2 return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region NextItem (XPathVariableBinding)

    fun testNextItem_NCName() {
        val expr = parse<XQueryNextItem>("for sliding window \$x in \$y start \$v next \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testNextItem_QName() {
        val expr = parse<XQueryNextItem>(
                "for sliding window \$a:x in \$a:y start \$a:v next \$a:w when true() return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testNextItem_URIQualifiedName() {
        val expr = parse<XQueryNextItem>(
                "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                "start \$Q{http://www.example.com}v next \$Q{http://www.example.com}w when true() " +
                "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region PositionalVar (XPathVariableBinding)

    fun testPositionalVar_NCName() {
        val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testPositionalVar_QName() {
        val expr = parse<XQueryPositionalVar>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testPositionalVar_URIQualifiedName() {
        val expr = parse<XQueryPositionalVar>(
                "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                        "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPositionalVar_MissingVarName() {
        val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(XsInteger as XdmSequenceType))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region PreviousItem (XPathVariableBinding)

    fun testPreviousItem_NCName() {
        val expr = parse<XQueryPreviousItem>("for sliding window \$x in \$y start \$v previous \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testPreviousItem_QName() {
        val expr = parse<XQueryPreviousItem>(
                "for sliding window \$a:x in \$a:y start \$a:v previous \$a:w when true() return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testPreviousItem_URIQualifiedName() {
        val expr = parse<XQueryPreviousItem>(
                "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                "start \$Q{http://www.example.com}v previous \$Q{http://www.example.com}w when true() " +
                "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region SlidingWindowClause (XPathVariableBinding)

    fun testSlidingWindowClause_NCName() {
        val expr = parse<XQuerySlidingWindowClause>("for sliding window \$x in \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSlidingWindowClause_QName() {
        val expr = parse<XQuerySlidingWindowClause>("for sliding window \$a:x in \$a:y return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSlidingWindowClause_URIQualifiedName() {
        val expr = parse<XQuerySlidingWindowClause>(
                "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                        "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSlidingWindowClause_MissingVarName() {
        val expr = parse<XQuerySlidingWindowClause>("for sliding window \$ \$y return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region TumblingWindowClause (XPathVariableBinding)

    fun testTumblingWindowClause_NCName() {
        val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$x in \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testTumblingWindowClause_QName() {
        val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$a:x in \$a:y return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testTumblingWindowClause_URIQualifiedName() {
        val expr = parse<XQueryTumblingWindowClause>(
                "for tumbling window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                        "return \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testTumblingWindowClause_MissingVarName() {
        val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$ \$y return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarDecl (XPathVariableBinding)

    fun testVarDecl_NCName() {
        val expr = parse<XQueryVarDecl>("declare variable \$x := \$y;")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarDecl_QName() {
        val expr = parse<XQueryVarDecl>("declare variable \$a:x := \$a:y;")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarDecl_URIQualifiedName() {
        val expr = parse<XQueryVarDecl>(
                "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testVarDecl_MissingVarName() {
        val expr = parse<XQueryVarDecl>("declare variable \$ := \$y;")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
}
