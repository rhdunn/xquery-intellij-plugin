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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsStringValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1")
private class XQueryPsiTest : ParserTestCase() {
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
            }

            @Test
            @DisplayName("unclosed uri literal content")
            fun unclosedUriLiteral() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"Lorem ipsum.")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryUriLiteral>("module namespace test = '''\"\"'")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("'\"\""))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"''\"\"\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("''\""))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
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
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
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
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
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
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl missing")
            fun noVersionDecl() {
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with version")
            fun versionDeclWithVersion() {
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("xquery version \"1.0\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(XQuery.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_1_0_20070123))
            }

            @Test
            @DisplayName("VersionDecl with unsupported version")
            fun versionDeclWithUnsupportedVersion() {
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("xquery version \"9.4\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with empty version")
            fun versionDeclWithEmptyVersion() {
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("xquery version \"\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with missing version")
            fun versionDeclWithMissingVersion() {
                settings.XQueryVersion = XQuery.REC_3_0_20140408.label

                val file = parse<XQueryModule>("xquery; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_0_20140408))

                settings.XQueryVersion = XQuery.REC_3_1_20170321.label

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuery.REC_3_1_20170321))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.1) Version Declaration : EBNF (2) VersionDecl")
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
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
            assertThat(decl.encoding, `is`(nullValue()))
        }

        @Test
        @DisplayName("no version, encoding")
        fun encodingOnly() {
            val decl = parse<XQueryVersionDecl>("xquery encoding \"latin1\";")[0]
            assertThat(decl.version, `is`(nullValue()))
            assertThat((decl.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))
        }

        @Test
        @DisplayName("empty version, no encoding")
        fun emptyVersion() {
            val decl = parse<XQueryVersionDecl>("xquery version \"\";")[0]
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`(""))
            assertThat(decl.encoding, `is`(nullValue()))
        }

        @Test
        @DisplayName("no version, empty encoding")
        fun emptyEncoding() {
            val decl = parse<XQueryVersionDecl>("xquery encoding \"\";")[0]
            assertThat(decl.version, `is`(nullValue()))
            assertThat((decl.encoding!! as XdmStaticValue).staticValue as String, `is`(""))
        }

        @Test
        @DisplayName("version, encoding")
        fun versionAndEncoding() {
            val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"latin1\";")[0]
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
            assertThat((decl.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))
        }

        @Test
        @DisplayName("version, empty encoding")
        fun emptyEncodingWithVersion() {
            val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"\";")[0]
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
            assertThat((decl.encoding!! as XdmStaticValue).staticValue as String, `is`(""))
        }

        @Test
        @DisplayName("comment before declaration")
        fun commentBefore() {
            val decl = parse<XQueryVersionDecl>("(: test :)\nxquery version \"1.0\";")[0]
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
            assertThat(decl.encoding, `is`(nullValue()))
        }

        @Test
        @DisplayName("comment as whitespace")
        fun commentAsWhitespace() {
            val decl = parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
            assertThat((decl.version!! as XdmStaticValue).staticValue as String, `is`("1.0"))
            assertThat((decl.encoding!! as XdmStaticValue).staticValue as String, `is`("latin1"))
        }
    }

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
    // region Type :: Function :: FunctionCall

    @Test
    fun testEQNameType_FunctionCall_NCName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_NCName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_FunctionCall_QName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_QName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_FunctionCall_EQName() {
        val file = parseResource("tests/resolve/functions/FunctionCall_EQName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    // endregion
    // region Type :: Function :: NamedFunctionRef

    @Test
    fun testEQNameType_NamedFunctionRef_NCName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_NCName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_NamedFunctionRef_QName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_QName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_NamedFunctionRef_EQName() {
        val file = parseResource("tests/resolve/functions/NamedFunctionRef_EQName.xq")
        val name = file.descendants().filterIsInstance<XPathEQName>().first()

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

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_ArrowFunctionSpecifier_QName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_QName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Function))
    }

    @Test
    fun testEQNameType_ArrowFunctionSpecifier_EQName() {
        val file = parseResource("tests/resolve/functions/ArrowFunctionSpecifier_EQName.xq")

        val arrowExprPsi = file.descendants().filterIsInstance<XPathArrowExpr>().first()
        val arrowFunctionSpecifierPsi = arrowExprPsi.children().filterIsInstance<XPathArrowFunctionSpecifier>().first()
        val name = arrowFunctionSpecifierPsi.children().filterIsInstance<XPathEQName>().first()

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

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    @Test
    fun testEQNameType_VarDecl_QName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_QName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclQName = varDeclPsi.children().filterIsInstance<XPathVarName>().first()
        val name = varDeclQName.children().filterIsInstance<XPathEQName>().first()

        assertThat(name.type, `is`(XPathEQName.Type.Variable))
    }

    @Test
    fun testEQNameType_VarDecl_EQName() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_EQName.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclQName = varDeclPsi.children().filterIsInstance<XPathVarName>().first()
        val name = varDeclQName.children().filterIsInstance<XPathEQName>().first()

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
    // region XPathFunctionCall

    @Test
    fun testFunctionCall() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(2))

        val qname = (functionCallPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix?.staticValue, `is`("math"))
        assertThat(qname?.localName?.staticValue, `is`("pow"))
    }

    @Test
    fun testFunctionCall_Empty() {
        val file = parseResource("tests/parser/xquery-1.0/FunctionCall.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(0))

        val qname = (functionCallPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix?.staticValue, `is`("fn"))
        assertThat(qname?.localName?.staticValue, `is`("true"))
    }

    @Test
    fun testFunctionCall_ArgumentPlaceholder() {
        val file = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")

        val functionCallPsi = file.descendants().filterIsInstance<XPathFunctionCall>().first()
        assertThat(functionCallPsi, `is`(notNullValue()))
        assertThat(functionCallPsi.arity, `is`(1))

        val qname = (functionCallPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix?.staticValue, `is`("math"))
        assertThat(qname?.localName?.staticValue, `is`("sin"))
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

        val qname = (functionDeclPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix?.staticValue, `is`("fn"))
        assertThat(qname?.localName?.staticValue, `is`("true"))
    }

    @Test
    fun testFunctionDecl_ParamList() {
        val file = parseResource("tests/parser/xquery-1.0/ParamList.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))
        assertThat(functionDeclPsi.arity, `is`(2))

        val qname = (functionDeclPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix, `is`(nullValue()))
        assertThat(qname?.localName?.staticValue, `is`("test"))
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

        val qname = (namedFunctionRefPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix, `is`(nullValue()))
        assertThat(qname?.localName?.staticValue, `is`("true"))
    }

    @Test
    fun testNamedFunctionRef_MissingArity() {
        val file = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")

        val namedFunctionRefPsi = file.descendants().filterIsInstance<XPathNamedFunctionRef>().first()
        assertThat(namedFunctionRefPsi, `is`(notNullValue()))
        assertThat(namedFunctionRefPsi.arity, `is`(0))

        val qname = (namedFunctionRefPsi.functionName as? XdmStaticValue)?.staticValue as? QName
        assertThat(qname?.prefix, `is`(nullValue()))
        assertThat(qname?.localName?.staticValue, `is`("true"))
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
}
