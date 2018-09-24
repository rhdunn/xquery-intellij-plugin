/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.roots

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

fun Project.sourceFolders(): Sequence<SourceFolder> {
    return ModuleManager.getInstance(this).modules.asSequence()
        .flatMap { module -> ModuleRootManager.getInstance(module).contentEntries.asSequence() }
        .flatMap { entry -> entry.sourceFolders.asSequence() }
}

fun SourceFolder.isRootOfFile(file: VirtualFile?): Boolean {
    if (file == null) return false
    return this.file?.equals(file) == true || isRootOfFile(file.parent)
}

fun VirtualFile.getSourceRootType(project: Project): JpsModuleSourceRootType<*> {
    var rootType: JpsModuleSourceRootType<*> = JavaSourceRootType.SOURCE
    project.sourceFolders().filter { folder -> folder.isRootOfFile(this) }.forEach { folder ->
        if (folder.rootType === JavaSourceRootType.TEST_SOURCE) {
            rootType = JavaSourceRootType.TEST_SOURCE
        }
    }
    return rootType
}
