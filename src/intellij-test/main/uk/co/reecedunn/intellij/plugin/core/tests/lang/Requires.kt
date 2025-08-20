// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresIFileElementTypeParseContents() {
    app.registerService<PsiBuilderFactory>(PsiBuilderFactoryImpl())
}
