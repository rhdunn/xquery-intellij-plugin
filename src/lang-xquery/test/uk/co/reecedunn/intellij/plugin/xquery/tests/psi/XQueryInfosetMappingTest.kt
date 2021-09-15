/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompAttrConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery Data Model 3.1 (4) Infoset Mapping - XQuery Node Constructors")
class XQueryInfosetMappingTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryInfosetMappingTest")

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        internal inner class Attributes {
            @Test
            @DisplayName("empty")
            fun empty() {
                val node = parse<XQueryDirElemConstructor>("<a/>")[0] as XdmElementNode

                assertThat(node.attributes.count(), `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
            fun dirAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test one='1' n:two='2' xmlns:n='urn:number' xmlns='urn:test'/>"
                )[0]
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("n:two"))

                assertThat((attributes[0].typedValue as? XsUntypedAtomicValue)?.data, `is`("1"))
                assertThat((attributes[1].typedValue as? XsUntypedAtomicValue)?.data, `is`("2"))

                assertThat(attributes.size, `is`(2))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; single attribute")
            fun compAttrConstructor_singleAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } }</test>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(1))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; multiple attributes")
            fun compAttrConstructor_multipleAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } , attribute n:two { 2 } }</test>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("n:two"))

                assertThat(attributes[0].typedValue, `is`(nullValue()))
                assertThat(attributes[1].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(2))
            }

            @Test
            @DisplayName("with direct and constructed attributes")
            fun withDirectAndConstructedAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<a one='1' two='2'>{attribute three{'3'}, attribute four{'4'}}</a>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("two"))
                assertThat(qname_presentation(attributes[2].nodeName!!), `is`("three"))
                assertThat(qname_presentation(attributes[3].nodeName!!), `is`("four"))

                assertThat((attributes[0].typedValue as? XsUntypedAtomicValue)?.data, `is`("1"))
                assertThat((attributes[1].typedValue as? XsUntypedAtomicValue)?.data, `is`("2"))
                assertThat(attributes[2].typedValue, `is`(nullValue()))
                assertThat(attributes[3].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(4))
            }
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryDirElemConstructor>("<test/>")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryDirElemConstructor>("<t:test xmlns:t='urn:test'/>")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    inner class DirAttribute {
        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<PluginDirAttribute>("<a test='value'/>")[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<PluginDirAttribute>("<a xmlns:t='urn:test' t:test='value'/>")[1] as XdmAttributeNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }
        }

        @Nested
        @DisplayName("Accessors (5.14) typed-value")
        internal inner class TypedValue {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val node = parse<PluginDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(CoreMatchers.sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val node = parse<PluginDirAttribute>("<a xmlns='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(CoreMatchers.sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("xml:id")
            fun id() {
                val node = parse<PluginDirAttribute>("<a xml:id='lorem-ipsum'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsIDValue
                assertThat(value.data, `is`("lorem-ipsum"))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val node = parse<PluginDirAttribute>("<a b='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.element, `is`(node as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
    inner class DirAttributeValue {
        @Nested
        @DisplayName("Accessors (5.14) typed-value")
        internal inner class TypedValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val node = parse<PluginDirAttribute>(
                    "<a b=\"http://www.example.com\uFFFF\"/>"
                )[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val node = parse<PluginDirAttribute>("<a b=\"http://www.example.com")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val node = parse<PluginDirAttribute>("<a b='''\"\"{{}}'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("'\"\"{}"))
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val node = parse<PluginDirAttribute>("<a b=\"''\"\"{{}}\"/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("''\"{}"))
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val node = parse<PluginDirAttribute>(
                    "<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"/>"
                )[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val node = parse<PluginDirAttribute>("<a b=\"&#xA0;&#160;&#x20;&#x1D520;\"/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
                assertThat(value.element, CoreMatchers.sameInstance(node as PsiElement))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val node = parse<PluginDirAttribute>("<a b=\"x{\$y}z\"/>")[0] as XdmAttributeNode
                assertThat(node.typedValue, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        internal inner class Attributes {
            @Test
            @DisplayName("without attributes")
            fun withoutAttributes() {
                val element = parse<XQueryCompElemConstructor>("element a {}")[0] as XdmElementNode
                assertThat(element.attributes.count(), `is`(0))
            }

            @Test
            @DisplayName("with single constructed attribute")
            fun withConstructedAttribute() {
                val element = parse<XQueryCompElemConstructor>("element a { attribute one{'1'} }")[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(1))
            }

            @Test
            @DisplayName("with multiple constructed attributes")
            fun withConstructedAttributes() {
                val element = parse<XQueryCompElemConstructor>(
                    "element a { attribute one{'1'}, attribute two{'2'} }"
                )[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("two"))
                assertThat(attributes[1].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(2))
            }
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompElemConstructor>("element test {}")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompElemConstructor>(
                    "declare namespace t = 'urn:test'; element t:test {}"
                )[0] as XdmElementNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompElemConstructor>("element Q{urn:test}test {}")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace?.data, `is`("urn:test"))
                assertThat(node.nodeName?.isLexicalQName, `is`(false))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompElemConstructor>(
                    "element { if (true()) 'test' else () } {}"
                )[0] as XdmElementNode

                assertThat(node.nodeName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
    inner class CompAttrConstructor {
        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute test { 'valid' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompAttrConstructor>(
                    "declare namespace t = 'urn:test'; element a { attribute t:test { 'value' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute Q{urn:test}test { 'value' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace?.data, `is`("urn:test"))
                assertThat(node.nodeName?.isLexicalQName, `is`(false))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute { if (true()) 'test' else () } {} }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName, `is`(nullValue()))
            }
        }
    }
}
