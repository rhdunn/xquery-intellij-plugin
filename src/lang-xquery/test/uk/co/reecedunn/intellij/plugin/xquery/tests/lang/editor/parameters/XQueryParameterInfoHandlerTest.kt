// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.parameters

import com.intellij.lang.Language
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.openapi.extensions.PluginId
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.ParameterInfoTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.editor.parameters.XPathParameterInfoHandler
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
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

@Suppress("RedundantVisibilityModifier", "Reformat")
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
class XQueryParameterInfoHandlerTest : IdeaPlatformTestCase(), ParameterInfoTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryParameterInfoHandlerTest")
    override val language: Language = XQuery

    val parameterInfoHandler = XPathParameterInfoHandler()

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
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
    @DisplayName("find element for parameter info")
    internal inner class FindElementForParameterInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = createParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(4))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = createParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(45))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val context = createParameterInfoContext("string(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(2))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:string"))
                assertThat(items[0].declaredArity, `is`(0))

                assertThat(qname_presentation(items[1].functionName!!), `is`("fn:string"))
                assertThat(items[1].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("no duplicates")
            fun noDuplicates() {
                val context = createParameterInfoContext(
                    """
                    module namespace t = "http://www.example.co.uk";
                    declare function f(${'$'}x) {};
                    declare function g() { f(2) };
                    """, 161
                )
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(161))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("f"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = createParameterInfoContext("let \$a := abs#1 return \$a(2)", 26)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(26))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
            fun namedFunctionRef() {
                val context = createParameterInfoContext("abs#1(2)", 6)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(6))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = createParameterInfoContext("let \$a := abs#1 return 2 => \$a()", 31)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(31))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = createParameterInfoContext("2 => abs()", 9)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(9))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = createParameterInfoContext("2 => fn:abs()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(12))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("2 => Q{http://www.w3.org/2005/xpath-functions}abs()", 50)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(50))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val context = createParameterInfoContext("2 => (fn:abs#1)()", 16)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(16))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:abs"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val context = createParameterInfoContext("2 => adjust-date-to-timezone()", 29)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(29))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(2))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:adjust-date-to-timezone"))
                assertThat(items[0].declaredArity, `is`(1))

                assertThat(qname_presentation(items[1].functionName!!), `is`("fn:adjust-date-to-timezone"))
                assertThat(items[1].declaredArity, `is`(2))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("no duplicates")
            fun noDuplicates() {
                val context = createParameterInfoContext(
                    """
                    module namespace t = "http://www.example.co.uk";
                    declare function f(${'$'}x) {};
                    declare function g() { 2 => f() };
                    """, 166
                )
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(166))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("f"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("exclude empty parameter function")
            fun excludeEmptyParameterFunction() {
                val context = createParameterInfoContext("2 => string()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(12))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(1))

                assertThat(qname_presentation(items[0].functionName!!), `is`("fn:string"))
                assertThat(items[0].declaredArity, `is`(1))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("find element for updating parameter info")
    internal inner class FindElementForUpdatingParameterInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = updateParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(4))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = updateParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = updateParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(45))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = updateParameterInfoContext("let \$a := abs#1 return \$a(2)", 26)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(26))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
            fun namedFunctionRef() {
                val context = updateParameterInfoContext("abs#1(2)", 6)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(6))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (131) VarRef")
            fun varRef() {
                val context = updateParameterInfoContext("let \$a := abs#1 return 2 => \$a()", 31)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(31))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (235) NCName")
            fun ncname() {
                val context = updateParameterInfoContext("2 => abs()", 9)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (234) QName")
            fun qname() {
                val context = updateParameterInfoContext("2 => fn:abs()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (218) EQName ; XQuery 3.1 EBNF (223) URIQualifiedName")
            fun uriQualifiedName() {
                val context = updateParameterInfoContext("2 => Q{http://www.w3.org/2005/xpath-functions}abs()", 50)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(50))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val context = updateParameterInfoContext("2 => (fn:abs#1)()", 16)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForUpdatingParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(16))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }
        }
    }

    @Nested
    @DisplayName("show parameter info")
    internal inner class ShowParameterInfo {
        @Test
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        fun functionCall() {
            val context = createParameterInfoContext("abs(2)", 4)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            parameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(4))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(3))
            assertThat(hint.showHintHandler, `is`(sameInstance(parameterInfoHandler)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        fun postfixExpr() {
            val context = createParameterInfoContext("abs#1(2)", 6)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            parameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(6))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(5))
            assertThat(hint.showHintHandler, `is`(sameInstance(parameterInfoHandler)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        fun arrowExpr() {
            val context = createParameterInfoContext("2 => abs()", 9)
            val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
            parameterInfoHandler.showParameterInfo(function, context)

            assertThat(context.highlightedElement, `is`(nullValue()))
            assertThat(context.parameterListStart, `is`(9))
            assertThat(context.itemsToShow, `is`(nullValue()))

            val hint = context as MockCreateParameterInfoContext
            assertThat(hint.showHintElement, `is`(sameInstance(function)))
            assertThat(hint.showHintOffset, `is`(8))
            assertThat(hint.showHintHandler, `is`(sameInstance(parameterInfoHandler)))
        }
    }

    @Nested
    @DisplayName("update parameter info")
    internal inner class UpdateParameterInfo {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("concat()", 7)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("concat(1)", 7)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("first argument")
            fun first() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 7)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(7))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("second argument")
            fun second() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 10)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(10))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("last argument")
            fun last() {
                val context = updateParameterInfoContext("concat(1, 2, 3, 4, 5)", 19)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(19))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(4))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("concat#1()", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("concat#1(1)", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("first argument")
            fun first() {
                val context = updateParameterInfoContext("concat#1(1, 2, 3, 4, 5)", 9)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(9))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(0))
            }

            @Test
            @DisplayName("second argument")
            fun second() {
                val context = updateParameterInfoContext("concat#1(1, 2, 3, 4, 5)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("last argument")
            fun last() {
                val context = updateParameterInfoContext("concat#2(1, 2, 3, 4, 5)", 21)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(21))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(4))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("empty arguments")
            fun empty() {
                val context = updateParameterInfoContext("1 => concat()", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("single argument")
            fun single() {
                val context = updateParameterInfoContext("1 => concat(1)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("first argument")
            fun first() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 12)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(12))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(1))
            }

            @Test
            @DisplayName("second argument")
            fun second() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 15)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(15))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(2))
            }

            @Test
            @DisplayName("last argument")
            fun last() {
                val context = updateParameterInfoContext("2 => concat(1, 2, 3, 4, 5)", 24)
                val function = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                parameterInfoHandler.updateParameterInfo(function, context)

                assertThat(context.parameterOwner, `is`(nullValue()))
                assertThat(context.highlightedParameter, `is`(nullValue()))
                assertThat(context.objectsToView.size, `is`(0))

                assertThat(context.parameterListStart, `is`(24))
                assertThat(context.isPreservedOnHintHidden, `is`(false))
                assertThat(context.isInnermostContext, `is`(false))

                assertThat(context.isUIComponentEnabled(0), `is`(false))
                assertThat(context.isUIComponentEnabled(1), `is`(false))

                val update = context as MockUpdateParameterInfoContext
                assertThat(update.isSingleParameterInfo, `is`(false))
                assertThat(update.currentParameter, `is`(5))
            }
        }
    }

    @Nested
    @DisplayName("update UI")
    internal inner class UpdateUI {
        fun updateUI(
            context: CreateParameterInfoContext,
            parameterIndex: Int,
            itemToShow: Int = 0
        ): MockParameterInfoUIContext<XPathArgumentList> {
            val function = parameterInfoHandler.findElementForParameterInfo(context)

            val ui = MockParameterInfoUIContext<XPathArgumentList>(function)
            ui.currentParameterIndex = parameterIndex

            parameterInfoHandler.updateUI(context.itemsToShow!![itemToShow] as XpmFunctionDeclaration, ui)
            assertThat(ui.currentParameterIndex, `is`(parameterIndex))
            assertThat(ui.parameterOwner, `is`(sameInstance(function)))
            assertThat(ui.isSingleOverload, `is`(false))
            assertThat(ui.isSingleParameterInfo, `is`(false))
            assertThat(ui.isUIComponentEnabled, `is`(false))

            return ui
        }

        fun highlighted(ui: MockParameterInfoUIContext<XPathArgumentList>): String = when {
            ui.highlightStart == -1 && ui.highlightEnd == -1 -> "<none>"
            else -> ui.text.substring(ui.highlightStart, ui.highlightEnd)
        }

        fun highlighted(context: CreateParameterInfoContext, parameterIndex: Int, itemToShow: Int = 0): String {
            return highlighted(updateUI(context, parameterIndex, itemToShow))
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.4.1.2) Evaluating Static Function Calls ; XQuery 3.1 (3.1.5.1) Evaluating Static and Dynamic Function Calls")
        internal inner class EvaluatingStaticFunctionCalls {
            @Nested
            @DisplayName("For non-variadic functions")
            internal inner class ForNonVariadicFunctions {
                @Test
                @DisplayName("empty arguments")
                fun emptyArguments() {
                    val context = createParameterInfoContext("true()", 5)

                    val ui = updateUI(context, -1)
                    assertThat(ui.text, `is`("<no parameters>"))
                    assertThat(highlighted(ui), `is`("<none>"))
                }

                @Test
                @DisplayName("positional arguments")
                fun positionalArguments() {
                    val context = createParameterInfoContext("replace(1, 2, 3)", 8)

                    val ui = updateUI(context, -1)
                    assertThat(ui.text, `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("keyword arguments")
                fun keywordArguments() {
                    val context = createParameterInfoContext("replace(pattern: 1, value: 2, replacement: 3)", 8)

                    val ui = updateUI(context, -1)
                    assertThat(ui.text, `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 1), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("positional and keyword arguments")
                fun positionalAndKeywordArguments() {
                    val context = createParameterInfoContext("replace(1, replacement: 2, pattern: 3)", 8)

                    val ui = updateUI(context, -1)
                    assertThat(ui.text, `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("sequence argument")
                fun sequenceArgument() {
                    val context = createParameterInfoContext("replace(1, 2, (3, 4, 5))", 8)

                    val ui = updateUI(context, -1)
                    assertThat(ui.text, `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string"))
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }
            }

            @Nested
            @DisplayName("For non-variadic arrow functions")
            internal inner class ForNonVariadicArrowFunctions {
                @Test
                @DisplayName("positional arguments")
                fun positionalArguments() {
                    val context = createParameterInfoContext("1 => replace(2, 3)", 13)

                    val ui = updateUI(context, -1)
                    assertThat(
                        ui.text,
                        `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string")
                    )
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("keyword arguments")
                fun keywordArguments() {
                    val context = createParameterInfoContext("1 => replace(replacement: 2, pattern: 3)", 13)

                    val ui = updateUI(context, -1)
                    assertThat(
                        ui.text,
                        `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string")
                    )
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("positional and keyword arguments")
                fun positionalAndKeywordArguments() {
                    val context = createParameterInfoContext("1 => replace(2, replacement: 3)", 13)

                    val ui = updateUI(context, -1)
                    assertThat(
                        ui.text,
                        `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string")
                    )
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

                @Test
                @DisplayName("sequence argument")
                fun sequenceArgument() {
                    val context = createParameterInfoContext("1 => replace(2, (3, 4, 5))", 13)

                    val ui = updateUI(context, -1)
                    assertThat(
                        ui.text,
                        `is`("\$value as xs:string?, \$pattern as xs:string, \$replacement as xs:string")
                    )
                    assertThat(highlighted(ui), `is`("<none>"))

                    assertThat(highlighted(context, 0), `is`("\$value as xs:string?"))
                    assertThat(highlighted(context, 1), `is`("\$pattern as xs:string"))
                    assertThat(highlighted(context, 2), `is`("\$replacement as xs:string"))
                    assertThat(highlighted(context, 3), `is`("<none>"))
                }

            }

            @Test
            @DisplayName("For ellipsis-variadic functions")
            fun forEllipsisVariadicFunctions() {
                val context = createParameterInfoContext("concat(1, 2, 3, 4, 5)", 19)

                val ui = updateUI(context, -1, itemToShow = 2)
                assertThat(
                    ui.text,
                    `is`("\$value1 as xs:anyAtomicType?, \$value2 as xs:anyAtomicType?, \$values as xs:anyAtomicType? ...")
                )
                assertThat(highlighted(ui), `is`("<none>"))

                assertThat(highlighted(context, 0, itemToShow = 2), `is`("\$value1 as xs:anyAtomicType?"))
                assertThat(highlighted(context, 1, itemToShow = 2), `is`("\$value2 as xs:anyAtomicType?"))
                assertThat(highlighted(context, 2, itemToShow = 2), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 3, itemToShow = 2), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 4, itemToShow = 2), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 5, itemToShow = 2), `is`("<none>"))
            }

            @Test
            @DisplayName("For ellipsis-variadic arrow functions")
            fun forEllipsisVariadicArrowFunctions() {
                val context = createParameterInfoContext("1 => concat(2, 3, 4, 5)", 21)

                val ui = updateUI(context, -1, itemToShow = 1)
                assertThat(
                    ui.text,
                    `is`("\$value1 as xs:anyAtomicType?, \$value2 as xs:anyAtomicType?, \$values as xs:anyAtomicType? ...")
                )
                assertThat(highlighted(ui), `is`("<none>"))

                assertThat(highlighted(context, 0, itemToShow = 1), `is`("\$value1 as xs:anyAtomicType?"))
                assertThat(highlighted(context, 1, itemToShow = 1), `is`("\$value2 as xs:anyAtomicType?"))
                assertThat(highlighted(context, 2, itemToShow = 1), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 3, itemToShow = 1), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 4, itemToShow = 1), `is`("\$values as xs:anyAtomicType? ..."))
                assertThat(highlighted(context, 5, itemToShow = 1), `is`("<none>"))
            }
        }
    }
}
