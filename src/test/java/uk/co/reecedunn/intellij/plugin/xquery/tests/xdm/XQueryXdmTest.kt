/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.xdm

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.XsString
import uk.co.reecedunn.intellij.plugin.xdm.model.QNameContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryXdmTest : ParserTestCase() {
    private inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().toList()
    }

    private inline fun <reified T> parseLiteral(xquery: String): XdmLexicalValue {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .first() as XdmLexicalValue
    }

    // region Lexical Values
    // region BracedUriLiteral (XdmLexicalValue)

    fun testBracedUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪̸&;&gt"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testBracedUriLiteral_CharRef() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region StringLiteral (XdmLexicalValue)

    fun testStringLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_CharRef() {
        val literal = parseLiteral<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region UriLiteral (XdmLexicalValue)

    fun testUriLiteral() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com\uFFFF\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_Unclosed() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_EscapeApos() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = '''\"\"'")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_EscapeQuot() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"''\"\"\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testUriLiteral_CharRef() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Static Context :: Default Namespace
    // region MainModule :: DefaultNamespaceDecl

    fun testMainModule_NoProlog() {
        val ctx = parse<XQueryMainModule>("<br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testMainModule_NoDefaultNamespaceDecl() {
        val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testMainModule_DefaultNamespaceDecl_Element() {
        val ctx = parse<XQueryMainModule>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Element)?.lexicalRepresentation, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(ctx.defaultNamespace(QNameContext.Element)?.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))

        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type)?.lexicalRepresentation, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(ctx.defaultNamespace(QNameContext.Type)?.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testMainModule_DefaultNamespaceDecl_Element_EmptyNamespace() {
        val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testMainModule_DefaultNamespaceDecl_Function() {
        val ctx = parse<XQueryMainModule>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))

        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function)?.lexicalRepresentation, `is`("http://www.w3.org/2005/xpath-functions/math"))
        assertThat(ctx.defaultNamespace(QNameContext.Function)?.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testMainModule_DefaultNamespaceDecl_Function_EmptyNamespace() {
        val ctx = parse<XQueryMainModule>("declare default function namespace ''; pi()")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    // endregion
    // region Prolog :: DefaultNamespaceDecl

    fun testProlog_NoDefaultNamespaceDecl() {
        val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testProlog_DefaultNamespaceDecl_Element() {
        val ctx = parse<XQueryProlog>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Element)?.lexicalRepresentation, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(ctx.defaultNamespace(QNameContext.Element)?.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))

        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type)?.lexicalRepresentation, `is`("http://www.w3.org/1999/xhtml"))
        assertThat(ctx.defaultNamespace(QNameContext.Type)?.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testProlog_DefaultNamespaceDecl_Element_EmptyNamespace() {
        val ctx = parse<XQueryProlog>("declare default element namespace ''; <br/>")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testProlog_DefaultNamespaceDecl_Function() {
        val ctx = parse<XQueryProlog>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))

        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(notNullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function)?.lexicalRepresentation, `is`("http://www.w3.org/2005/xpath-functions/math"))
        assertThat(ctx.defaultNamespace(QNameContext.Function)?.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    fun testProlog_DefaultNamespaceDecl_Function_EmptyNamespace() {
        val ctx = parse<XQueryProlog>("declare default function namespace ''; pi()")[0] as XdmStaticContext

        assertThat(ctx.defaultNamespace(QNameContext.Element), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Function), `is`(nullValue()))
        assertThat(ctx.defaultNamespace(QNameContext.Type), `is`(nullValue()))
    }

    // endregion
    // endregion
}
