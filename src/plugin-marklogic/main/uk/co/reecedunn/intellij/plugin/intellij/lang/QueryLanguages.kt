/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher
import com.intellij.openapi.fileTypes.FileNameMatcher
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageData
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicBundle

object ServerSideJavaScript : Language("MLJavaScript", "application/vnd.marklogic-javascript") {
    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = MarkLogicBundle.message("language.sjs.display-name")
}

val SPARQLQuery: Language by lazy {
    Language.findInstancesByMimeType("application/sparql-query").firstOrNull() ?: {
        val language = Language.findLanguageByID("SPARQLQuery") ?: object : Language("SPARQLQuery") {
            override fun getDisplayName(): String = "SPARQL Query"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("rq"),
                ExtensionFileNameMatcher("sparql")
            )

            override val mimeTypes: Array<String> = arrayOf("application/sparql-query")
        })
        language
    }()
}

val SPARQLUpdate: Language by lazy {
    Language.findInstancesByMimeType("application/sparql-update").firstOrNull() ?: {
        val language = Language.findLanguageByID("SPARQLUpdate") ?: object : Language("SPARQLUpdate") {
            override fun getDisplayName(): String = "SPARQL Update"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("ru")
            )

            override val mimeTypes: Array<String> = arrayOf("application/sparql-update")
        })
        language
    }()
}

val SQL: Language by lazy {
    Language.findInstancesByMimeType("application/sql").firstOrNull() ?: {
        val language = Language.findLanguageByID("SQL") ?: object : Language("SQL") {}
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("sql")
            )

            override val mimeTypes: Array<String> = arrayOf("application/sql")
        })
        language
    }()
}
