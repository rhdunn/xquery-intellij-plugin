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
package uk.co.reecedunn.intellij.plugin.core.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeManager

interface LanguageExtensions {
    val associations: List<FileNameMatcher>
}

fun Language.getAssociations(): List<FileNameMatcher> {
    val associations = associatedFileType?.let { FileTypeManager.getInstance().getAssociations(it) } ?: listOf()
    return if (associations.isEmpty() && this is LanguageExtensions)
        this.associations
    else
        associations
}
