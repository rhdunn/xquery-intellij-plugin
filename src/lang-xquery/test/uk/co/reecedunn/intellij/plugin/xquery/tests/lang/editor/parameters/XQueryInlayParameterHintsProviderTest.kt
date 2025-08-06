// Copyright (C) 2020-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.parameters

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.editor.parameters.XPathInlayParameterHintsProvider
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("UnstableApiUsage", "RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Inlay Hints - XPath Parameter Hints Provider")
class XQueryInlayParameterHintsProviderTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryInlayParameterHintsProviderTest")
    override val language: Language = XQuery

    val provider = XPathInlayParameterHintsProvider()

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
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

        project.registerService(XQueryProjectSettings())
        project.registerService(XpmModuleLoaderSettings(project))

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)
    }

    @Nested
    @DisplayName("get hint info")
    internal inner class GetHintInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val f = parse<XPathFunctionCall>("abs(2)")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val f = parse<XPathFunctionCall>("fn:abs(2)")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val f = parse<XPathFunctionCall>("Q{http://www.w3.org/2005/xpath-functions}abs(2)")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val f = parse<PluginArrowDynamicFunctionCall>("let \$a := abs#1 return 2 => \$a()")[0]

                val info = provider.getHintInfo(f)
                assertThat(info, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val f = parse<PluginArrowFunctionCall>("2 => abs()")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val f = parse<PluginArrowFunctionCall>("2 => fn:abs()")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val f = parse<PluginArrowFunctionCall>("2 => Q{http://www.w3.org/2005/xpath-functions}abs()")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val f = parse<PluginArrowDynamicFunctionCall>("2 => (fn:abs#1)()")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xpath-functions}abs"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("abs"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("value"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        internal inner class Param {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val f = parse<XPathFunctionCall>("declare function local:test(\$x) {}; local:test(2)")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val f = parse<XPathFunctionCall>("declare function local:test(\$fn:x) {}; local:test(2)")[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val f = parse<XPathFunctionCall>(
                    "declare function local:test(\$Q{http://www.example.com}x) {}; local:test(2)"
                )[0]

                val info = provider.getHintInfo(f)!!
                assertThat(info.fullyQualifiedName, `is`("Q{http://www.w3.org/2005/xquery-local-functions}test"))
                assertThat(info.language, `is`(nullValue()))
                assertThat(info.getMethodName(), `is`("test"))

                assertThat(info.paramNames.size, `is`(1))
                assertThat(info.paramNames[0], `is`("x"))
            }
        }
    }

    @Nested
    @DisplayName("get parameter hints")
    internal inner class GetParameterHints {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
            fun integerLiteral() {
                val f = parse<XPathFunctionCall>("declare function local:f(\$arg1, \$arg2) {}; local:f(3, 6)")[0]

                val hints = provider.getParameterHints(f)
                assertThat(hints.size, `is`(2))

                assertThat(hints[0].text, `is`("arg1"))
                assertThat(hints[0].offset, `is`(51))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                assertThat(hints[1].text, `is`("arg2"))
                assertThat(hints[1].offset, `is`(54))
                assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Test
            @DisplayName("XQuery 4.0 ED EBNF (137) KeywordArgument")
            fun keywordArgument() {
                val f = parse<XPathFunctionCall>("declare function local:f(\$arg1, \$arg2) {}; local:f(3, arg2: 6)")[0]

                val hints = provider.getParameterHints(f)
                assertThat(hints.size, `is`(1))

                assertThat(hints[0].text, `is`("arg1"))
                assertThat(hints[0].offset, `is`(51))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (107) SimpleMapExpr")
            internal inner class SimpleMapExpr {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(a!?one, a!?two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(59))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(a!?one, a!?arg2, a!?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (181) UnaryLookup")
            internal inner class UnaryLookup {
                @Test
                @DisplayName("KeySpecifier is an IntegerLiteral")
                fun integerLiteral() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(?1, ?2)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(55))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(?one, ?two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(57))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(?one, ?arg2, ?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(71))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (125) Lookup")
            internal inner class Lookup {
                @Test
                @DisplayName("KeySpecifier is an IntegerLiteral")
                fun integerLiteral() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}a?1, ${'$'}a?2)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(57))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}a?one, ${'$'}a?two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(59))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}a?one, ${'$'}a?arg2, ${'$'}a?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            internal inner class VarRef {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}one, ${'$'}two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(57))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}one, ${'$'}arg2, ${'$'}three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(71))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest ; XQuery 3.1 EBNF (119) NameTest")
            internal inner class NameTest {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/one, ${'$'}x/lorem//two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(59))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/one, ${'$'}x/arg2, ${'$'}x/lorem//three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
            internal inner class AbbrevForwardStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/@one, ${'$'}x/lorem//@two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(60))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/@one, ${'$'}x/@arg2, ${'$'}x/lorem//@three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(77))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep")
            internal inner class ForwardStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/child::one, ${'$'}x/lorem//child::two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(66))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/child::one, ${'$'}x/child::arg2, ${'$'}x/lorem//child::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(89))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (115) ReverseStep")
            internal inner class ReverseStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(${'$'}x/parent::one, ${'$'}x/lorem//parent::two)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(67))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(${'$'}x/parent::one, ${'$'}x/parent::arg2, ${'$'}x/lorem//parent::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(91))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            internal inner class DirElemConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(<one/>, <two/>)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(59))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(<one/>, <arg2/>, <three/>)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            internal inner class CompElemConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(element one {}, element two {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(67))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(element one {}, element arg2 {}, element three {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(91))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
            internal inner class CompAttrConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2) {};
                        |local:f(attribute one {}, attribute two {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(51))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(69))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<XPathFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |local:f(attribute one {}, attribute arg2 {}, attribute three {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(58))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(95))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
            fun integerLiteral() {
                val f = parse<PluginArrowFunctionCall>(
                    "declare function local:f(\$arg1, \$arg2) {}; 3 => local:f(6)"
                )[0]

                val hints = provider.getParameterHints(f)
                assertThat(hints.size, `is`(1))

                assertThat(hints[0].text, `is`("arg2"))
                assertThat(hints[0].offset, `is`(56))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (107) SimpleMapExpr")
            internal inner class SimpleMapExpr {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |a!?one => local:f(a!?two, a!?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(68))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(76))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |a!?one => local:f(a!?two, a!?arg3, a!?four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(75))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (181) UnaryLookup")
            internal inner class UnaryLookup {
                @Test
                @DisplayName("KeySpecifier is an IntegerLiteral")
                fun integerLiteral() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |?1 => local:f(?2, ?3)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(64))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(68))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |?one => local:f(?two, ?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(72))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |?one => local:f(?two, ?arg3, ?four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(86))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (125) Lookup")
            internal inner class Lookup {
                @Test
                @DisplayName("KeySpecifier is an IntegerLiteral")
                fun integerLiteral() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}a?1 => local:f(${'$'}a?2, ${'$'}a?3)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(72))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}a?one => local:f(${'$'}a?two, ${'$'}a?three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(68))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(76))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}a?one => local:f(${'$'}a?two, ${'$'}a?arg3, ${'$'}a?four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(75))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            internal inner class VarRef {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}two, ${'$'}three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(72))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}two, ${'$'}arg3, ${'$'}four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(86))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (118) NodeTest ; XQuery 3.1 EBNF (119) NameTest")
            internal inner class NameTest {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/two, ${'$'}x/lorem//three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(74))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/two, ${'$'}x/arg3, ${'$'}x/lorem//four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(90))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep ; XQuery 3.1 EBNF (114) AbbrevForwardStep")
            internal inner class AbbrevForwardStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/@two, ${'$'}x/lorem//@three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(75))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/@two, ${'$'}x/@arg3, ${'$'}x/lorem//@four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (112) ForwardStep")
            internal inner class ForwardStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/child::two, ${'$'}x/lorem//child::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(81))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/child::two, ${'$'}x/child::arg3, ${'$'}x/lorem//child::four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(104))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (115) ReverseStep")
            internal inner class ReverseStep {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |${'$'}one => local:f(${'$'}x/parent::two, ${'$'}x/lorem//parent::three)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(66))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(82))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |${'$'}one => local:f(${'$'}x/parent::two, ${'$'}x/parent::arg3, ${'$'}x/lorem//parent::four)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(73))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(106))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            internal inner class DirElemConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |<one/> => local:f(<two/>, <three/>)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(68))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(76))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |<one/> => local:f(<two/>, <arg3/>, <four/>)
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(75))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            internal inner class CompElemConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |element one {} => local:f(element two {}, element three {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(76))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(92))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |element one {} => local:f(element two {}, element arg3 {}, element four {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(83))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(116))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
            internal inner class CompAttrConstructor {
                @Test
                @DisplayName("name not matching the parameter names")
                fun different() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3) {};
                        |attribute one {} => local:f(attribute two {}, attribute three {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(78))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg3"))
                    assertThat(hints[1].offset, `is`(96))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("name matching the parameter names")
                fun same() {
                    val f = parse<PluginArrowFunctionCall>(
                        """
                        |declare function local:f(${'$'}arg1, ${'$'}arg2, ${'$'}arg3, ${'$'}arg4) {};
                        |attribute one {} => local:f(attribute two {}, attribute arg3 {}, attribute four {})
                        """.trimMargin()
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg2"))
                    assertThat(hints[0].offset, `is`(85))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg4"))
                    assertThat(hints[1].offset, `is`(122))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
        internal inner class ParamList {
            @Test
            @DisplayName("incomplete variable name")
            fun incompleteVariableName() {
                val f = parse<XPathFunctionCall>("declare function local:f(\$, \$arg2) {}; local:f(3, 4)")[0]

                val hints = provider.getParameterHints(f)
                assertThat(hints.size, `is`(1))

                assertThat(hints[0].text, `is`("arg2"))
                assertThat(hints[0].offset, `is`(50))
                assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
            }

            @Nested
            @DisplayName("variadic")
            internal inner class Variadic {
                @Test
                @DisplayName("zero")
                fun zero() {
                    val f = parse<XPathFunctionCall>(
                        "declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3)"
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(1))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("one")
                fun one() {
                    val f = parse<XPathFunctionCall>(
                        "declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3, 6)"
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(58))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }

                @Test
                @DisplayName("many")
                fun many() {
                    val f = parse<XPathFunctionCall>(
                        "declare function local:f(\$arg1, \$arg2 ...) {}; local:f(3, 6, 9, 12)"
                    )[0]

                    val hints = provider.getParameterHints(f)
                    assertThat(hints.size, `is`(2))

                    assertThat(hints[0].text, `is`("arg1"))
                    assertThat(hints[0].offset, `is`(55))
                    assertThat(hints[0].isShowOnlyIfExistedBefore, `is`(false))

                    assertThat(hints[1].text, `is`("arg2"))
                    assertThat(hints[1].offset, `is`(58))
                    assertThat(hints[1].isShowOnlyIfExistedBefore, `is`(false))
                }
            }
        }
    }
}
