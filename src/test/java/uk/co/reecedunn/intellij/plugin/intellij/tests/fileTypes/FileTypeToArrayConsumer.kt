/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.tests.fileTypes

import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.util.Pair
import org.jetbrains.annotations.NonNls
import java.util.*

internal class FileTypeToArrayConsumer : FileTypeConsumer {
    val fileTypes: MutableList<Pair<FileType, String>> = ArrayList()
    val fileMatchers: MutableList<Pair<FileType, FileNameMatcher>> = ArrayList()

    override fun consume(fileType: FileType) {
        fileTypes.add(Pair.create(fileType, null))
    }

    override fun consume(fileType: FileType, @NonNls extensions: String) {
        fileTypes.add(Pair.create(fileType, extensions))
    }

    override fun consume(fileType: FileType, vararg matchers: FileNameMatcher) {
        for (matcher in matchers) {
            fileMatchers.add(Pair.create(fileType, matcher))
        }
    }

    override fun getStandardFileTypeByName(@NonNls name: String): FileType? {
        return null
    }
}
