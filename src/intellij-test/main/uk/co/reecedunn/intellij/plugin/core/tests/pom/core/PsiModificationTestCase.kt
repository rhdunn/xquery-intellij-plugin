// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.pom.core

import com.intellij.openapi.application.ApplicationManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.impl.source.codeStyle.IndentHelper
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.psi.codeStyles.MockIndentHelper
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.ProjectTestCase

interface PsiModificationTestCase : PluginDescriptorProvider, ProjectTestCase {
    fun requiresPsiElementReplace() {
        requiresPomModel()
        requiresChangeUtilCopyElement()
        requiresChangeEditUtilReplaceChild()
    }

    private fun requiresPomModel() {
        project.registerService(TreeAspect())
        project.registerService<PomModel>(MockPomModel(project))
    }

    private fun requiresChangeUtilCopyElement() {
        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
    }

    private fun requiresChangeEditUtilReplaceChild() {
        val app = ApplicationManager.getApplication()
        app.registerService<IndentHelper>(MockIndentHelper())
    }
}
