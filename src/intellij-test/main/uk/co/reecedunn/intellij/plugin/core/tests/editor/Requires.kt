// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.editor

import com.intellij.openapi.editor.EditorFactory
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetDocument
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresEditorFactory() {
    app.registerService<EditorFactory>(MockEditorFactoryEx())
}

fun PlatformTestCase.requiresPsiFileGetEditor() {
    requiresPsiFileGetDocument()
    requiresEditorFactory()
}
