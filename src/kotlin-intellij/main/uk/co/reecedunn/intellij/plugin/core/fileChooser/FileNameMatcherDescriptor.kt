/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.fileChooser

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileElement
import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.vfs.VirtualFile

class FileNameMatcherDescriptor(val associations: List<FileNameMatcher>) :
    FileChooserDescriptor(true, false, false, true, false, false) {

    override fun isFileVisible(file: VirtualFile?, showHiddenFiles: Boolean): Boolean {
        if (!showHiddenFiles && FileElement.isFileHidden(file)) {
            return false
        }

        if (file!!.isDirectory) {
            return true
        }

        val name = file.name
        return associations.find { association -> association.accept(name) } != null
    }

    override fun isFileSelectable(file: VirtualFile?): Boolean = !file!!.isDirectory && isFileVisible(file, true)
}
