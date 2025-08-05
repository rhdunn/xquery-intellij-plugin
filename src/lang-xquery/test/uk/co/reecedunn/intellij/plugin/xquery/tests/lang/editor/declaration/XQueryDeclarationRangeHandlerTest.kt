// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.editor.declaration

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
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.parser.parse
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.editor.declaration.XQueryFunctionDeclRangeHandler
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Declaration Range Handler")
class XQueryDeclarationRangeHandlerTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryDeclarationRangeHandlerTest")
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
    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("external")
        fun external() {
            val decl =  parse<XQueryFunctionDecl>(" (::) declare function fn:true() external;")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range, `is`(nullValue()))
        }

        @Test
        @DisplayName("missing FunctionBody")
        fun missingBody() {
            val decl = parse<XQueryFunctionDecl>(" (::) declare function fn:true()")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range, `is`(nullValue()))
        }

        @Test
        @DisplayName("with FunctionBody")
        fun functionBody() {
            val decl = parse<XQueryFunctionDecl>(" (::) declare function fn:true() {};")[0]
            val range = XQueryFunctionDeclRangeHandler().getDeclarationRange(decl)
            assertThat(range?.startOffset, `is`(6))
            assertThat(range?.endOffset, `is`(33))
        }
    }
}
