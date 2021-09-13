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
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRoot
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

@Suppress("NonExtendableApiUsage")
class MockSourceFolder(private val file: VirtualFile, private val rootType: JpsModuleSourceRootType<*>) : SourceFolder {
    override fun getUrl(): String = TODO()

    override fun getFile(): VirtualFile = file

    override fun getPackagePrefix(): String = TODO()

    override fun isTestSource(): Boolean = rootType === JavaSourceRootType.SOURCE

    override fun getContentEntry(): ContentEntry = TODO()

    override fun isSynthetic(): Boolean = TODO()

    override fun setPackagePrefix(packagePrefix: String): Unit = TODO()

    override fun getRootType(): JpsModuleSourceRootType<*> = rootType

    override fun getJpsElement(): JpsModuleSourceRoot = TODO()

    @Suppress("UnstableApiUsage")
    override fun <P : JpsElement?> changeType(newType: JpsModuleSourceRootType<P>?, properties: P): Unit = TODO()
}
