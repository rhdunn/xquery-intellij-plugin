/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xquery.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmPathStep
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCatchClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySequenceTypeUnion
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XQuery")
private class PluginPsiTest : ParserTestCase()  {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XQueryModule
    }

    override fun registerExtensions() {
        registerNamespaceProvider(XQueryNamespaceProvider, "INSTANCE")
        registerStaticallyKnownFunctionProvider(XQueryFunctionProvider, "INSTANCE")
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
                assertThat(type.typeClass, `is`(sameInstance(XdmDocumentNode::class.java)))

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
                assertThat(type.typeClass, `is`(sameInstance(XdmDocumentNode::class.java)))

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
            assertThat(type.typeClass, `is`(sameInstance(XdmTextNode::class.java)))

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
                assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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

            @Test
            @DisplayName("StringLiteral name; no space in name")
            fun stringLiteralName_noSpace() {
                val field = parse<PluginTupleField>("() instance of tuple ( 'test' )")[0]
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
            @DisplayName("StringLiteral name; space in name")
            fun stringLiteralName_withSpace() {
                val field = parse<PluginTupleField>("() instance of tuple ( 'test key name' )")[0]
                assertThat(field.fieldName.data, `is`("test key name"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as PluginTupleType
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(\"test key name\")"))
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

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (103) SchemaWildcardTest")
        fun schemaWildcardTest() {
            val type = parse<PluginSchemaWildcardTest>("() instance of schema-wildcard ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("schema-wildcard()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmSchemaWildcard::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (104) ModelGroupTest")
        internal inner class ModelGroupTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<PluginModelGroupTest>("() instance of model-group ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("model-group()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmModelGroup::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<PluginModelGroupTest>("() instance of model-group ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("model-group()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmModelGroup::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name")
            fun name() {
                val test = parse<PluginModelGroupTest>("() instance of model-group ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("model-group(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmModelGroup::class.java)))

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
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.7) Element Test")
    internal inner class ElementTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (111) ElementNameOrWildcard")
        internal inner class ElementNameOrWildcard {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val test = parse<XPathElementTest>("() instance of element ( *:test )")[0]
                assertThat(op_qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.8) Attribute Test")
    internal inner class AttributeTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (112) AttribNameOrWildcard")
        internal inner class AttribNameOrWildcard {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( *:test )")[0]
                assertThat(op_qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.9) Type Alias")
    internal inner class TypeAlias {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of ~ test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
            @DisplayName("item type; Saxon 9.8")
            fun itemType_saxon9() {
                val test = parse<PluginTypeAlias>("() instance of ~ test")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("item type; Saxon 10.0")
            fun itemType_saxon10() {
                val test = parse<PluginTypeAlias>("() instance of type ( test )")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
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
                )[0] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(expr.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix?.data, `is`("xmlns"))
                assertThat(node.nodeName?.localName?.data, `is`("b"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(expr as PsiElement))
            }

            @Test
            @DisplayName("namespace prefix, missing DirAttributeValue")
            fun namespacePrefixMissingDirAttributeValue() {
                val expr = parse<PluginDirAttribute>("<a xmlns:b=>")[0] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri, `is`(nullValue()))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix?.data, `is`("xmlns"))
                assertThat(node.nodeName?.localName?.data, `is`("b"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                assertThat(node.typedValue, `is`(nullValue()))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val expr = parse<PluginDirAttribute>(
                    "<a xmlns='http://www.example.com'/>"
                )[0] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(expr.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("xmlns"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(expr as PsiElement))
            }

            @Test
            @DisplayName("xml:id")
            fun id() {
                val expr = parse<PluginDirAttribute>("<a xml:id='lorem-ipsum'/>")[0] as XpmNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri, `is`(nullValue()))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix?.data, `is`("xml"))
                assertThat(node.nodeName?.localName?.data, `is`("id"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                val value = node.typedValue as XsIDValue
                assertThat(value.data, `is`("lorem-ipsum"))
                assertThat(value.element, `is`(expr as PsiElement))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val expr = parse<PluginDirAttribute>(
                    "<a b='http://www.example.com'/>"
                )[0] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri, `is`(nullValue()))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("b"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.element, `is`(expr as PsiElement))
            }

            @Test
            @DisplayName("attribute with an xmlns local name and a prefix")
            fun attributeWithXmlnsLocalName() {
                val expr = parse<PluginDirAttribute>(
                    "<a b:xmlns='http://www.example.com'/>"
                )[0] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri, `is`(nullValue()))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(true))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName?.prefix?.data, `is`("b"))
                assertThat(node.nodeName?.localName?.data, `is`("xmlns"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.element, `is`(expr as PsiElement))
            }

            @Test
            @DisplayName("non-namespace declaration attribute; missing QName prefix")
            fun missingPrefix() {
                val expr = parse<PluginDirAttribute>(
                    "<a one='http://www.example.com/one' :two='http://www.example.com/two'/>"
                )[1] as XpmNamespaceDeclaration

                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri, `is`(nullValue()))

                assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))

                val node = expr as XdmAttributeNode
                assertThat(node.nodeName, `is`(nullValue()))
                assertThat(node.typedValue, `is`(nullValue()))
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
                        endsWith("/org/w3/www/2005/xpath-functions/array.xqy")
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
        @DisplayName("XQuery IntelliJ Plugin EBNF (6) DefaultCaseClause")
        internal inner class DefaultCaseClause {
            @Test
            @DisplayName("NCName")
            fun testDefaultCaseClause_NCName() {
                val expr = parse<PluginDefaultCaseClause>(
                    "typeswitch (\$x) default \$y return \$z"
                )[0] as XpmVariableBinding

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
                )[0] as XpmVariableBinding

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
                )[0] as XpmVariableBinding

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
                )[0] as XpmVariableBinding
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

                var qname = (decls[0] as XpmVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                qname = (decls[1] as XpmVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }
        }

        @Nested
        @DisplayName("XQuery Scripting Extensions 1.0 EBNF (156) BlockVarDecl")
        internal inner class BlockVarDeclEntry {
            @Test
            @DisplayName("NCName")
            fun testBlockVarDeclEntry_NCName() {
                val expr = parse<PluginBlockVarDeclEntry>(
                    "block { declare \$x := \$y; 2 }"
                )[0] as XpmVariableDeclaration

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
                )[0] as XpmVariableDeclaration

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
                )[0] as XpmVariableDeclaration

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
                )[0] as XpmVariableDeclaration
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.5) Update Expressions")
    internal inner class UpdateExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (12) UpdateExpr")
        internal inner class UpdateExpr {
            @Test
            @DisplayName("enclosed expression")
            fun enclosedExpr() {
                val expr = parse<PluginUpdateExpr>("() update { () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryTokenType.K_UPDATE))
                assertThat(expr.expressionElement?.textOffset, `is`(3))
            }

            @Test
            @DisplayName("expression")
            fun expr() {
                val expr = parse<PluginUpdateExpr>("() update ()")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryTokenType.K_UPDATE))
                assertThat(expr.expressionElement?.textOffset, `is`(3))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.7) Primary Expressions")
    internal inner class PrimaryExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin (3.7.1) Non-Deterministic Function Calls")
        internal inner class NonDeterministicFunctionCalls {
            @Test
            @DisplayName("XQuery IntelliJ Plugin EBNF (16) NonDeterministicFunctionCall")
            fun nonDeterministicFunctionCall() {
                val expr = parse<PluginNonDeterministicFunctionCall>("non-deterministic \$x()")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                assertThat(expr.expressionElement?.textOffset, `is`(20))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin (3.7.2) Inline Function Expressions")
        internal inner class InlineFunctionExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr ; XQuery IntelliJ Plugin EBNF (95) ParamList")
            internal inner class InlineFunctionExpr {
                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val decl = parse<XpmFunctionDeclaration>("function (\$one, \$two ...) {}")[0]
                    assertThat(decl.functionName, `is`(nullValue()))
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.arity, `is`(Range(1, Int.MAX_VALUE)))
                    assertThat(decl.isVariadic, `is`(true))

                    assertThat(decl.params.size, `is`(2))
                    assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                    assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
                }
            }

            @Test
            @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (81) ContextItemFunctionExpr")
            fun contextItemFunctionExpr() {
                val expr = parse<PluginContextItemFunctionExpr>(".{ () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }

            @Test
            @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (117) LambdaFunctionExpr")
            fun lambdaFunctionExpr() {
                val expr = parse<PluginLambdaFunctionExpr>("_{ $1 }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.LAMBDA_FUNCTION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.8) JSON Constructors")
    internal inner class JSONConstructors {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin (3.8.1) Maps")
        internal inner class Maps {
            @Nested
            @DisplayName("XQuery IntelliJ Plugin EBNF (66) MapConstructor")
            internal inner class MapConstructor {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val expr = parse<XPathMapConstructor>("object-node {}")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("with entry")
                fun withEntry() {
                    val expr = parse<XPathMapConstructor>("object-node { \"1\" : \"one\" }")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR_ENTRY))
                    assertThat(expr.expressionElement?.textOffset, `is`(14))
                }
            }

            @Nested
            @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
            internal inner class MapConstructorEntry {
                @Test
                @DisplayName("MarkLogic")
                fun markLogic() {
                    val entry = parse<XPathMapConstructorEntry>("object-node { \"1\" : \"one\" }")[0]
                    assertThat(entry.separator.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
                }

                @Test
                @DisplayName("Saxon")
                fun saxon() {
                    val entry = parse<XPathMapConstructorEntry>("map { \"1\" := \"one\" }")[0]
                    assertThat(entry.separator.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                }
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (50) BooleanConstructor")
        fun booleanConstructor() {
            val expr = parse<PluginBooleanConstructor>("boolean-node { true() }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.BOOLEAN_CONSTRUCTOR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (54) NumberConstructor")
        fun numberConstructor() {
            val expr = parse<PluginNumberConstructor>("number-node { 1 }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.NUMBER_CONSTRUCTOR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (58) NullConstructor")
        fun nullConstructor() {
            val expr = parse<PluginNullConstructor>("null-node {}")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.NULL_CONSTRUCTOR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (62) ArrayConstructor")
        internal inner class CurlyArrayConstructor {
            @Test
            @DisplayName("empty")
            fun empty() {
                val expr = parse<XPathCurlyArrayConstructor>("array-node {}")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }

            @Test
            @DisplayName("with members")
            fun withMembers() {
                val expr = parse<XPathCurlyArrayConstructor>("array-node { 1, 2, 3 }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
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
            @DisplayName("namespace axis")
            fun namespaceAxis() {
                val step = parse<XPathForwardStep>("namespace::test")[0] as XpmPathStep
                assertThat(step.axisType, `is`(XpmAxisType.Namespace))
                assertThat(step.nodeType, sameInstance(XdmNamespaceItem))
            }

            @Test
            @DisplayName("property axis")
            fun propertyAxis() {
                val step = parse<XPathForwardStep>("property::test")[0] as XpmPathStep
                assertThat(step.axisType, `is`(XpmAxisType.Property))
                assertThat(step.nodeType, sameInstance(XdmElementItem))
            }

            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNameTest>("property::one")
                assertThat(steps.size, `is`(1))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // property
            }

            @Test
            @DisplayName("usage type")
            fun usageType() {
                val steps = parse<XPathNameTest>("property::one").map {
                    it.walkTree().filterIsInstance<XsQNameValue>().first().element!!
                }
                assertThat(steps.size, `is`(1))
                assertThat(steps[0].getUsageType(), `is`(XpmUsageType.Element)) // property
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.12) Binary Constructors")
    internal inner class TryCatchExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
        internal inner class CatchClause {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XQueryCatchClause>("try { () } catch (\$x) { \$y }")[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XQueryCatchClause>("try { () } catch (\$a:x) { \$a:y }")[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XQueryCatchClause>(
                    "try { () } catch (\$Q{http://www.example.com}x) { \$Q{http://www.example.com}y }"
                )[0] as XpmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun noVarName() {
                val expr = parse<XQueryCatchClause>("try { () } catch () { \$x }")[0] as XpmVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.12) Binary Constructors")
    internal inner class BinaryConstructors {
        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (30) BinaryConstructor")
        fun binaryConstructor() {
            val expr = parse<PluginBinaryConstructor>("binary { \"AF\" }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.BINARY_CONSTRUCTOR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.13) Logical Expressions")
    internal inner class LogicalExpressions {
        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (19) OrExpr")
        fun orExpr() {
            val expr = parse<XPathOrExpr>("1 orElse 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_ORELSE))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (11) AndExpr")
        fun andExpr() {
            val expr = parse<XPathAndExpr>("1 andAlso 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_ANDALSO))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.14) Conditional Expressions")
    internal inner class ConditionalExpressions {
        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (92) TernaryIfExpr")
        fun ternaryIfExpr() {
            val expr = parse<PluginTernaryIfExpr>("true() ?? 1 !! 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.TERNARY_IF))
            assertThat(expr.expressionElement?.textOffset, `is`(7))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (93) ElvisExpr")
        fun elvisExpr() {
            val expr = parse<PluginElvisExpr>("1 ?: 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ELVIS))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (114) OtherwiseExpr")
        fun otherwiseExpr() {
            val expr = parse<PluginOtherwiseExpr>("1 otherwise 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OTHERWISE))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.16) Postfix Expressions")
    internal inner class PostfixExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (129) DynamicFunctionCall")
        internal inner class DynamicFunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
            fun namedFunctionRef() {
                val f = parse<PluginDynamicFunctionCall>("fn:abs#1(1)")[0]

                val ref = f.functionReference
                assertThat(op_qname_presentation(ref?.functionName!!), `is`("fn:abs"))
                assertThat(ref.arity, `is`(1))

                val args = f.children().filterIsInstance<XPathArgumentList>().first()
                assertThat(args.arity, `is`(1))
                assertThat(args.functionReference, `is`(sameInstance(ref)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(1))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("1"))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr ; XQuery 3.1 EBNF (168) NamedFunctionRef")
            internal inner class ParenthesizedExprWithNamedFunctionRef {
                @Test
                @DisplayName("single")
                fun single() {
                    val f = parse<PluginDynamicFunctionCall>("(fn:abs#1)(1)")[0]

                    val ref = f.functionReference
                    assertThat(op_qname_presentation(ref?.functionName!!), `is`("fn:abs"))
                    assertThat(ref.arity, `is`(1))

                    val args = f.children().filterIsInstance<XPathArgumentList>().first()
                    assertThat(args.arity, `is`(1))
                    assertThat(args.functionReference, `is`(sameInstance(ref)))

                    val bindings = args.bindings
                    assertThat(bindings.size, `is`(1))

                    assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg"))
                    assertThat(bindings[0].size, `is`(1))
                    assertThat(bindings[0][0].text, `is`("1"))
                }

                @Test
                @DisplayName("multiple")
                fun multiple() {
                    val f = parse<PluginDynamicFunctionCall>("(fn:abs#1, fn:count#1)(1)")[0]
                    assertThat(f.functionReference, `is`(nullValue()))

                    val args = f.children().filterIsInstance<XPathArgumentList>().first()
                    assertThat(args.arity, `is`(1))
                    assertThat(args.functionReference, `is`(nullValue()))
                    assertThat(args.bindings.size, `is`(0))
                }
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
                assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
    @DisplayName("XQuery IntelliJ Plugin (4.2) Annotations")
    internal inner class Annotations {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
        internal inner class CompatibilityAnnotation {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<PluginCompatibilityAnnotation>("declare private function f() {};")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.XQuery))
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Annotation))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("private"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("private"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("MarkLogic")
            fun markLogic() {
                val annotation = parse<PluginCompatibilityAnnotation>(
                    "declare private function f() {};"
                )[0] as XdmAnnotation
                assertThat(op_qname_presentation(annotation.name!!), `is`("private"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("private"))
            }

            @Test
            @DisplayName("XQuery Update Facility 3.0")
            fun updateFacility() {
                val annotation = parse<PluginCompatibilityAnnotation>(
                    "declare updating function f() {};"
                )[0] as XdmAnnotation
                assertThat(op_qname_presentation(annotation.name!!), `is`("updating"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("updating"))
            }

            @Test
            @DisplayName("Scripting Extension 1.0")
            fun scriptingExtension() {
                val annotation = parse<PluginCompatibilityAnnotation>(
                    "declare sequential function f() {};"
                )[0] as XdmAnnotation
                assertThat(op_qname_presentation(annotation.name!!), `is`("sequential"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("sequential"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (4.3) Stylesheet Import")
    internal inner class StylesheetImport {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
        internal inner class StylesheetImport {
            @Test
            @DisplayName("location uris; single uri")
            fun singleLocationUri() {
                val import = parse<PluginStylesheetImport>("import stylesheet at;")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("missing location uri")
            fun missingLocationUri() {
                val import = parse<PluginStylesheetImport>("import stylesheet at 'test.xsl';")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("test.xsl"))
                assertThat(uris[0].context, `is`(XdmUriContext.Location))
                assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.STYLESHEET)))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (4.5) Function Declaration")
    internal inner class FunctionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl ; XQuery IntelliJ Plugin EBNF (95) ParamList")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("variadic")
            fun variadic() {
                val decl = parse<XpmFunctionDeclaration>("declare function test(\$one, \$two ...) external;")[0]
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(1, Int.MAX_VALUE)))
                assertThat(decl.isVariadic, `is`(true))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigatablePsiElement).presentation!! as ItemPresentationEx
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("test"))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("test(\$one, \$two ...)"))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("test"))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("test(\$one, \$two ...)"))
                assertThat(presentation.presentableText, `is`("test"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall ; XQuery IntelliJ Plugin EBNF (95) ParamList")
        internal inner class FunctionCall {
            @Test
            @DisplayName("variadic; no arguments specified for the variadic parameter")
            fun variadicEmpty() {
                val f = parse<XPathFunctionCall>("fn:concat(2, 4)")[0] as XpmFunctionReference
                assertThat(f.arity, `is`(2))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("concat"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(2))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(3))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg1"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("2"))

                assertThat(op_qname_presentation(bindings[1].param.variableName!!), `is`("arg2"))
                assertThat(bindings[1].size, `is`(1))
                assertThat(bindings[1][0].text, `is`("4"))

                assertThat(op_qname_presentation(bindings[2].param.variableName!!), `is`("args"))
                assertThat(bindings[2].size, `is`(0))
            }

            @Test
            @DisplayName("variadic; single argument specified for the variadic parameter")
            fun variadicSingle() {
                val f = parse<XPathFunctionCall>("fn:concat(2, 4, 6)")[0] as XpmFunctionReference
                assertThat(f.arity, `is`(3))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("concat"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(3))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(3))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg1"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("2"))

                assertThat(op_qname_presentation(bindings[1].param.variableName!!), `is`("arg2"))
                assertThat(bindings[1].size, `is`(1))
                assertThat(bindings[1][0].text, `is`("4"))

                assertThat(op_qname_presentation(bindings[2].param.variableName!!), `is`("args"))
                assertThat(bindings[2].size, `is`(1))
                assertThat(bindings[2][0].text, `is`("6"))
            }

            @Test
            @DisplayName("variadic; multiple arguments specified for the variadic parameter")
            fun variadicMultiple() {
                val f = parse<XPathFunctionCall>("fn:concat(2, 4, 6, 8)")[0] as XpmFunctionReference
                assertThat(f.arity, `is`(4))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("concat"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(4))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(3))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg1"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("2"))

                assertThat(op_qname_presentation(bindings[1].param.variableName!!), `is`("arg2"))
                assertThat(bindings[1].size, `is`(1))
                assertThat(bindings[1][0].text, `is`("4"))

                assertThat(op_qname_presentation(bindings[2].param.variableName!!), `is`("args"))
                assertThat(bindings[2].size, `is`(2))
                assertThat(bindings[2][0].text, `is`("6"))
                assertThat(bindings[2][1].text, `is`("8"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (4.6) Using Declaration")
    internal inner class UsingDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) UsingDecl")
        internal inner class UsingDecl {
            @Test
            @DisplayName("using declaration")
            fun using() {
                val decl = parse<XpmNamespaceDeclaration>(
                    "using namespace 'http://www.w3.org/2005/xpath-functions/math';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(decl.namespaceUri?.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri?.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val decl = parse<XpmNamespaceDeclaration>("using namespace '';")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`(""))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("missing namespace")
            fun missingNamespace() {
                val decl = parse<XpmNamespaceDeclaration>("using namespace;")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri, `is`(nullValue()))

                assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }
    }
}
