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
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI)")
private class PluginPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.1) Node Constructors")
    internal inner class NodeConstructors {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
        internal inner class DirAttribute {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val expr = parse<PluginDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XPathDefaultNamespaceDeclaration

                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("namespace prefix, missing DirAttributeValue")
            fun namespacePrefixMissingDirAttributeValue() {
                val expr = parse<PluginDirAttribute>("<a xmlns:b=>")[0] as XPathDefaultNamespaceDeclaration

                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val expr = parse<PluginDirAttribute>("<a xmlns='http://www.example.com'/>")[0] as XPathDefaultNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val expr = parse<PluginDirAttribute>("<a b='http://www.example.com'/>")[0] as XPathDefaultNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri, `is`(nullValue()))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Undefined))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve/files/DirAttributeList_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve/files/DirAttributeList_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("res:// file matching")
                fun resProtocol() {
                    val file = parseResource("tests/resolve/files/DirAttributeList_ResourceFile.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve/files/DirAttributeList_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve/files/DirAttributeList_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.8.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("MarkLogic")
            fun markLogic() {
                val entry = parse<XPathMapConstructorEntry>("object-node { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XQueryTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("Saxon")
            fun saxon() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" := \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.9.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin BNF (25) ForwardAxis")
        internal inner class ForwardAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("property::one")
                assertThat(steps.size, `is`(1))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // property
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (1.1) Type Declaration")
    internal inner class TypeDeclaration {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin BNF (19) TypeDecl")
        internal inner class TypeDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare type test = xs:string;
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
}
