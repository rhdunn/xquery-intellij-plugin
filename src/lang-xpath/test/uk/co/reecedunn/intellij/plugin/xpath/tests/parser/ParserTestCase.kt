/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.application.options.codeStyle.cache.CodeStyleCachingService
import com.intellij.application.options.codeStyle.cache.CodeStyleCachingServiceImpl
import com.intellij.compat.testFramework.MockPomModelImpl
import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProviderBean

abstract class ParserTestCase : ParsingTestCase<PsiFile>(null, XPathParserDefinition()) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        project.registerServiceInstance(TreeAspect::class.java, TreeAspect())
        project.registerServiceInstance(PomModel::class.java, MockPomModelImpl(project))
        registerPsiModification()

        project.registerServiceInstance(CodeStyleCachingService::class.java, CodeStyleCachingServiceImpl())

        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        project.registerServiceInstance(ProjectRootManager::class.java, MockProjectRootsManager())
        project.registerServiceInstance(ModuleManager::class.java, MockModuleManager(project))

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            XpmFunctionProvider.EP_NAME, XpmFunctionProviderBean::class.java, pluginDisposable
        )
    }
}
