// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.pom.core

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SchemeManagerFactory
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.codeStyle.AppCodeStyleSettingsManager
import com.intellij.psi.codeStyle.CodeStyleSchemes
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.FileCodeStyleProvider
import com.intellij.psi.codeStyle.FileIndentOptionsProvider
import com.intellij.psi.codeStyle.FileTypeIndentOptionsProvider
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.codeStyle.ProjectCodeStyleSettingsManager
import com.intellij.psi.codeStyle.modifier.CodeStyleSettingsModifier
import com.intellij.psi.impl.source.codeStyle.IndentHelper
import com.intellij.psi.impl.source.codeStyle.IndentHelperImpl
import com.intellij.psi.impl.source.codeStyle.PersistableCodeStyleSchemes
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import com.intellij.testFramework.MockSchemeManagerFactory
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.ProjectTestCase

interface PsiModificationTestCase : PluginDescriptorProvider, ProjectTestCase {
    fun registerPsiModification() {
        val app = ApplicationManager.getApplication()

        project.registerService(TreeAspect())
        project.registerService<PomModel>(MockPomModel(project))

        app.registerExtensionPointBean(
            FileIndentOptionsProvider.EP_NAME, FileIndentOptionsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileTypeIndentOptionsProvider.EP_NAME, FileTypeIndentOptionsProvider::class.java, pluginDisposable
        )

        app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
        app.registerService<IndentHelper>(IndentHelperImpl())

        registerCodeSettingsService()
        registerCodeStyleSettingsManager()

        project.registerService<InjectedLanguageManager>(MockInjectedLanguageManager())

        val schemeManagerFactory = MockSchemeManagerFactory()
        app.registerService<SchemeManagerFactory>(schemeManagerFactory)
        app.registerService<CodeStyleSchemes>(PersistableCodeStyleSchemes(schemeManagerFactory))
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerCodeSettingsService() {
        try {
            val service = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsService") as Class<Any>
            val `class` = Class.forName("com.intellij.psi.codeStyle.CodeStyleSettingsServiceImpl") as Class<Any>
            val `object` = `class`.getConstructor().newInstance()
            ApplicationManager.getApplication().registerService(service, `object`)
        } catch (e: ClassNotFoundException) {
        }
    }

    private fun registerCodeStyleSettingsManager() {
        val app = ApplicationManager.getApplication()

        app.registerExtensionPointBean(
            CodeStyleSettingsProvider.EXTENSION_POINT_NAME, CodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            "com.intellij.langCodeStyleSettingsContributor",
            "com.intellij.psi.codeStyle.LanguageCodeStyleSettingsContributor",
            pluginDisposable
        )
        app.registerExtensionPointBean(
            LanguageCodeStyleSettingsProvider.EP_NAME, LanguageCodeStyleSettingsProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(
            FileCodeStyleProvider.EP_NAME, FileCodeStyleProvider::class.java, pluginDisposable
        )

        app.registerService(AppCodeStyleSettingsManager())
        project.registerService(ProjectCodeStyleSettingsManager(project))

        @Suppress("UnstableApiUsage")
        app.registerExtensionPointBean(
            CodeStyleSettingsModifier.EP_NAME, CodeStyleSettingsModifier::class.java, pluginDisposable
        )
    }
}
