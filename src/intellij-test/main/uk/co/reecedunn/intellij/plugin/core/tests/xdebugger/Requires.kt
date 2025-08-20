// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.xdebugger

import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

@Suppress("UnstableApiUsage")
fun PlatformTestCase.requiresXDebuggerUtilCreatePosition() {
    app.registerService<XDebuggerUtil>(XDebuggerUtilImpl())
}
