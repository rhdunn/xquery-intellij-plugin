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
import com.intellij.openapi.util.Key

interface LanguageAssociations {
    companion object {
        val KEY = Key.create<LanguageAssociations>("uk.co.reecedunn.intellij.plugin.key.languageAssociations")
    }

    val associations: List<FileNameMatcher>
}

fun Language.getAssociations(): List<FileNameMatcher> {
    val associations = associatedFileType?.let { FileTypeManager.getInstance().getAssociations(it) } ?: listOf()
    return if (associations.isEmpty())
        this.getUserData(LanguageAssociations.KEY)?.associations ?: listOf()
    else
        associations
}

fun Array<out Language>.getAssociations(): List<FileNameMatcher> {
    return asSequence().flatMap { language -> language.getAssociations().asSequence() }.toList()
}

fun Array<out Language>.findByAssociations(path: String): Language? {
    return find { language ->
        language.getAssociations().find { association ->
            association.accept(path)
        } != null
    }
}

fun Language.getLanguageMimeTypes(): Array<String> {
    return mimeTypes
}
