// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.xdebugger.frame

import javax.swing.Icon

interface XValueNode : com.intellij.xdebugger.frame.XValueNode {
    @Suppress("OVERRIDE_DEPRECATION", "UnstableApiUsage", "removal")
    override fun setPresentation(icon: Icon?, type: String?, separator: String, value: String?, hasChildren: Boolean) {
        TODO("Don't call this deprecated API.")
    }
}
