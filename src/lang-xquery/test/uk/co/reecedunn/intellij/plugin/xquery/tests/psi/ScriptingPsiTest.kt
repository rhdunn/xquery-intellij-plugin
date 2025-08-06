// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmConcatenatingExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.text
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginBlockVarDeclEntry
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery Scripting Extension 1.0 - IntelliJ Program Structure Interface (PSI)")
class ScriptingPsiTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("ScriptingPsiTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.1) Apply Expression")
    internal inner class ApplyExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (32) ApplyExpr")
        fun applyExpr() {
            val expr = parse<ScriptingApplyExpr>("1; 2;")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.APPLY_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.2) Block Expressions")
    internal inner class BlockExpressions {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (153) BlockExpr")
        fun blockExpr() {
            val expr = parse<ScriptingBlockExpr>("block { 1, 2 }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.BLOCK_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Nested
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (157) BlockBody")
        internal inner class BlockBody {
            @Test
            @DisplayName("single")
            fun single() {
                val expr = parse<ScriptingBlockBody>("block { 1 }")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(1))
                assertThat(exprs[0].text, `is`("1"))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expr = parse<ScriptingBlockBody>("block { 1, 2 + 3, 4 }")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(3))
                assertThat(exprs[0].text, `is`("1"))
                assertThat(exprs[1].text, `is`("2 + 3"))
                assertThat(exprs[2].text, `is`("4"))
            }
        }

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
            fun ncname() {
                val decl = parse<XpmVariableDeclaration>("block { declare \$x := \$y; 2 }")[0]
                assertThat(decl.isExternal, `is`(false))
                assertThat(decl.variableType?.typeName, `is`(nullValue()))
                assertThat(decl.variableExpression?.text, `is`("\$y"))

                val qname = decl.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = decl.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(ScriptingBlock::class.java)))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val decl = parse<XpmVariableDeclaration>("block { declare \$a:x := \$a:y; 2 }")[0]
                assertThat(decl.isExternal, `is`(false))
                assertThat(decl.variableType?.typeName, `is`(nullValue()))
                assertThat(decl.variableExpression?.text, `is`("\$a:y"))

                val qname = decl.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = decl.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(ScriptingBlock::class.java)))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val decl = parse<XpmVariableDeclaration>(
                    "block { declare \$Q{http://www.example.com}x := \$Q{http://www.example.com}y; 2 }"
                )[0]
                assertThat(decl.isExternal, `is`(false))
                assertThat(decl.variableType?.typeName, `is`(nullValue()))
                assertThat(decl.variableExpression?.text, `is`("\$Q{http://www.example.com}y"))

                val qname = decl.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = decl.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(ScriptingBlock::class.java)))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val decl = parse<XpmVariableDeclaration>("block { declare \$ := \$y; 2 }")[0]
                assertThat(decl.isExternal, `is`(false))
                assertThat(decl.variableType?.typeName, `is`(nullValue()))
                assertThat(decl.variableExpression?.text, `is`("\$y"))
                assertThat(decl.variableName, `is`(nullValue()))
            }

            @Test
            @DisplayName("with type")
            fun withType() {
                val decl = parse<XpmVariableDeclaration>("block { declare \$a:x  as  node ( (::) )? := \$a:y; 2 }")[0]
                assertThat(decl.isExternal, `is`(false))
                assertThat(decl.variableType?.typeName, `is`("node()?"))
                assertThat(decl.variableExpression?.text, `is`("\$a:y"))

                val qname = decl.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.3) Assignment Expression")
    internal inner class AssignmentExpression {
        @Nested
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (158) AssignmentExpr")
        internal inner class AssignmentExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val binding = parse<ScriptingAssignmentExpr>("\$x := \$y")[0] as XpmVariableReference

                val qname = binding.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(3))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val binding = parse<ScriptingAssignmentExpr>("\$a:x := \$a:y")[0] as XpmVariableReference

                val qname = binding.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(5))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val binding = parse<ScriptingAssignmentExpr>(
                    "\$Q{http://www.example.com}x := \$Q{http://www.example.com}y"
                )[0] as XpmVariableReference

                val qname = binding.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(28))
            }
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.4) While Expression")
    internal inner class WhileExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (160) WhileExpr")
        fun whileExpr() {
            val expr = parse<ScriptingWhileExpr>("while (false()) { 1 }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.WHILE_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.5) Exit Expression")
    internal inner class ExitExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (159) ExitExpr")
        fun exitExpr() {
            val expr = parse<ScriptingExitExpr>("exit returning 1")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.EXIT_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (6.5) Concatenation Expression")
    internal inner class ConcatenationExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (33) ConcatExpr")
        fun concatExpr() {
            val expr = parse<ScriptingConcatExpr>("1, 2 + 3, 4")[0] as XpmConcatenatingExpression
            assertThat(expr.expressionElement, `is`(nullValue()))

            val exprs = expr.expressions.toList()
            assertThat(exprs.size, `is`(3))
            assertThat(exprs[0].text, `is`("1"))
            assertThat(exprs[1].text, `is`("2 + 3"))
            assertThat(exprs[2].text, `is`("4"))
        }
    }
}
