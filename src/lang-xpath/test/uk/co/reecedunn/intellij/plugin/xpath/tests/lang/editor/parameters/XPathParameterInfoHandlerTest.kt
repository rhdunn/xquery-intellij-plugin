// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.editor.parameters

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.editor.requiresPsiFileGetEditor
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.MockCreateParameterInfoContext
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parameterInfo.ParameterInfoTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath as XPathLanguage
import uk.co.reecedunn.intellij.plugin.xpath.lang.editor.parameters.XPathParameterInfoHandler
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
class XPathParameterInfoHandlerTest : IdeaPlatformTestCase(), ParameterInfoTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XPathParameterInfoHandlerTest")
    override val language: Language = XPathLanguage

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()
        requiresPsiFileGetEditor()

        XPathASTFactory().registerExtension(project, XPathLanguage)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        XpmFunctionProvider.register(this)
    }

    private val parameterInfoHandler = XPathParameterInfoHandler()

    @Nested
    @DisplayName("find element for parameter info")
    internal inner class FindElementForParameterInfo {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (123) NCName")
            fun ncname() {
                val context = createParameterInfoContext("abs(2)", 4)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(4))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (122) QName")
            fun qname() {
                val context = createParameterInfoContext("fn:abs(2)", 7)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(7))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (117) URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("Q{http://www.w3.org/2005/xpath-functions}abs(2)", 45)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(45))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (49) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XPath 3.1 EBNF (59) VarRef")
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
            @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
            fun namedFunctionRef() {
                val context = createParameterInfoContext("abs#1(2)", 6)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(6))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (29) ArrowExpr ; XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XPath 3.1 EBNF (59) VarRef")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (123) NCName")
            fun ncname() {
                val context = createParameterInfoContext("2 => abs()", 9)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(9))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (122) QName")
            fun qname() {
                val context = createParameterInfoContext("2 => fn:abs()", 12)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(12))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (117) URIQualifiedName")
            fun uriQualifiedName() {
                val context = createParameterInfoContext("2 => Q{http://www.w3.org/2005/xpath-functions}abs()", 50)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(50))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

                val hint = context as MockCreateParameterInfoContext
                assertThat(hint.showHintElement, `is`(nullValue()))
                assertThat(hint.showHintOffset, `is`(0))
                assertThat(hint.showHintHandler, `is`(nullValue()))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (61) ParenthesizedExpr")
            fun parenthesizedExpr() {
                val context = createParameterInfoContext("2 => (fn:abs#1)()", 16)
                val args = context.file.walkTree().filterIsInstance<XPathArgumentList>().first()
                val item = parameterInfoHandler.findElementForParameterInfo(context)
                assertThat(item, `is`(sameInstance(args)))

                assertThat(context.highlightedElement, `is`(nullValue()))
                assertThat(context.parameterListStart, `is`(16))

                val items = context.itemsToShow!!.map { it as XpmFunctionDeclaration }
                assertThat(items.size, `is`(0))

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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (123) NCName")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (122) QName")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (117) URIQualifiedName")
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
        @DisplayName("XPath 3.1 EBNF (49) PostfixExpr")
        internal inner class PostfixExpr {
            @Test
            @DisplayName("XPath 3.1 EBNF (59) VarRef")
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
            @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
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
        @DisplayName("XPath 3.1 EBNF (29) ArrowExpr ; XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
        internal inner class ArrowExpr {
            @Test
            @DisplayName("XPath 3.1 EBNF (59) VarRef")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (123) NCName")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (122) QName")
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
            @DisplayName("XPath 3.1 EBNF (112) EQName ; XPath 3.1 EBNF (117) URIQualifiedName")
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
            @DisplayName("XPath 3.1 EBNF (61) ParenthesizedExpr")
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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
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
        @DisplayName("XPath 3.1 EBNF (49) PostfixExpr")
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
        @DisplayName("XPath 3.1 EBNF (29) ArrowExpr ; XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
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
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
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
        @DisplayName("XPath 3.1 EBNF (49) PostfixExpr")
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
        @DisplayName("XPath 3.1 EBNF (29) ArrowExpr ; XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
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
}
