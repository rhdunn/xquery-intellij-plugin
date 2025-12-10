// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.syntax

import com.intellij.lang.MetaLanguage
import com.intellij.platform.syntax.psi.PsiSyntaxBuilderFactory
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresPsiSyntaxBuilderFactory() {
    app.registerExtensionPointBean(MetaLanguage.EP_NAME, MetaLanguage::class.java, pluginDisposable)

    app.registerService(PsiSyntaxBuilderFactory.defaultBuilderFactory())
}
