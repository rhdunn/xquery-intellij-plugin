/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.lang.highlighter

import com.intellij.compat.testFramework.registerExtensionPointBean
import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.ParserDefinition
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.XsltShadowPsiElementFactory

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase(vararg definitions: ParserDefinition) :
    uk.co.reecedunn.intellij.plugin.core.tests.parser.AnnotatorTestCase<PsiFile>(null, *definitions) {

    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)

        project.registerServiceInstance(ProjectRootManager::class.java, MockProjectRootsManager())
        project.registerServiceInstance(ModuleManager::class.java, MockModuleManager(project))
        project.registerServiceInstance(InjectedLanguageManager::class.java, MockInjectedLanguageManager())

        XpmShadowPsiElementFactory.register(this, XsltShadowPsiElementFactory)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }
}
