// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.lang.highlighter

import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.xml.XmlExtension
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.XsltShadowPsiElementFactory

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
abstract class AnnotatorTestCase(language: Language) : ParsingTestCase<PsiFile>(language) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XmlASTFactory().registerExtension(project, XMLLanguage.INSTANCE)

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)

        project.registerService<ProjectRootManager>(MockProjectRootsManager())
        project.registerService<ModuleManager>(MockModuleManager(mockProject))
        project.registerService<InjectedLanguageManager>(MockInjectedLanguageManager())

        XpmShadowPsiElementFactory.register(this, XsltShadowPsiElementFactory)
    }
}
