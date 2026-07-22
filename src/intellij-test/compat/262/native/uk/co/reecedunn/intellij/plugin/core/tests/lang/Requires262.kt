// Copyright (C) 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.MetaLanguage
import com.intellij.lang.MetaLanguageProvider
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

@Suppress("UnstableApiUsage", "DEPRECATION")
fun PlatformTestCase.requiresMetaLanguage() {
    app.registerExtensionPointBean(MetaLanguage.PROVIDER_EP_NAME, MetaLanguageProvider::class.java, pluginDisposable)
    app.registerExtensionPointBean(MetaLanguage.EP_NAME, MetaLanguage::class.java, pluginDisposable)
}
