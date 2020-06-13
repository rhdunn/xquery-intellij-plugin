/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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

import com.intellij.compat.testFramework.registerCodeStyleCachingService
import com.intellij.compat.testFramework.registerPomModel
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpm.java.JavaTypePath
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactoryBean
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()) {

    open fun registerModules(manager: MockModuleManager) {}

    @BeforeAll
    override fun setUp() {
        super.setUp()
        registerPomModel(myProject)
        registerPsiModification()
        myProject.registerCodeStyleCachingService()

        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())
        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())

        val manager = MockModuleManager(myProject)
        registerModules(manager)
        myProject.registerService(ModuleManager::class.java, manager)

        myProject.registerService(JavaTypePath::class.java, JavaTypePath(myProject))
        myProject.registerService(XpmModuleLoaderSettings::class.java, XpmModuleLoaderSettings(myProject))

        registerExtensionPoint(XpmModulePathFactory.EP_NAME, XpmModulePathFactory::class.java)
        registerModulePathFactory(XpmModuleLocationPath)

        registerExtensionPoint(XpmModuleLoaderFactory.EP_NAME, XpmModuleLoaderFactoryBean::class.java)
        registerModuleLoader("module", "uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.JspModuleSourceRootLoader\$Companion")
        registerModuleLoader("relative", "uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.RelativeModuleLoader")

        registerExtensionPoint(ImportPathResolver.EP_NAME, ImportPathResolver::class.java)
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.basex.model.BuiltInFunctions)
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.marklogic.model.BuiltInFunctions)
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.saxon.model.BuiltInFunctions)
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)

        registerExtensionPoint(XpmFunctionDecorator.EP_NAME, XpmFunctionDecorator::class.java)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun registerModulePathFactory(factory: XpmModulePathFactory) {
        registerExtension(XpmModulePathFactory.EP_NAME, factory)
    }

    private fun registerModuleLoader(name: String, implementation: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmModuleLoaderFactoryBean()
        bean.name = name
        bean.implementation = implementation
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerModuleLoader"), classLoader))
        registerExtension(XpmModuleLoaderFactory.EP_NAME, bean)
    }

    private fun registerBuiltInFunctions(resolver: ImportPathResolver) {
        registerExtension(ImportPathResolver.EP_NAME, resolver)
    }

    protected val settings get(): XQueryProjectSettings = XQueryProjectSettings.getInstance(myProject)
}
