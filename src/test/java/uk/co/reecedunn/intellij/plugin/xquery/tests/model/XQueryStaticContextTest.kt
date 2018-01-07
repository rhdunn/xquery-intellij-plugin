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

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathStaticContext
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryStaticContextTest : ParserTestCase() {
    private inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().toList()
    }

    // region Default Namespace
    // region MainModule :: DefaultNamespaceDecl

    fun testMainModule_NoProlog() {
        val ctx = parse<XQueryMainModule>("<br/>")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testMainModule_NoDefaultNamespaceDecl() {
        val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testMainModule_DefaultNamespaceDecl_Element() {
        val ctx = parse<XQueryMainModule>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0] as XPathStaticContext

        val element = ctx.defaultElementOrTypeNamespace.toList()
        assertThat(element.size, `is`(1))
        assertThat(element[0].staticValue as String, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(element[0].staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testMainModule_DefaultNamespaceDecl_Element_EmptyNamespace() {
        val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testMainModule_DefaultNamespaceDecl_Function() {
        val ctx = parse<XQueryMainModule>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))

        val function = ctx.defaultFunctionNamespace.toList()
        assertThat(function.size, `is`(1))
        assertThat(function[0].staticValue as String, `is`("http://www.w3.org/2005/xpath-functions/math"))
        assertThat(function[0].staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testMainModule_DefaultNamespaceDecl_Function_EmptyNamespace() {
        val ctx = parse<XQueryMainModule>("declare default function namespace ''; pi()")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    // endregion
    // region Prolog :: DefaultNamespaceDecl

    fun testProlog_NoDefaultNamespaceDecl() {
        val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testProlog_DefaultNamespaceDecl_Element() {
        val ctx = parse<XQueryProlog>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0] as XPathStaticContext

        val element = ctx.defaultElementOrTypeNamespace.toList()
        assertThat(element.size, `is`(1))
        assertThat(element[0].staticValue as String, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(element[0].staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testProlog_DefaultNamespaceDecl_Element_EmptyNamespace() {
        val ctx = parse<XQueryProlog>("declare default element namespace ''; <br/>")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    fun testProlog_DefaultNamespaceDecl_Function() {
        val ctx = parse<XQueryProlog>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))

        val function = ctx.defaultFunctionNamespace.toList()
        assertThat(function.size, `is`(1))
        assertThat(function[0].staticValue as String, `is`("http://www.w3.org/2005/xpath-functions/math"))
        assertThat(function[0].staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testProlog_DefaultNamespaceDecl_Function_EmptyNamespace() {
        val ctx = parse<XQueryProlog>("declare default function namespace ''; pi()")[0] as XPathStaticContext

        assertThat(ctx.defaultElementOrTypeNamespace.count(), `is`(0))
        assertThat(ctx.defaultFunctionNamespace.count(), `is`(0))
    }

    // endregion
    // endregion
    // region In-Scope Namespaces
    // region DirElemConstructor -> DirAttributeList -> DirAttribute

    fun testInScopeNamespaces_DirAttribute_Xmlns() {
        val element = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{b:test()}</a>")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("b"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_DirAttribute_Xmlns_NoNamespaceUri() {
        val element = parse<XPathFunctionCall>("<a xmlns:b=>{b:test()}</a>")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_DirAttribute() {
        val element = parse<XPathFunctionCall>("<a b='http://www.example.com'>{b:test()}</a>")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    // endregion
    // region ModuleDecl

    fun testInScopeNamespaces_ModuleDecl() {
        val element = parse<XQueryFunctionDecl>("module namespace a='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_ModuleDecl_NoNamespacePrefix() {
        val element = parse<XQueryFunctionDecl>("module namespace ='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_ModuleDecl_NoNamespaceUri() {
        val element = parse<XQueryFunctionDecl>("module namespace a=; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    // endregion
    // region ModuleImport

    fun testInScopeNamespaces_ModuleImport_Prolog() {
        val element = parse<XQueryFunctionDecl>("import module namespace a='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_ModuleImport_MainModule() {
        val element = parse<XPathFunctionCall>("import module namespace a='http://www.example.com'; a:test();")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_ModuleImport_NoNamespacePrefix() {
        val element = parse<XQueryFunctionDecl>("import module namespace ='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_ModuleImport_NoNamespaceUri() {
        val element = parse<XQueryFunctionDecl>("import module namespace a=; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    // endregion
    // region NamespaceDecl

    fun testInScopeNamespaces_NamespaceDecl_Prolog() {
        val element = parse<XQueryFunctionDecl>("declare namespace a='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_NamespaceDecl_MainModule() {
        val element = parse<XPathFunctionCall>("declare namespace a='http://www.example.com'; a:test();")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_NamespaceDecl_NoNamespacePrefix() {
        val element = parse<XQueryFunctionDecl>("declare namespace ='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_NamespaceDecl_NoNamespaceUri() {
        val element = parse<XQueryFunctionDecl>("declare namespace a=; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    // endregion
    // region SchemaImport

    fun testInScopeNamespaces_SchemaImport_Prolog() {
        val element = parse<XQueryFunctionDecl>("import schema namespace a='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_SchemaImport_MainModule() {
        val element = parse<XPathFunctionCall>("import schema namespace a='http://www.example.com'; a:test();")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(6))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("a"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.example.com"))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_SchemaImport_NoNamespacePrefix() {
        val element = parse<XQueryFunctionDecl>("import schema namespace ='http://www.example.com'; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_SchemaImport_NoNamespaceUri() {
        val element = parse<XQueryFunctionDecl>("import schema namespace a=; declare function a:test() {};")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        // predefined XQuery 1.0 namespaces:

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    // endregion
    // endregion
    // region In-Scope Namespaces :: Predefined Namespaces

    fun testInScopeNamespaces_PredefinedNamespaces_XQuery10() {
        settings.implementationVersion = "w3c/1ed"
        settings.XQueryVersion = "1.0"

        val element = parse<XPathFunctionCall>("fn:true()")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(5))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))
    }

    fun testInScopeNamespaces_PredefinedNamespaces_MarkLogic60() {
        settings.implementationVersion = "marklogic/v6"
        settings.XQueryVersion = "1.0-ml"

        val element = parse<XPathFunctionCall>("fn:true()")[0]
        val namespaces = element.staticallyKnownNamespaces().toList()
        assertThat(namespaces.size, `is`(22))

        assertThat(namespaces[0].namespacePrefix?.staticValue as String, `is`("xsi"))
        assertThat(namespaces[0].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema-instance"))

        assertThat(namespaces[1].namespacePrefix?.staticValue as String, `is`("xs"))
        assertThat(namespaces[1].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(namespaces[2].namespacePrefix?.staticValue as String, `is`("xqterr"))
        assertThat(namespaces[2].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xqt-error"))

        assertThat(namespaces[3].namespacePrefix?.staticValue as String, `is`("xqe"))
        assertThat(namespaces[3].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xqe"))

        assertThat(namespaces[4].namespacePrefix?.staticValue as String, `is`("xml"))
        assertThat(namespaces[4].namespaceUri?.staticValue as String, `is`("http://www.w3.org/XML/1998/namespace"))

        assertThat(namespaces[5].namespacePrefix?.staticValue as String, `is`("xdmp"))
        assertThat(namespaces[5].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp"))

        assertThat(namespaces[6].namespacePrefix?.staticValue as String, `is`("spell"))
        assertThat(namespaces[6].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/spell"))

        assertThat(namespaces[7].namespacePrefix?.staticValue as String, `is`("sec"))
        assertThat(namespaces[7].namespaceUri?.staticValue as String, `is`("http://marklogic.com/security"))

        assertThat(namespaces[8].namespacePrefix?.staticValue as String, `is`("prop"))
        assertThat(namespaces[8].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/property"))

        assertThat(namespaces[9].namespacePrefix?.staticValue as String, `is`("prof"))
        assertThat(namespaces[9].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/profile"))

        assertThat(namespaces[10].namespacePrefix?.staticValue as String, `is`("math"))
        assertThat(namespaces[10].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/math"))

        assertThat(namespaces[11].namespacePrefix?.staticValue as String, `is`("map"))
        assertThat(namespaces[11].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/map"))

        assertThat(namespaces[12].namespacePrefix?.staticValue as String, `is`("lock"))
        assertThat(namespaces[12].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/lock"))

        assertThat(namespaces[13].namespacePrefix?.staticValue as String, `is`("local"))
        assertThat(namespaces[13].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xquery-local-functions"))

        assertThat(namespaces[14].namespacePrefix?.staticValue as String, `is`("json"))
        assertThat(namespaces[14].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/json"))

        assertThat(namespaces[15].namespacePrefix?.staticValue as String, `is`("fn"))
        assertThat(namespaces[15].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xpath-functions"))

        assertThat(namespaces[16].namespacePrefix?.staticValue as String, `is`("error"))
        assertThat(namespaces[16].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/error"))

        assertThat(namespaces[17].namespacePrefix?.staticValue as String, `is`("err"))
        assertThat(namespaces[17].namespaceUri?.staticValue as String, `is`("http://www.w3.org/2005/xqt-error"))

        assertThat(namespaces[18].namespacePrefix?.staticValue as String, `is`("dir"))
        assertThat(namespaces[18].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/directory"))

        assertThat(namespaces[19].namespacePrefix?.staticValue as String, `is`("dbg"))
        assertThat(namespaces[19].namespaceUri?.staticValue as String, `is`("http://marklogic.com/xdmp/dbg"))

        assertThat(namespaces[20].namespacePrefix?.staticValue as String, `is`("dav"))
        assertThat(namespaces[20].namespaceUri?.staticValue as String, `is`("DAV:"))

        assertThat(namespaces[21].namespacePrefix?.staticValue as String, `is`("cts"))
        assertThat(namespaces[21].namespaceUri?.staticValue as String, `is`("http://marklogic.com/cts"))
    }

    // endregion
}
