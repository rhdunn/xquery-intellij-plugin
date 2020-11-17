/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.annotation

import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.ParserDefinition
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactoryBean
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.XsltShadowPsiElementFactory

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase(vararg definitions: ParserDefinition) :
    uk.co.reecedunn.intellij.plugin.core.tests.parser.AnnotatorTestCase<PsiFile>(null, *definitions),
    PluginDescriptorProvider {
    // region PluginDescriptorProvider

    override val pluginDescriptor: PluginDescriptor
        get() = DefaultPluginDescriptor(pluginId, this::class.java.classLoader)

    override val pluginDisposable: Disposable
        get() = testRootDisposable

    // endregion

    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        registerExtensionPoint(StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java)
        registerExtensionPoint(XmlExtension.EP_NAME, XmlExtension::class.java)

        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())
        myProject.registerService(ModuleManager::class.java, MockModuleManager(myProject))
        myProject.registerService(InjectedLanguageManager::class.java, MockInjectedLanguageManager())

        registerExtensionPoint(XpmShadowPsiElementFactory.EP_NAME, XpmShadowPsiElementFactoryBean::class.java)
        XpmShadowPsiElementFactory.register(this, XsltShadowPsiElementFactory)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }
}
