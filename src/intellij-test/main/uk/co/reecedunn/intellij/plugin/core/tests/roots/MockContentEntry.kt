/*
 * Copyright (C) 2018, 2022 Reece H. Dunn
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

import com.intellij.compat.openapi.roots.MockContentEntry
import com.intellij.openapi.roots.*
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.java.JavaSourceRootType

@Suppress("NonExtendableApiUsage")
class MockContentEntry(private val file: VirtualFile) : MockContentEntry() {
    override fun getSourceFolders(): Array<SourceFolder> {
        return arrayOf(MockSourceFolder(file, JavaSourceRootType.SOURCE))
    }
}
