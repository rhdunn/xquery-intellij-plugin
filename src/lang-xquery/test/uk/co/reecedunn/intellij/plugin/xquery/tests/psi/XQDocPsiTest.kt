// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("xqDoc - IntelliJ Program Structure Interface (PSI)")
class XQDocPsiTest : IdeaPlatformTestCase(), LanguageTestCase {
    override val pluginId: PluginId = PluginId.getId("XQDocPsiTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    @Nested
    @DisplayName("Comment")
    internal inner class Comment {
        @Test
        @DisplayName("unclosed comment; no text")
        fun unclosed() {
            val psi = parse<XPathComment>("(:")[0]
            assertThat(psi.isXQDoc, `is`(false))
        }

        @Test
        @DisplayName("without xqDoc marker")
        fun noXQDocMarker() {
            val psi = parse<XPathComment>("(: Lorem ipsum. :)")[0]
            assertThat(psi.isXQDoc, `is`(false))
        }

        @Test
        @DisplayName("with xqDoc marker")
        fun xqDocMarker() {
            val psi = parse<XPathComment>("(:~ Lorem ipsum. :)")[0]
            assertThat(psi.isXQDoc, `is`(true))
        }
    }
}
