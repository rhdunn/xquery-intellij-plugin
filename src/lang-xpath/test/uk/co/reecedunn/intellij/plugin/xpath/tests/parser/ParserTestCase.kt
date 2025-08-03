// Copyright (C) 2018-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.application.options.codeStyle.cache.CodeStyleCachingService
import com.intellij.compat.application.options.codeStyle.cache.CodeStyleCachingService
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.pom.core.MockPomModel
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.fileTypes.XPathFileType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProviderBean

abstract class ParserTestCase : ParsingTestCase<PsiFile>(XPath) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerService(TreeAspect::class.java, TreeAspect())
        project.registerService(PomModel::class.java, MockPomModel(project))
        registerPsiModification()

        project.registerService(CodeStyleCachingService::class.java, CodeStyleCachingService(project))

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)
        XPathFileType.registerFileType()

        project.registerService(ProjectRootManager::class.java, MockProjectRootsManager())
        project.registerService(ModuleManager::class.java, MockModuleManager(mockProject))

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            XpmFunctionProvider.EP_NAME, XpmFunctionProviderBean::class.java, pluginDisposable
        )

        project.registerService(InjectedLanguageManager::class.java, MockInjectedLanguageManager())
    }
}
