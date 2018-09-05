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
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDefaultCaseClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginBlockVarDeclEntry
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginNamedKindTest
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class XQueryModelTest : ParserTestCase() {
    // region Lexical Values
    // region DirAttributeValue (XdmStaticValue)

    @Test
    fun testDirAttributeValue() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com\uFFFF\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_Unclosed() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("http://www.example.com"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_EscapeApos() {
        val literal = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("'\"\"{}"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_EscapeQuot() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("''\"{}"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_CharRef() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsString))
        assertThat(literal.staticValue as String, `is`("\u00A0\u00A0\u0020"))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDirAttributeValue_EnclosedExpr() {
        val literal = parse<XQueryDirAttributeValue>("<a b=\"x{\$y}z\"/>")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticType, `is`(XsUntyped))
        assertThat(literal.staticValue, `is`(nullValue()))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Sequence Types
    // region TextTest (XPathTypeDeclaration) [MarkLogic]

    @Test
    fun testTextTest_MarkLogicJson_Any() {
        val expr = parse<XPathTextTest>("\$x instance of text(*)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        // TODO: Expose the MarkLogic JSON syntax to the XDM data model.
        assertThat(expr.declaredType, `is`(XdmText))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testTextTest_MarkLogicJson_KeyName() {
        val expr = parse<XPathTextTest>("\$x instance of text(\"test\")")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        // TODO: Expose the MarkLogic JSON syntax to the XDM data model.
        assertThat(expr.declaredType, `is`(XdmText))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region AnyKindTest (XPathTypeDeclaration) [MarkLogic]

    @Test
    fun testAnyKindTest_MarkLogicJson_Any() {
        val expr = parse<XPathAnyKindTest>("\$x instance of node(*)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        // TODO: Expose the MarkLogic JSON syntax to the XDM data model.
        assertThat(expr.declaredType, `is`(XdmNode))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testAnyKindTest_MarkLogicJson_KeyName() {
        val expr = parse<PluginNamedKindTest>("\$x instance of node(\"test\")")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        // TODO: Expose the MarkLogic JSON syntax to the XDM data model.
        assertThat(expr.declaredType, `is`(XdmNode))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Namespaces
    // region DirAttribute (XPathNamespaceDeclaration)

    @Test
    fun testDirAttribute_Xmlns() {
        val expr = parse<PluginDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("b"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testDirAttribute_Xmlns_NoNamespaceUri() {
        val expr = parse<PluginDirAttribute>("<a xmlns:b=>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("b"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    @Test
    fun testDirAttribute() {
        val expr = parse<PluginDirAttribute>("<a b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsString))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    // endregion
    // region ModuleDecl (XPathNamespaceDeclaration)

    @Test
    fun testModuleDecl() {
        val expr = parse<XQueryModuleDecl>("module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testModuleDecl_NoNamespacePrefix() {
        val expr = parse<XQueryModuleDecl>("module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testModuleDecl_NoNamespaceUri() {
        val expr = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region ModuleImport (XPathNamespaceDeclaration)

    @Test
    fun testModuleImport() {
        val expr = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testModuleImport_NoNamespacePrefix() {
        val expr = parse<XQueryModuleImport>("import module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testModuleImport_NoNamespaceUri() {
        val expr = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region NamespaceDecl (XPathNamespaceDeclaration)

    @Test
    fun testNamespaceDecl() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testNamespaceDecl_NoNamespacePrefix() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testNamespaceDecl_NoNamespaceUri() {
        val expr = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // region SchemaImport (XPathNamespaceDeclaration)

    @Test
    fun testSchemaImport() {
        val expr = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testSchemaImport_NoNamespacePrefix() {
        val expr = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(nullValue()))

        assertThat(expr.namespaceUri, `is`(notNullValue()))
        assertThat(expr.namespaceUri?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespaceUri?.staticType, `is`(XsAnyURI))
        assertThat(expr.namespaceUri?.staticValue as String, `is`("http://www.example.com"))
    }

    @Test
    fun testSchemaImport_NoNamespaceUri() {
        val expr = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XPathNamespaceDeclaration

        assertThat(expr.namespacePrefix, `is`(notNullValue()))
        assertThat(expr.namespacePrefix?.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.namespacePrefix?.staticType, `is`(XsNCName))
        assertThat(expr.namespacePrefix?.staticValue as String, `is`("test"))

        assertThat(expr.namespaceUri, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region Variables
    // region BlockVarDeclEntry (XPathVariableDeclaration) [XQuery Scripting Extensions]

    @Test
    fun testBlockVarDeclEntry_NCName() {
        val expr = parse<PluginBlockVarDeclEntry>("block { declare \$x := \$y; 2 }")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testBlockVarDeclEntry_QName() {
        val expr = parse<PluginBlockVarDeclEntry>("block { declare \$a:x := \$a:y; 2 }")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testBlockVarDeclEntry_URIQualifiedName() {
        val expr = parse<PluginBlockVarDeclEntry>(
                "block { declare \$Q{http://www.example.com}x := \$Q{http://www.example.com}y; 2 }")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testBlockVarDeclEntry_MissingVarName() {
        val expr = parse<PluginBlockVarDeclEntry>("block { declare \$ := \$y; 2 }")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testBlockVarDeclEntry_Multiple_NCName() {
        val decls = parse<PluginBlockVarDeclEntry>("block { declare \$x := 1, \$y := 2; 3 }")
        assertThat(decls.size, `is`(2))

        val expr = decls[1] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region CaseClause (XPathVariableBinding)

    @Test
    fun testCaseClause_NCName() {
        val expr = parse<XQueryCaseClause>("typeswitch (\$x) case \$y as xs:string return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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

    @Test
    fun testCountClause_NCName() {
        val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testCountClause_QName() {
        val expr = parse<XQueryCountClause>("for \$a:x in \$a:y count \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testCountClause_URIQualifiedName() {
        val expr = parse<XQueryCountClause>(
                "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                        "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testCountClause_MissingVarName() {
        val expr = parse<XQueryCountClause>("for \$x in \$y count \$")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region CurrentItem (XPathVariableBinding)

    @Test
    fun testCurrentItem_NCName() {
        val expr = parse<XQueryCurrentItem>("for sliding window \$x in \$y start \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testCurrentItem_QName() {
        val expr = parse<XQueryCurrentItem>("for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DefaultCaseClause (XPathVariableBinding)

    @Test
    fun testDefaultCaseClause_NCName() {
        val expr = parse<PluginDefaultCaseClause>("typeswitch (\$x) default \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDefaultCaseClause_QName() {
        val expr = parse<PluginDefaultCaseClause>("typeswitch (\$a:x) default \$a:y return \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDefaultCaseClause_URIQualifiedName() {
        val expr = parse<PluginDefaultCaseClause>(
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testDefaultCaseClause_NoVarName() {
        val expr = parse<PluginDefaultCaseClause>("typeswitch (\$x) default return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region ForBinding (XPathVariableBinding)

    @Test
    fun testForBinding_NCName() {
        val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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

    @Test
    fun testGroupingSpec_NCName() {
        val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val groupvar = (expr as PsiElement).children().filterIsInstance<XQueryGroupingVariable>().first()
        val varname = groupvar.children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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

    @Test
    fun testGroupingVariable_NCName() {
        val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testGroupingVariable_QName() {
        val expr = parse<XQueryGroupingVariable>("for \$a:x in \$a:y group by \$a:z return \$a:w")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("z"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testGroupingVariable_MissingVarName() {
        val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region LetBinding (XPathVariableBinding)

    @Test
    fun testLetBinding_NCName() {
        val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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

    @Test
    fun testNextItem_NCName() {
        val expr = parse<XQueryNextItem>("for sliding window \$x in \$y start \$v next \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region PositionalVar (XPathVariableBinding)

    @Test
    fun testPositionalVar_NCName() {
        val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testPositionalVar_QName() {
        val expr = parse<XQueryPositionalVar>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testPositionalVar_URIQualifiedName() {
        val expr = parse<XQueryPositionalVar>(
                "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                        "return \$Q{http://www.example.com}w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testPositionalVar_MissingVarName() {
        val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(XsInteger))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region PreviousItem (XPathVariableBinding)

    @Test
    fun testPreviousItem_NCName() {
        val expr = parse<XQueryPreviousItem>("for sliding window \$x in \$y start \$v previous \$w when true() return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("w"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region SlidingWindowClause (XPathVariableBinding)

    @Test
    fun testSlidingWindowClause_NCName() {
        val expr = parse<XQuerySlidingWindowClause>("for sliding window \$x in \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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

    @Test
    fun testTumblingWindowClause_NCName() {
        val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$x in \$y return \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
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
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testTumblingWindowClause_MissingVarName() {
        val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$ \$y return \$w")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarDecl (XPathVariableDeclaration)

    @Test
    fun testVarDecl_NCName() {
        val expr = parse<XQueryVarDecl>("declare variable \$x := \$y;")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarDecl_QName() {
        val expr = parse<XQueryVarDecl>("declare variable \$a:x := \$a:y;")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarDecl_URIQualifiedName() {
        val expr = parse<XQueryVarDecl>(
                "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarDecl_MissingVarName() {
        val expr = parse<XQueryVarDecl>("declare variable \$ := \$y;")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
}
