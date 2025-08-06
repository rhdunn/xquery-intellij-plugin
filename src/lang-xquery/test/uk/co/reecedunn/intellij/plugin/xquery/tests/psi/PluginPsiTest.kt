// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginContextItemFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginLambdaFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginSequenceTypeList
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTypeAlias
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAnnotated
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmCatchClause
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.text
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmArrayExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.keyName
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmPathStep
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryIcons

@Suppress("RedundantVisibilityModifier", "Reformat")
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XQuery")
class PluginPsiTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("PluginPsiTest")
    override val language: Language = XQuery

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmFunctionProvider.register(this, XQueryFunctionProvider)

        XpmFunctionDecorator.register(this)

        project.registerService(XQueryProjectSettings())
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
    @DisplayName("XQuery IntelliJ Plugin (2.1.2.2) Tuple Test")
    internal inner class RecordTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (23) RecordTest")
        internal inner class RecordTest {
            @Test
            @DisplayName("empty")
            fun empty() {
                val test = parse<XPathRecordTest>("() instance of tuple ( (::) )")[0]
                assertThat(test.fields.count(), `is`(0))
                assertThat(test.isExtensible, `is`(true))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one field")
            fun one() {
                val test = parse<XPathRecordTest>("() instance of tuple ( test )")[0]
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
                val test = parse<XPathRecordTest>("() instance of tuple ( x : xs:float , y : xs:float )")[0]
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
                val test = parse<XPathRecordTest>("() instance of tuple ( * )")[0]
                assertThat(test.fields.count(), `is`(0))
                assertThat(test.isExtensible, `is`(true))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple fields; extensible")
            fun multipleExtensible() {
                val test = parse<XPathRecordTest>("() instance of tuple ( x : xs:float , y : xs:float , * )")[0]
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
        @DisplayName("XQuery IntelliJ Plugin EBNF (24) FieldDeclaration")
        internal inner class FieldDeclaration {
            @Test
            @DisplayName("required; unspecified type")
            fun nameOnlyRequired() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.fieldSeparator, `is`(nullValue()))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as XPathRecordTest
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
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test ? )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.fieldSeparator, `is`(nullValue()))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test?)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("required; specified type (Saxon 9.8 syntax)")
            fun nameAndTypeRequired_saxon9() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test : xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test: xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional; specified type (Saxon 9.8 syntax)")
            fun nameAndTypeOptional_saxon9() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test ? : xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test?: xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional; specified type; compact whitespace (Saxon 9.8 syntax)")
            fun nameAndTypeOptional_compactWhitespace_saxon9() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple(test?:xs:string)")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.fieldSeparator, `is`(XPathTokenType.ELVIS))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test?: xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("required; specified type (Saxon 10 syntax)")
            fun nameAndTypeRequired_saxon10() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test as xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test as xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional; specified type (Saxon 10 syntax)")
            fun nameAndTypeOptional_saxon10() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( test ? as xs:string )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType?.typeName, `is`("xs:string"))
                assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                assertThat(field.isOptional, `is`(true))

                val test = field.parent as XPathRecordTest
                assertThat(test.fields.first(), `is`(sameInstance(field)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("tuple(test? as xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("StringLiteral name; no space in name")
            fun stringLiteralName_noSpace() {
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( 'test' )")[0]
                assertThat(field.fieldName.data, `is`("test"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.fieldSeparator, `is`(nullValue()))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as XPathRecordTest
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
                val field = parse<XPathFieldDeclaration>("() instance of tuple ( 'test key name' )")[0]
                assertThat(field.fieldName.data, `is`("test key name"))
                assertThat(field.fieldType, `is`(nullValue()))
                assertThat(field.fieldSeparator, `is`(nullValue()))
                assertThat(field.isOptional, `is`(false))

                val test = field.parent as XPathRecordTest
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
                    val expr = parse<XPathMapConstructor>("object-node {}")[0] as XpmMapExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))

                    assertThat(expr.itemTypeClass, sameInstance(XdmObjectNode::class.java))
                    assertThat(expr.itemExpression, sameInstance(expr))

                    val entries = expr.entries.toList()
                    assertThat(entries.size, `is`(0))
                }

                @Test
                @DisplayName("with entries")
                fun withEntries() {
                    val expr = parse<XPathMapConstructor>("object-node { \"1\" : \"one\", \"2\" : \"two\" }")[0] as XpmMapExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR_ENTRY))
                    assertThat(expr.expressionElement?.textOffset, `is`(14))

                    assertThat(expr.itemTypeClass, sameInstance(XdmObjectNode::class.java))
                    assertThat(expr.itemExpression, sameInstance(expr))

                    val entries = expr.entries.toList()
                    assertThat(entries.size, `is`(2))

                    assertThat((entries[0].keyExpression as XPathStringLiteral).data, `is`("1"))
                    assertThat((entries[0].valueExpression as XPathStringLiteral).data, `is`("one"))

                    assertThat((entries[1].keyExpression as XPathStringLiteral).data, `is`("2"))
                    assertThat((entries[1].valueExpression as XPathStringLiteral).data, `is`("two"))
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

                    assertThat((entry.keyExpression as XPathStringLiteral).data, `is`("1"))
                    assertThat((entry.valueExpression as XPathStringLiteral).data, `is`("one"))
                    assertThat(entry.keyName, `is`(nullValue()))
                }

                @Test
                @DisplayName("Saxon")
                fun saxon() {
                    val entry = parse<XPathMapConstructorEntry>("map { \"1\" := \"one\" }")[0]
                    assertThat(entry.separator.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))

                    assertThat((entry.keyExpression as XPathStringLiteral).data, `is`("1"))
                    assertThat((entry.valueExpression as XPathStringLiteral).data, `is`("one"))
                    assertThat(entry.keyName, `is`(nullValue()))
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
                val expr = parse<XPathCurlyArrayConstructor>("array-node {}")[0] as XpmArrayExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))

                assertThat(expr.itemTypeClass, sameInstance(XdmArrayNode::class.java))
                assertThat(expr.itemExpression, sameInstance(expr))

                val entries = expr.memberExpressions.toList()
                assertThat(entries.size, `is`(0))
            }

            @Test
            @DisplayName("single member")
            fun singleMember() {
                val expr = parse<XPathCurlyArrayConstructor>("array-node { 1 }")[0] as XpmArrayExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))

                assertThat(expr.itemTypeClass, sameInstance(XdmArrayNode::class.java))
                assertThat(expr.itemExpression, sameInstance(expr))

                val entries = expr.memberExpressions.toList()
                assertThat(entries.size, `is`(1))
                assertThat(entries[0].text, `is`("1"))
            }

            @Test
            @DisplayName("multiple members")
            fun multipleMembers() {
                val expr = parse<XPathCurlyArrayConstructor>("array-node { 1, 2 + 3, 4 }")[0] as XpmArrayExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))

                assertThat(expr.itemTypeClass, sameInstance(XdmArrayNode::class.java))
                assertThat(expr.itemExpression, sameInstance(expr))

                val entries = expr.memberExpressions.toList()
                assertThat(entries.size, `is`(3))
                assertThat(entries[0].text, `is`("1"))
                assertThat(entries[1].text, `is`("2 + 3"))
                assertThat(entries[2].text, `is`("4"))
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
                val expr = parse<XQueryCatchClause>("try { 1 } catch (\$x) { 2 }")[0] as XpmCatchClause
                assertThat(expr.catchExpression.text, `is`("2"))
                assertThat(expr.errorList.count(), `is`(0))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(XQueryCatchClause::class.java)))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XQueryCatchClause>("try { 1 } catch (\$a:x) { 2 }")[0] as XpmCatchClause
                assertThat(expr.catchExpression.text, `is`("2"))
                assertThat(expr.errorList.count(), `is`(0))

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(XQueryCatchClause::class.java)))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XQueryCatchClause>(
                    "try { 1 } catch (\$Q{http://www.example.com}x) { 2 }"
                )[0] as XpmCatchClause
                assertThat(expr.catchExpression.text, `is`("2"))
                assertThat(expr.errorList.count(), `is`(0))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(XQueryCatchClause::class.java)))
            }

            @Test
            @DisplayName("missing VarName")
            fun noVarName() {
                val expr = parse<XQueryCatchClause>("try { 1 } catch () { 2 }")[0] as XpmCatchClause
                assertThat(expr.variableName, `is`(nullValue()))
                assertThat(expr.catchExpression.text, `is`("2"))
                assertThat(expr.errorList.count(), `is`(0))
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
        @DisplayName("XQuery IntelliJ Plugin XQuery EBNF (93) ElvisExpr")
        fun elvisExpr() {
            val expr = parse<PluginElvisExpr>("1 ?: 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ELVIS))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (4.1) Item Type Declaration")
    internal inner class TypeDeclaration {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (19) ItemTypeDecl")
        internal inner class ItemTypeDecl {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XQueryItemTypeDecl>("declare type test := xs:string;")[0]

                val qname = expr.typeName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))

                val presentation = (expr as NavigationItem).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.TypeDecl)))
                assertThat(presentation.presentableText, `is`("test"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("with CompatibilityAnnotation")
            fun withCompatibiliyAnnotation() {
                val expr = parse<XQueryItemTypeDecl>("declare private type test := xs:string;")[0]

                val qname = expr.typeName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))

                val presentation = (expr as NavigationItem).presentation!!
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
                assertThat(qname_presentation(annotation.name!!), `is`("private"))

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
                assertThat(qname_presentation(annotation.name!!), `is`("updating"))

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
                assertThat(qname_presentation(annotation.name!!), `is`("sequential"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("sequential"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
        internal inner class VarDecl {
            @Test
            @DisplayName("visibility (public/private)")
            fun visibility() {
                val decls = parse<XpmAnnotated>(
                    """
                    declare variable ${'$'}a := 1;
                    declare private variable ${'$'}c := 3;
                    """.trimIndent()
                )
                assertThat(decls[0].accessLevel, `is`(XpmAccessLevel.Public))
                assertThat(decls[1].accessLevel, `is`(XpmAccessLevel.Private))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("visibility (%public/%private)")
            fun visibility() {
                val decls = parse<XpmAnnotated>(
                    """
                    declare function a() {};
                    declare private function c() {};
                    """.trimIndent()
                )
                assertThat(decls[0].accessLevel, `is`(XpmAccessLevel.Public))
                assertThat(decls[1].accessLevel, `is`(XpmAccessLevel.Private))
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

                assertThat(decl.parameters.size, `is`(2))
                assertThat(qname_presentation(decl.parameters[0].variableName!!), `is`("one"))
                assertThat(qname_presentation(decl.parameters[1].variableName!!), `is`("two"))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
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
