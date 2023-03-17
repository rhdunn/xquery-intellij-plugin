// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.startup

import com.intellij.ide.startup.impl.StartupManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UnstableApiUsage")
fun StartupManager(project: Project): StartupManager = StartupManagerImpl(project, coroutineScope = GlobalScope)
