// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.pom.core

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.impl.source.codeStyle.IndentHelper
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.injecton.MockInjectedLanguageManager
import uk.co.reecedunn.intellij.plugin.core.tests.psi.codeStyles.MockIndentHelper
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.ProjectTestCase

interface PsiModificationTestCase : PluginDescriptorProvider, ProjectTestCase {
    fun registerPsiModification() {
        val app = ApplicationManager.getApplication()

        project.registerService(TreeAspect())
        project.registerService<PomModel>(MockPomModel(project))

        app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
        app.registerService<IndentHelper>(MockIndentHelper())

        project.registerService<InjectedLanguageManager>(MockInjectedLanguageManager())
    }
}
