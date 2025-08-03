// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

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
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.pom.core.MockPomModel
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.java.JavaTypePath
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.JspModuleSourceRootLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.RelativeModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecoratorBean
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

abstract class ParserTestCase :
    ParsingTestCase<XQueryModule>(XQuery) {

    open fun registerModules(manager: MockModuleManager) {}

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerService(TreeAspect::class.java, TreeAspect())
        project.registerService(PomModel::class.java, MockPomModel(project))
        registerPsiModification()

        project.registerService(CodeStyleCachingService::class.java, CodeStyleCachingService(project))

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(ProjectRootManager::class.java, MockProjectRootsManager())

        val manager = MockModuleManager(mockProject)
        registerModules(manager)
        project.registerService(ModuleManager::class.java, manager)

        project.registerService(JavaTypePath::class.java, JavaTypePath(project))
        project.registerService(XpmModuleLoaderSettings::class.java, XpmModuleLoaderSettings(project))

        project.registerService(XQueryProjectSettings())

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")

        XpmModuleLoaderFactory.register(this, "module", JspModuleSourceRootLoader, "")
        XpmModuleLoaderFactory.register(this, "relative", RelativeModuleLoader)

        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.basex.model.BuiltInFunctions)
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.marklogic.model.BuiltInFunctions)
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.saxon.model.BuiltInFunctions)
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            XpmFunctionDecorator.EP_NAME, XpmFunctionDecoratorBean::class.java, pluginDisposable
        )

        project.registerService(InjectedLanguageManager::class.java, MockInjectedLanguageManager())
    }

    protected val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(project)
}
