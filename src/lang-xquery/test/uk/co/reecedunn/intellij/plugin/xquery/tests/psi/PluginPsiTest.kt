/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginSequenceTypeList
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginUnionType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySequenceTypeUnion
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI)")
private class PluginPsiTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(PluginPsiTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.1) SequenceType Syntax")
    internal inner class SequenceTypeSyntax {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (72) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("array node")
            fun arrayNode() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( array-node ( (::) ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(PluginAnyArrayNodeTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(array-node())"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocument::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("object node")
            fun objectNode() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( object-node ( (::) ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(PluginAnyMapNodeTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(object-node())"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocument::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
        fun namedKindTest() {
            val test = parse<PluginNamedKindTest>("() instance of node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedNumberNodeTest")
        fun namedTextTest() {
            val test = parse<PluginNamedTextTest>("() instance of text ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("text(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmText::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.1) Union Type")
    internal inner class UnionType {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
        internal inner class UnionType {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare type decl = union(test);
                    """
                )[1] as XsQNameValue
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

            @Test
            @DisplayName("empty")
            fun empty() {
                val test = parse<PluginUnionType>("() instance of union ( (::) )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(0))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one")
            fun one() {
                val test = parse<PluginUnionType>("() instance of union ( xs:string )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(1))
                assertThat(op_qname_presentation(memberTypes[0]), `is`("xs:string"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union(xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("many")
            fun many() {
                val test = parse<PluginUnionType>("() instance of union ( xs:string , xs:anyURI )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(2))
                assertThat(op_qname_presentation(memberTypes[0]), `is`("xs:string"))
                assertThat(op_qname_presentation(memberTypes[1]), `is`("xs:anyURI"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union(xs:string, xs:anyURI)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (21) TypedMapTest")
        internal inner class TypedMapTest {
            @Test
            @DisplayName("union key type")
            fun unionKeyType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( union ( xs:string , xs:float ) , xs:int )")[0]
                assertThat(test.keyType?.typeName, `is`("union(xs:string, xs:float)"))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(union(xs:string, xs:float), xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.2) Tuple Type")
    internal inner class TupleType {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (23) TupleType")
        internal inner class TupleType {
            @Test
            @DisplayName("empty")
            fun empty() {
                val test = parse<PluginTupleType>("() instance of tuple ( (::) )")[0]
                assertThat(test.fields.count(), `is`(0))
                assertThat(test.isExtensible, `is`(false))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one field")
            fun one() {
                val test = parse<PluginTupleType>("() instance of tuple ( test )")[0]
                assertThat(test.fields.count(), `is`(1))
                assertThat(test.isExtensible, `is`(false))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple fields")
            fun multiple() {
                val test = parse<PluginTupleType>("() instance of tuple ( x : xs:float , y : xs:float )")[0]
                assertThat(test.fields.count(), `is`(2))
                assertThat(test.isExtensible, `is`(false))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(x: xs:float, y: xs:float)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("empty; extensible")
            fun emptyExtensible() {
                val test = parse<PluginTupleType>("() instance of tuple ( * )")[0]
                assertThat(test.fields.count(), `is`(0))
                assertThat(test.isExtensible, `is`(false))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple fields; extensible")
            fun multipleExtensible() {
                val test = parse<PluginTupleType>("() instance of tuple ( x : xs:float , y : xs:float , * )")[0]
                assertThat(test.fields.count(), `is`(2))
                assertThat(test.isExtensible, `is`(true))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(x: xs:float, y: xs:float, *)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (24) TupleField")
        internal inner class TupleField {
            @Test
            @DisplayName("required; unspecified type")
            fun nameOnlyRequired() {
                val field = parse<PluginTupleField>("() instance of tuple ( test )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as PluginTupleType
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional; unspecified type")
            fun nameOnlyOptional() {
                val field = parse<PluginTupleField>("() instance of tuple ( test ? )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as PluginTupleType
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test?)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("required; specified type")
            fun nameAndTypeRequired() {
                val field = parse<PluginTupleField>("() instance of tuple ( test : xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as PluginTupleType
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test: xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional; specified type")
            fun nameAndTypeOptional() {
                val field = parse<PluginTupleField>("() instance of tuple ( test ? : xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as PluginTupleType
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test?: xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.3) Binary Test")
    internal inner class BinaryTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
        fun binaryTest() {
            val type = parse<PluginBinaryTest>("() instance of binary ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("binary()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmBinary::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.4) Schema Kind Tests")
    internal inner class SchemaKindTests {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginAttributeDeclTest>("() instance of attribute-decl ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute-decl()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginAttributeDeclTest>("() instance of attribute-decl ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute-decl()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginAttributeDeclTest>("() instance of attribute-decl ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute-decl(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
        internal inner class ComplexTypeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginComplexTypeTest>("() instance of complex-type ( (::) )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("complex-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmComplexType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginComplexTypeTest>("() instance of complex-type ( * )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("complex-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmComplexType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginComplexTypeTest>("() instance of complex-type ( test )")[0]
                assertThat(test.schemaType?.type?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("complex-type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmComplexType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
        internal inner class ElementDeclTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginElementDeclTest>("() instance of element-decl ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element-decl()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginElementDeclTest>("() instance of element-decl ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element-decl()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginElementDeclTest>("() instance of element-decl ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element-decl(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementDecl::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
        fun schemaComponentTest() {
            val type = parse<PluginSchemaComponentTest>("() instance of schema-component ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("schema-component()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmSchemaComponent::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
        internal inner class SchemaParticleTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginSchemaParticleTest>("() instance of schema-particle ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-particle()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaParticle::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginSchemaParticleTest>("() instance of schema-particle ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-particle()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaParticle::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginSchemaParticleTest>("() instance of schema-particle ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-particle(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaParticle::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
        fun schemaRootTest() {
            val type = parse<PluginSchemaRootTest>("() instance of schema-root ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("schema-root()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmSchemaRoot::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
        internal inner class SchemaTypeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginSchemaTypeTest>("() instance of schema-type ( (::) )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginSchemaTypeTest>("() instance of schema-type ( * )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginSchemaTypeTest>("() instance of schema-type ( test )")[0]
                assertThat(test.schemaType?.type?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
        internal inner class SimpleTypeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginSimpleTypeTest>("() instance of simple-type ( (::) )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("simple-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSimpleType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginSimpleTypeTest>("() instance of simple-type ( * )")[0]
                assertThat(test.schemaType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("simple-type()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSimpleType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginSimpleTypeTest>("() instance of simple-type ( test )")[0]
                assertThat(test.schemaType?.type?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("simple-type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSimpleType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
        internal inner class SchemaFacetTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginSchemaFacetTest>("() instance of schema-facet ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-facet()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaFacet::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginSchemaFacetTest>("() instance of schema-facet ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-facet()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaFacet::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginSchemaFacetTest>("() instance of schema-facet ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-facet(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmSchemaFacet::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.5.1) Boolean Node Test")
    internal inner class BooleanNodeTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
        fun anyBooleanTest() {
            val type = parse<PluginAnyBooleanNodeTest>("() instance of boolean-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("boolean-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmBooleanNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedBooleanNodeTest")
        fun namedBooleanTest() {
            val test = parse<PluginNamedBooleanNodeTest>("() instance of boolean-node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("boolean-node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmBooleanNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.5.2) Number Node Test")
    internal inner class NumberNodeTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
        fun anyNumberTest() {
            val type = parse<PluginAnyNumberNodeTest>("() instance of number-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("number-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNumberNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
        fun namedNumberTest() {
            val test = parse<PluginNamedNumberNodeTest>("() instance of number-node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("number-node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNumberNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.5.3) Null Node Test")
    internal inner class NullNodeTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
        fun anyNullTest() {
            val type = parse<PluginAnyNullNodeTest>("() instance of null-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("null-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNullNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
        fun namedNullTest() {
            val test = parse<PluginNamedNullNodeTest>("() instance of null-node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("null-node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNullNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.5.4) Array Node Test")
    internal inner class ArrayNodeTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
        fun anyArrayTest() {
            val type = parse<PluginAnyArrayNodeTest>("() instance of array-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("array-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmArrayNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
        fun namedArrayTest() {
            val test = parse<PluginNamedArrayNodeTest>("() instance of array-node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("array-node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmArrayNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.5.5) Map Node Test")
    internal inner class MapNodeTest {
        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
        fun anyMapTest() {
            val type = parse<PluginAnyMapNodeTest>("() instance of object-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("object-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmObjectNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
        fun namedMapTest() {
            val test = parse<PluginNamedMapNodeTest>("() instance of object-node ( \"test\" )")[0]
            assertThat(test.keyName.data, `is`("test"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("object-node(\"test\")"))
            assertThat(type.typeClass, `is`(sameInstance(XdmObjectNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.6) Sequence Types")
    internal inner class SequenceTypes {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
        internal inner class SequenceTypeList {
            @Test
            @DisplayName("parenthesized")
            fun parenthesized() {
                val test = parse<PluginSequenceTypeList>(
                    "() instance of ( node ( (::) ) , xs:string , array ( * ) )"
                )[0]
                assertThat(test.isParenthesized, `is`(true))

                val type = test as XdmSequenceTypeList
                assertThat(type.typeName, `is`("(node(), xs:string, array(*))"))

                val types = type.types.toList()
                assertThat(types.size, `is`(3))
                assertThat(types[0].typeName, `is`("node()"))
                assertThat(types[1].typeName, `is`("xs:string"))
                assertThat(types[2].typeName, `is`("array(*)"))

                assertThat(type.itemType?.typeName, `is`("item()"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("not parenthesized")
            fun notParenthesized() {
                val test = parse<PluginSequenceTypeList>(
                    "() instance of ( xs:int | node ( (::) ) , xs:string , array ( * ) )"
                )[0]
                assertThat(test.isParenthesized, `is`(false))

                val type = test as XdmSequenceTypeList
                assertThat(type.typeName, `is`("node(), xs:string, array(*)"))

                val types = type.types.toList()
                assertThat(types.size, `is`(3))
                assertThat(types[0].typeName, `is`("node()"))
                assertThat(types[1].typeName, `is`("xs:string"))
                assertThat(types[2].typeName, `is`("array(*)"))

                assertThat(type.itemType?.typeName, `is`("item()"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (76) SequenceTypeUnion")
        fun sequenceTypeUnion() {
            val test = parse<XQuerySequenceTypeUnion>("() instance of ( node ( (::) ) | xs:string | array ( * ) )")[0]
            assertThat(test.isParenthesized, `is`(true))

            val type = test as XdmSequenceTypeUnion
            assertThat(type.typeName, `is`("(node() | xs:string | array(*))"))

            val types = type.types.toList()
            assertThat(types.size, `is`(3))
            assertThat(types[0].typeName, `is`("node()"))
            assertThat(types[1].typeName, `is`("xs:string"))
            assertThat(types[2].typeName, `is`("array(*)"))

            assertThat(type.itemType?.typeName, `is`("item()"))
            assertThat(type.lowerBound, `is`(0))
            assertThat(type.upperBound, `is`(Int.MAX_VALUE))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (98) EmptySequenceType")
        fun emptySequence() {
            val type = parse<XPathSequenceType>("() instance of empty ( (::) )")[0] as XdmSequenceType
            assertThat(type.typeName, `is`("empty-sequence()"))
            assertThat(type.itemType, `is`(nullValue()))
            assertThat(type.lowerBound, `is`(0))
            assertThat(type.upperBound, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.1) Node Constructors")
    internal inner class NodeConstructors {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
        internal inner class DirAttribute {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val expr = parse<PluginDirAttribute>(
                    "<a xmlns:b='http://www.example.com'/>"
                )[0] as XPathDefaultNamespaceDeclaration

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
                val expr = parse<PluginDirAttribute>(
                    "<a xmlns='http://www.example.com'/>"
                )[0] as XPathDefaultNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val expr = parse<PluginDirAttribute>(
                    "<a b='http://www.example.com'/>"
                )[0] as XPathDefaultNamespaceDeclaration

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
                    val file = parseResource("tests/resolve-xquery/files/DirAttributeList_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/DirAttributeList_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/DirAttributeList_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(
                        prologs[0].resourcePath(),
                        endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy")
                    )
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/DirAttributeList_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<PluginDirAttribute>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.3.1) Typeswitch")
    internal inner class Typeswitch {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (75) DefaultCaseClause")
        internal inner class DefaultCaseClause {
            @Test
            @DisplayName("NCName")
            fun testDefaultCaseClause_NCName() {
                val expr = parse<PluginDefaultCaseClause>(
                    "typeswitch (\$x) default \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testDefaultCaseClause_QName() {
                val expr = parse<PluginDefaultCaseClause>(
                    "typeswitch (\$a:x) default \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testDefaultCaseClause_URIQualifiedName() {
                val expr = parse<PluginDefaultCaseClause>(
                    """
                    typeswitch (${'$'}Q{http://www.example.com}x)
                    default ${'$'}Q{http://www.example.com}y
                    return ${'$'}Q{http://www.example.com}z
                    """
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testDefaultCaseClause_NoVarName() {
                val expr = parse<PluginDefaultCaseClause>(
                    "typeswitch (\$x) default return \$z"
                )[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.4) Block Expressions")
    internal inner class BlockExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (9) BlockVarDecl")
        internal inner class BlockVarDecl {
            @Test
            @DisplayName("multiple BlockVarDeclEntry nodes")
            fun testBlockVarDeclEntry_Multiple() {
                val decls = parse<PluginBlockVarDeclEntry>("block { declare \$x := 1, \$y := 2; 3 }")
                assertThat(decls.size, `is`(2))

                var qname = (decls[0] as XPathVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                qname = (decls[1] as XPathVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (10) BlockVarDeclEntry")
        internal inner class BlockVarDeclEntry {
            @Test
            @DisplayName("NCName")
            fun testBlockVarDeclEntry_NCName() {
                val expr = parse<PluginBlockVarDeclEntry>(
                    "block { declare \$x := \$y; 2 }"
                )[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testBlockVarDeclEntry_QName() {
                val expr = parse<PluginBlockVarDeclEntry>(
                    "block { declare \$a:x := \$a:y; 2 }"
                )[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testBlockVarDeclEntry_URIQualifiedName() {
                val expr = parse<PluginBlockVarDeclEntry>(
                    "block { declare \$Q{http://www.example.com}x := \$Q{http://www.example.com}y; 2 }"
                )[0] as XPathVariableDeclaration

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testBlockVarDeclEntry_MissingVarName() {
                val expr = parse<PluginBlockVarDeclEntry>(
                    "block { declare \$ := \$y; 2 }"
                )[0] as XPathVariableDeclaration
                assertThat(expr.variableName, `is`(nullValue()))
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
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("Saxon")
            fun saxon() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" := \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.9.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
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
    @DisplayName("XQuery IntelliJ Plugin (4.1) Type Declaration")
    internal inner class TypeDeclaration {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
        internal inner class TypeDecl {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginTypeDecl>("declare type test := xs:string;")[0]

                val qname = expr.typeName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
                assertThat(presentation.presentableText, `is`("test"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameResolution() {
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
