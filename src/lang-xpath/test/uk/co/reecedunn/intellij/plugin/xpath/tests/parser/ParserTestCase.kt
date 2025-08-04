// Copyright (C) 2018-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProviderBean

abstract class ParserTestCase : ParsingTestCase<PsiFile>(XPath) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        project.registerService<ModuleManager>(MockModuleManager(mockProject))

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            XpmFunctionProvider.EP_NAME, XpmFunctionProviderBean::class.java, pluginDisposable
        )
    }
}
