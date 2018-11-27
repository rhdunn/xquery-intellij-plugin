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
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XQueryPsiTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(XQueryPsiTest::class.java, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XQuery 3.1 (2) Basics")
    internal inner class Basics {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (217) URILiteral")
        internal inner class URILiteral {
            @Test
            @DisplayName("uri literal content")
            fun uriLiteral() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"Lorem ipsum.\uFFFF\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed uri literal content")
            fun unclosedUriLiteral() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"Lorem ipsum.")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryUriLiteral>("module namespace test = '''\"\"'")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"''\"\"\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (224) BracedURILiteral")
        internal inner class BracedURILiteral {
            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (235) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathNCName>("declare option * \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathURIQualifiedName>("declare option Q{http://www.example.com}* \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.1) Literals")
    internal inner class Literals {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.1) Direct Element Constructors")
    internal inner class DirectElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("open and close tags")
            fun openAndCloseTags() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))
            }

            @Test
            @DisplayName("self-closing")
            fun selfClosing() {
                val element = parse<XQueryDirElemConstructor>("<h:br/>")[0]
                assertThat(element.isSelfClosing, `is`(true))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("h"))
                assertThat(open.localName!!.data, `is`("br"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("missing close tag")
            fun missingClosingTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("incomplete open tag")
            fun incompleteOpenTag() {
                val element = parse<XQueryDirElemConstructor>("<a:></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName, `is`(nullValue()))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))
            }

            @Test
            @DisplayName("incomplete close tag")
            fun incompleteCloseTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    <test/>
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("<elem test=\"\"/>")[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com\uFFFF\"/>")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("'\"\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("''\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"x{\$y}z\"")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3.9.1.2) Namespace Declaration Attributes")
        internal inner class NamespaceDeclarationAttributes {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("namespace prefix containing an EnclosedExpr")
            fun namespacePrefixWithEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("default element/type namespace containing an EnclosedExpr")
            fun defaultElementTypeNamespaceEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.3.1) Computed Element Constructors")
    internal inner class ComputedElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
        internal inner class CompElemConstructor {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    element test {}
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.3.2) Computed Attribute Constructors")
    internal inner class ComputedAttributeConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        internal inner class CompAttrConstructor {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("attribute test {}")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12) FLWORExpressions")
    internal inner class FLWORExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
        internal inner class PositionalVar {
            @Test
            @DisplayName("NCName")
            fun testPositionalVar_NCName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testPositionalVar_QName() {
                val expr = parse<XQueryPositionalVar>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPositionalVar_URIQualifiedName() {
                val expr = parse<XQueryPositionalVar>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testPositionalVar_MissingVarName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.2) For Clause")
    internal inner class ForClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
        internal inner class ForBinding {
            @Test
            @DisplayName("NCName")
            fun testForBinding_NCName() {
                val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testForBinding_QName() {
                val expr = parse<XQueryForBinding>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testForBinding_URIQualifiedName() {
                val expr = parse<XQueryForBinding>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testForBinding_MissingVarName() {
                val expr = parse<XQueryForBinding>("for \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.3) Let Clause")
    internal inner class LetClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (49) LetBinding")
        internal inner class LetBinding {
            @Test
            @DisplayName("NCName")
            fun testLetBinding_NCName() {
                val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testLetBinding_QName() {
                val expr = parse<XQueryLetBinding>("let \$a:x := 2 return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testLetBinding_URIQualifiedName() {
                val expr = parse<XQueryLetBinding>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testLetBinding_MissingVarName() {
                val expr = parse<XQueryLetBinding>("let \$ := 2 return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.4) Window Clause")
    internal inner class WindowClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause")
        internal inner class TumblingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testTumblingWindowClause_NCName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testTumblingWindowClause_QName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testTumblingWindowClause_URIQualifiedName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testTumblingWindowClause_MissingVarName() {
                val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
        internal inner class SlidingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testSlidingWindowClause_NCName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testSlidingWindowClause_QName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testSlidingWindowClause_URIQualifiedName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testSlidingWindowClause_MissingVarName() {
                val expr = parse<XQuerySlidingWindowClause>("for sliding window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
        internal inner class CurrentItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testCurrentItem_NCName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$x in \$y start \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testCurrentItem_QName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCurrentItem_URIQualifiedName() {
                val expr = parse<XQueryCurrentItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
        internal inner class PreviousItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start previous \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testPreviousItem_NCName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$x in \$y start \$v previous \$w when true() return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testPreviousItem_QName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v previous \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPreviousItem_URIQualifiedName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v previous \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (58) NextItem")
        internal inner class NextItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start next \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testNextItem_NCName() {
                val expr = parse<XQueryNextItem>("for sliding window \$x in \$y start \$v next \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testNextItem_QName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v next \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testNextItem_URIQualifiedName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v next \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.6) Count Clause")
    internal inner class CountClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (59) CountClause")
        internal inner class CountClauseTest {
            @Test
            @DisplayName("NCName")
            fun testCountClause_NCName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testCountClause_QName() {
                val expr = parse<XQueryCountClause>("for \$a:x in \$a:y count \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCountClause_URIQualifiedName() {
                val expr = parse<XQueryCountClause>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCountClause_MissingVarName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.7) Group By Clause")
    internal inner class GroupByClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (63) GroupingSpec")
        internal inner class GroupingSpec {
            @Test
            @DisplayName("NCName")
            fun testGroupingSpec_NCName() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testGroupingSpec_QName() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testGroupingSpec_URIQualifiedName() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testGroupingSpec_MissingVarName() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (64) GroupingVariable")
        internal inner class GroupingVariable {
            @Test
            @DisplayName("NCName")
            fun testGroupingVariable_NCName() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testGroupingVariable_QName() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testGroupingVariable_URIQualifiedName() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testGroupingVariable_MissingVarName() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$")[0] as XPathVariableName
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
    internal inner class Typeswitch {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (75) CaseClause")
        internal inner class CaseClause {
            @Test
            @DisplayName("NCName")
            fun testCaseClause_NCName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$x) case \$y as xs:string return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testCaseClause_QName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$a:x) case \$a:y as xs:string return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCaseClause_URIQualifiedName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$Q{http://www.example.com}x) " +
                            "case \$Q{http://www.example.com}y as xs:string " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCaseClause_NoVarName() {
                val expr = parse<XQueryCaseClause>("typeswitch (\$x) case xs:string return \$z")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.22) Extension Expressions")
    internal inner class ExtensionExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("(# test #)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4) Modules and Prologs")
    internal inner class ModulesAndPrologs {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (1) Module")
        internal inner class Module {
            @Test
            @DisplayName("empty file")
            fun emptyFile() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl missing")
            fun noVersionDecl() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with version")
            fun versionDeclWithVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"1.0\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))
            }

            @Test
            @DisplayName("VersionDecl with unsupported version")
            fun versionDeclWithUnsupportedVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"9.4\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with empty version")
            fun versionDeclWithEmptyVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with missing version")
            fun versionDeclWithMissingVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryMainModule>("()")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryMainModule>("declare function local:func() {}; ()")[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("local:func"))
            }

            @Nested
            @DisplayName("variables from MainModule via QueryBody")
            internal inner class Variables {
                @Test
                @DisplayName("without Prolog")
                fun testMainModule_Variables_NoProlog() {
                    val ctx = parse<XQueryMainModule>("1")[0] as XPathVariableDeclarations

                    assertThat(ctx.variables.count(), `is`(0))
                }

                @Test
                @DisplayName("no VarDecl")
                fun testMainModule_Variables_NoVarDecl() {
                    val ctx = parse<XQueryMainModule>("declare function f() {}; 1")[0] as XPathVariableDeclarations

                    assertThat(ctx.variables.count(), `is`(0))
                }

                @Test
                @DisplayName("missing VarName")
                fun testMainModule_Variables_VarDeclWithMissingVarName() {
                    val ctx = parse<XQueryMainModule>("declare variable \$; 1")[0] as XPathVariableDeclarations

                    assertThat(ctx.variables.count(), `is`(0))
                }

                @Test
                @DisplayName("single VarDecl")
                fun testMainModule_Variables_SingleVarDecl() {
                    val ctx = parse<XQueryMainModule>("declare variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple VarDecls")
                fun testMainModule_Variables_MultipleVarDecls() {
                    val ctx = parse<XQueryMainModule>("declare variable \$x; declare variable \$y; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("public Annotation")
                fun testMainModule_Variables_PublicVarDecl() {
                    val ctx = parse<XQueryMainModule>("declare %public variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("private Annotation")
                fun testMainModule_Variables_PrivateVarDecl() {
                    val ctx = parse<XQueryMainModule>("declare %private variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    // NOTE: This variable is listed in the variable declarations for the file so that errors
                    // at the point of use can differentiate from missing and private variables, allowing
                    // things like making the variable public via refactoring.
                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (4) LibraryModule")
        internal inner class LibraryModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryLibraryModule>("module namespace test = \"http://www.example.com\";")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryLibraryModule>(
                    """
                        module namespace test = "http://www.example.com";
                        declare function test:func() {};
                    """
                )[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Nested
            @DisplayName("variables from Prolog via FunctionDecl")
            internal inner class Variables {
                @Test
                @DisplayName("no VarDecl")
                fun testProlog_Variables_NoVarDecl() {
                    val ctx = parse<XQueryProlog>("declare function f() {}; 1")[0] as XPathVariableDeclarations

                    assertThat(ctx.variables.count(), `is`(0))
                }

                @Test
                @DisplayName("missing VarName")
                fun testProlog_Variables_VarDeclWithMissingVarName() {
                    val ctx = parse<XQueryProlog>("declare variable \$; 1")[0] as XPathVariableDeclarations

                    assertThat(ctx.variables.count(), `is`(0))
                }

                @Test
                @DisplayName("single VarDecl")
                fun testProlog_Variables_SingleVarDecl() {
                    val ctx = parse<XQueryProlog>("declare variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple VarDecls")
                fun testProlog_Variables_MultipleVarDecls() {
                    val ctx = parse<XQueryProlog>("declare variable \$x; declare variable \$y; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("public Annotation")
                fun testProlog_Variables_PublicVarDecl() {
                    val ctx = parse<XQueryProlog>("declare %public variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("private Annotation")
                fun testProlog_Variables_PrivateVarDecl() {
                    val ctx = parse<XQueryProlog>("declare %private variable \$x; 1")[0] as XPathVariableDeclarations

                    val variables = ctx.variables.toList()
                    assertThat(variables.size, `is`(1))

                    // NOTE: This variable is listed in the variable declarations for the file so that errors
                    // at the point of use can differentiate from missing and private variables, allowing
                    // things like making the variable public via refactoring.
                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.1) Version Declaration")
    internal inner class VersionDecl {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (2) VersionDecl")
        internal inner class VersionDecl {
            @Test
            @DisplayName("no version, no encoding")
            fun noVersionOrEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery;")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("version, no encoding")
            fun versionOnly() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, encoding")
            fun encodingOnly() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"latin1\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }

            @Test
            @DisplayName("empty version, no encoding")
            fun emptyVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`(""))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, empty encoding")
            fun emptyEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`(""))
            }

            @Test
            @DisplayName("version, encoding")
            fun versionAndEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"latin1\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }

            @Test
            @DisplayName("version, empty encoding")
            fun emptyEncodingWithVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`(""))
            }

            @Test
            @DisplayName("comment before declaration")
            fun commentBefore() {
                val decl = parse<XQueryVersionDecl>("(: test :)\nxquery version \"1.0\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("comment as whitespace")
            fun commentAsWhitespace() {
                val decl =
                    parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.2) Module Declaration")
    internal inner class ModuleDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 [4.2] Module Declaration : [5] ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("without prolog")
            fun withoutProlog() {
                val decl = parse<XQueryModuleDecl>("module namespace test = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))

                assertThat((decl as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("with prolog")
            fun withProlog() {
                val decl = parse<XQueryModuleDecl>("""
                    module namespace test = "http://www.example.com";
                    declare function test:func() {};
                """)[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))

                val prologs = (decl as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val decl = parse<XQueryModuleDecl>("module namespace = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val decl = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.10) Decimal Format Declaration")
    internal inner class DecimalFormatDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
        internal inner class DecimalFormatDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare decimal-format test;")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.11) Schema Import")
    internal inner class SchemaImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val expr = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val expr = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("default element namespace")
            fun defaultElementNamespace() {
                val expr = parse<XQuerySchemaImport>("import schema default element namespace 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.12) Module Import")
    internal inner class ModuleImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix NCName")
            fun noNamespacePrefixNCName() {
                val import = parse<XQueryModuleImport>("import module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val import = parse<XQueryModuleImport>("import module 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri, `is`(nullValue()))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("location uris; single uri")
            fun singleLocationUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com' at 'test1.xqy';")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("test1.xqy"))
            }

            @Test
            @DisplayName("location uris; multiple uris")
            fun multipleLocationUris() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com' at 'test1.xqy' , 'test2.xqy';")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(2))
                assertThat(uris[0].data, `is`("test1.xqy"))
                assertThat(uris[1].data, `is`("test2.xqy"))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve/files/test.xq"))
                }

                @Test
                @DisplayName("parent directory")
                fun parentDirectory() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ParentDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(0))
                }

                @Test
                @DisplayName("module in relative directory")
                fun moduleInRelativeDirectory() {
                    val file = parseResource("tests/resolve/ModuleImport_URILiteral_InDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve/namespaces/ModuleDecl.xq"))
                }

                @Test
                @DisplayName("res:// file matching")
                fun resProtocol() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("res:// file missing")
                fun resProtocolMissing() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// (import namespace) file matching")
                fun httpProtocolOnNamespace() {
                    val file = parseResource("tests/resolve/files/ModuleImport_NamespaceOnly.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// (import namespace) file missing")
                fun httpProtocolOnNamespaceMissing() {
                    val file = parseResource("tests/resolve/files/ModuleImport_NamespaceOnly_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("multiple location URIs")
                fun multipleLocationUris() {
                    val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_MultipleLocationUris.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(2))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve/files/test.xq"))
                    assertThat(prologs[1].resourcePath(), endsWith("/tests/resolve/files/test2.xq"))
                }

                @Test
                @DisplayName("module root")
                fun moduleRoot() {
                    val psi = parse<XQueryModuleImport>(
                        """
                        import module "http://example.com/test" at "/resolve/files/test.xq";
                        ()
                        """
                    )[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve/files/test.xq"))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.13) Namespace Declaration")
    internal inner class NamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        internal inner class NamespaceDecl {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve/files/NamespaceDecl_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve/files/NamespaceDecl_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("res:// file matching")
                fun resProtocol() {
                    val file = parseResource("tests/resolve/files/NamespaceDecl_ResourceFile.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve/files/NamespaceDecl_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve/files/NamespaceDecl_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.14) Default Namespace Declaration")
    internal inner class DefaultNamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("default element/type namespace declaration")
            fun element() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/1999/xhtml"))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("default function namespace declaration")
            fun function() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultFunction))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace '';")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`(""))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("missing namespace")
            fun missingNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace;")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri, `is`(nullValue()))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("res:// file matching")
                fun resProtocol() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_ResourceFile.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching; ending in name")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file matching; ending in name with '#'")
                fun httpProtocolWithHash() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_HttpProtocol_Hash.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/1999/02/22-rdf-syntax-ns.xqy"))
                }

                @Test
                @DisplayName("http:// file matching; as a directory (ending in '/')")
                fun httpProtocolForDirectory() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_HttpProtocol_Directory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/saxon.sf.net/default.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve/files/DefaultNamespaceDecl_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.15) Annotations")
    internal inner class Annotations {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare function %test f() {};")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.16) Variable Declaration")
    internal inner class VariableDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
        internal inner class VarDecl {
            @Test
            @DisplayName("NCName")
            fun testVarDecl_NCName() {
                val expr = parse<XQueryVarDecl>("declare variable \$x := \$y;")[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testVarDecl_QName() {
                val expr = parse<XQueryVarDecl>("declare variable \$a:x := \$a:y;")[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testVarDecl_URIQualifiedName() {
                val expr = parse<XQueryVarDecl>(
                    "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;"
                )[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testVarDecl_MissingVarName() {
                val expr = parse<XQueryVarDecl>("declare variable \$ := \$y;")[0] as XPathVariableDeclaration
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.18) Function Declaration")
    internal inner class FunctionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("empty ParamList")
            fun emptyParamList() {
                val decl = parse<XQueryFunctionDecl>("declare function fn:true() external;")[0]
                assertThat(decl.arity, `is`(Range(0, 0)))

                val qname = decl.functionName!!
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XQueryFunctionDecl>("declare function test(\$one, \$two) external;")[0]
                assertThat(decl.arity, `is`(Range(2, 2)))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val decl = parse<XQueryFunctionDecl>("declare function :true() external;")[0]
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare function test() {};
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunction))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.19) Option Declaration")
    internal inner class OptionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (37) OptionDecl")
        internal inner class OptionDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare option test \"lorem ipsum\";")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }
}
