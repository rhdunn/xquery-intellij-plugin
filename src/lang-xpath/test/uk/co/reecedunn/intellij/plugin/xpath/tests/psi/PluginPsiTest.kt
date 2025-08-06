// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginContextItemFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginLambdaFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTypeAlias
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider

@Suppress("RedundantVisibilityModifier")
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XPath")
class PluginPsiTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("PluginPsiTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
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
    @DisplayName("XQuery IntelliJ Plugin XPath (2.1.2.5) Type Alias")
    internal inner class TypeAlias {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (34) TypeAlias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of ~ test")[0] as XsQNameValue
                assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
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
    @DisplayName("XQuery IntelliJ Plugin XPath (3.4) Logical Expressions")
    internal inner class LogicalExpressions {
        @Test
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
        fun orExpr() {
            val expr = parse<XPathOrExpr>("1 or 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OR))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
        fun andExpr() {
            val expr = parse<XPathAndExpr>("1 andAlso 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_ANDALSO))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.6) Primary Expressions")
    internal inner class PrimaryExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath (3.6.1) Inline Function Expressions")
        internal inner class InlineFunctionExpressions {
            @Test
            @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
            fun contextItemFunctionExpr() {
                val expr = parse<PluginContextItemFunctionExpr>(".{ () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }

            @Test
            @DisplayName("XQuery IntelliJ Plugin XPath EBNF (35) LambdaFunctionExpr")
            fun lambdaFunctionExpr() {
                val expr = parse<PluginLambdaFunctionExpr>("_{ $1 }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.LAMBDA_FUNCTION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }
    }
}
