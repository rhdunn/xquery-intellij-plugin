// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.vfs

import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import com.intellij.openapi.vfs.encoding.EncodingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.editor.requiresEditorFactory
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresVirtualFileGetCharset() {
    requiresEditorFactory()
    requiresEncodingManager()
}

private fun PlatformTestCase.requiresEncodingManager() {
    app.registerService<EncodingManager>(EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
}
