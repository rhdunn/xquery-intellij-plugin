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
import com.intellij.testFramework.LightVirtualFileBase
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

private fun PsiElement.resourcePath(): String {
    var file = containingFile.virtualFile
    if (file is LightVirtualFileBase) {
        file = file.originalFile
    }
    return file.path.replace('\\', '/')
}

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - IntelliJ Program Structure Interface (PSI)")
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
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("'\"\"{}"))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("''\"{}"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
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

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryMainModule>("()")[0]

                assertThat((module as XQueryPrologResolver).prolog, `is`(nullValue()))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryMainModule>("declare function local:func() {}; ()")[0]

                val prolog = (module as XQueryPrologResolver).prolog!!
                val name = prolog.walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("local:func"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (4) LibraryModule")
        internal inner class LibraryModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryLibraryModule>("module namespace test = \"http://www.example.com\";")[0]

                assertThat((module as XQueryPrologResolver).prolog, `is`(nullValue()))
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

                val prolog = (module as XQueryPrologResolver).prolog!!
                val name = prolog.walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
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
            val decl = parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
            assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
            assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
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

                assertThat((decl as XQueryPrologResolver).prolog, `is`(nullValue()))
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

                val prolog = (decl as XQueryPrologResolver).prolog!!
                val name = prolog.walkTree().filterIsInstance<XPathEQName>().first()
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
            }

            @Test
            @DisplayName("missing namespace prefix NCName")
            fun noNamespacePrefixNCName() {
                val import = parse<XQueryModuleImport>("import module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val import = parse<XQueryModuleImport>("import module 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri, `is`(nullValue()))
            }

            @Test
            @DisplayName("empty")
            fun empty() {
                val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq")
                val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                assertThat((psi as XQueryPrologResolver).prolog, `is`(nullValue()))
            }

            @Test
            @DisplayName("same directory")
            fun sameDirectory() {
                val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")
                val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                val prolog = (psi as XQueryPrologResolver).prolog!!
                assertThat(prolog.resourcePath(), endsWith("/tests/resolve/files/test.xq"))
            }

            @Test
            @DisplayName("parent directory")
            fun parentDirectory() {
                val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ParentDirectory.xq")
                val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                val prolog = (psi as XQueryPrologResolver).prolog!!
                assertThat(prolog.resourcePath(), endsWith("/tests/resolve/namespaces/ModuleDecl.xq"))
            }

            @Test
            @DisplayName("res:// file matching")
            fun resProtocol() {
                val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq")
                val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                val prolog = (psi as XQueryPrologResolver).prolog!!
                assertThat(prolog.resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
            }

            @Test
            @DisplayName("res:// file missing")
            fun resProtocolMissing() {
                val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFileNotFound.xq")
                val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                assertThat((psi as XQueryPrologResolver).prolog, `is`(nullValue()))
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
                assertThat(decl.arity, `is`(0))

                val qname = decl.functionName!!
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XQueryFunctionDecl>("declare function test(\$one, \$two) external;")[0]
                assertThat(decl.arity, `is`(2))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val decl = parse<XQueryFunctionDecl>("declare function :true() external;")[0]
                assertThat(decl.arity, `is`(0))
                assertThat(decl.functionName, `is`(nullValue()))
            }
        }
    }
}
