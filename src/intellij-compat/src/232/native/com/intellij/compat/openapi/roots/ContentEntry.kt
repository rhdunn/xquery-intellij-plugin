// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.roots

import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ExcludeFolder
import com.intellij.openapi.roots.ProjectModelExternalSource
import com.intellij.openapi.roots.SourceFolder
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

@Suppress("NonExtendableApiUsage", "UnstableApiUsage")
interface ContentEntry : ContentEntry {
    override fun <P : JpsElement?> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        externalSource: ProjectModelExternalSource
    ): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        useSourceOfContentRoot: Boolean
    ): SourceFolder = TODO()

    override fun <P : JpsElement> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P,
        externalSource: ProjectModelExternalSource?
    ): SourceFolder = TODO()

    override fun <P : JpsElement> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P,
        isAutomaticallyImported: Boolean
    ): SourceFolder = TODO()

    override fun addExcludeFolder(url: String, isAutomaticallyImported: Boolean): ExcludeFolder = TODO()
}