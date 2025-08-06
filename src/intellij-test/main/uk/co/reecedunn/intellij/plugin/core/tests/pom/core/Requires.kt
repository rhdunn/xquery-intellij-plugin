// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.pom.core

import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.impl.source.codeStyle.IndentHelper
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.psi.codeStyles.MockIndentHelper
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase

fun IdeaPlatformTestCase.requiresPsiElementReplace() {
    requiresPomModel()
    requiresChangeUtilCopyElement()
    requiresChangeEditUtilReplaceChild()
}

private fun IdeaPlatformTestCase.requiresPomModel() {
    project.registerService(TreeAspect())
    project.registerService<PomModel>(MockPomModel(project))
}

private fun IdeaPlatformTestCase.requiresChangeUtilCopyElement() {
    app.registerExtensionPointBean(TreeCopyHandler.EP_NAME, TreeCopyHandler::class.java, pluginDisposable)
}

private fun IdeaPlatformTestCase.requiresChangeEditUtilReplaceChild() {
    app.registerService<IndentHelper>(MockIndentHelper())
}
