/*
 * Copyright (C) 2019-2020 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.compat.testFramework

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.impl.DirectoryIndex
import com.intellij.openapi.roots.impl.DirectoryIndexImpl
import com.intellij.openapi.roots.impl.ProjectFileIndexImpl
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileBasedIndexImpl
import org.picocontainer.MutablePicoContainer

fun MutablePicoContainer.registerProgressManager() {
    val component = getComponentAdapter(ProgressManager::class.java.name)
    if (component == null) {
        registerComponentInstance(ProgressManager::class.java.name, ProgressManagerImpl())
    }
}

@Suppress("UnstableApiUsage")
fun PlatformLiteFixture.registerFileBasedIndex() {
    registerExtensionPoint(FileBasedIndexExtension.EXTENSION_POINT_NAME, FileBasedIndexExtension::class.java)
    registerProjectService(DirectoryIndex::class.java, DirectoryIndexImpl(project))
    registerProjectService(ProjectFileIndex::class.java, ProjectFileIndexImpl(project))
    registerApplicationService(FileBasedIndex::class.java, FileBasedIndexImpl())
}
