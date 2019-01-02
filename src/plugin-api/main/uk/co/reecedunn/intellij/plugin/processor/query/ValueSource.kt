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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import uk.co.reecedunn.intellij.plugin.core.io.decode
import java.io.File

enum class ValueSourceType {
    LocalFile,
    DatabaseFile,
    Text
}

data class ValueSource(
    var type: ValueSourceType,
    var data: String
) {
    val value: String?
        get() {
            return when (type) {
                ValueSourceType.LocalFile -> {
                    val url = VfsUtil.pathToUrl(data.replace(File.separatorChar, '/'))
                    val file = url.let { VirtualFileManager.getInstance().findFileByUrl(it) }
                    file?.inputStream?.decode(file.charset)
                }
                else -> data
            }
        }
}

fun LocalFileSource(path: String) = ValueSource(ValueSourceType.LocalFile, path)

fun DatabaseFileSource(path: String) = ValueSource(ValueSourceType.DatabaseFile, path)

fun TextSource(value: String) = ValueSource(ValueSourceType.Text, value)
