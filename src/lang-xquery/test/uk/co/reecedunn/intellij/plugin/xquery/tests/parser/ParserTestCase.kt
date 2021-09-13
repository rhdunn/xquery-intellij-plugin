/*
 * Copyright (C) 2016-2021 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.application.options.codeStyle.cache.CodeStyleCachingService
import com.intellij.application.options.codeStyle.cache.CodeStyleCachingServiceImpl
import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
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
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

abstract class ParserTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()) {

    open fun registerModules(manager: MockModuleManager) {}

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerServiceInstance(TreeAspect::class.java, TreeAspect())
        project.registerServiceInstance(PomModel::class.java, MockPomModel(project))
        registerPsiModification()

        project.registerServiceInstance(CodeStyleCachingService::class.java, CodeStyleCachingServiceImpl())

        project.registerServiceInstance(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())
        project.registerServiceInstance(ProjectRootManager::class.java, MockProjectRootsManager())

        val manager = MockModuleManager(project)
        registerModules(manager)
        project.registerServiceInstance(ModuleManager::class.java, manager)

        project.registerServiceInstance(JavaTypePath::class.java, JavaTypePath(project))
        project.registerServiceInstance(XpmModuleLoaderSettings::class.java, XpmModuleLoaderSettings(project))

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
    }

    protected val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(project)
}
