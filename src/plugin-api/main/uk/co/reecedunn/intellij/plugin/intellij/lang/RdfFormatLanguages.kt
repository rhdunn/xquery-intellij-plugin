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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher
import com.intellij.openapi.fileTypes.FileNameMatcher
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageData

val N3: Language by lazy {
    Language.findInstancesByMimeType("text/n3").firstOrNull() ?: {
        val language = Language.findLanguageByID("N3") ?: object : Language("N3") {}
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("n3")
            )

            override val mimeTypes: Array<String> = arrayOf("text/n3")
        })
        language
    }()
}

val NQuads: Language by lazy {
    Language.findInstancesByMimeType("application/n-quads").firstOrNull() ?: {
        val language = Language.findLanguageByID("NQuads") ?: object : Language("NQuads") {
            override fun getDisplayName(): String = "N-Quads"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("nq")
            )

            override val mimeTypes: Array<String> = arrayOf("application/n-quads")
        })
        language
    }()
}

val NTriples: Language by lazy {
    Language.findInstancesByMimeType("application/n-triples").firstOrNull() ?: {
        val language = Language.findLanguageByID("NTriples") ?: object : Language("NTriples") {
            override fun getDisplayName(): String = "N-Triples"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("nt")
            )

            override val mimeTypes: Array<String> = arrayOf("application/n-triples")
        })
        language
    }()
}

val RdfJson: Language by lazy {
    Language.findInstancesByMimeType("application/rdf+json").firstOrNull() ?: {
        val language = Language.findLanguageByID("RDFJSON") ?: object : Language("RDFJSON") {
            override fun getDisplayName(): String = "RDF/JSON"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("rj")
            )

            override val mimeTypes: Array<String> = arrayOf("application/rdf+json")
        })
        language
    }()
}

val RdfXml: Language by lazy {
    Language.findInstancesByMimeType("application/rdf+xml").firstOrNull() ?: {
        val language = Language.findLanguageByID("RDFXML") ?: object : Language("RDFXML") {
            override fun getDisplayName(): String = "RDF/XML"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("rdf")
            )

            override val mimeTypes: Array<String> = arrayOf("application/rdf+xml")
        })
        language
    }()
}

val TriG: Language by lazy {
    Language.findInstancesByMimeType("application/trig").firstOrNull() ?: {
        val language = Language.findLanguageByID("TriG") ?: object : Language("TriG") {
            override fun getDisplayName(): String = "TriG"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("trig")
            )

            override val mimeTypes: Array<String> = arrayOf("application/trig")
        })
        language
    }()
}

val TripleXml: Language by lazy {
    Language.findInstancesByMimeType("application/vnd.marklogic.triples+xml").firstOrNull() ?: {
        val language = Language.findLanguageByID("TripleXml") ?: object : Language("TripleXml") {
            override fun getDisplayName(): String = "MarkLogic Triple/XML"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("xml")
            )

            override val mimeTypes: Array<String> = arrayOf("application/vnd.marklogic.triples+xml")
        })
        language
    }()
}

val Turtle: Language by lazy {
    Language.findInstancesByMimeType("text/turtle").firstOrNull() ?: {
        val language = Language.findLanguageByID("Turtle") ?: object : Language("Turtle") {
            override fun getDisplayName(): String = "Turtle"
        }
        language.putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("ttl")
            )

            override val mimeTypes: Array<String> = arrayOf("text/turtle")
        })
        language
    }()
}

val RDF_FORMATS: List<Language> by lazy {
    listOf(N3, NQuads, NTriples, RdfJson, RdfXml, TriG, TripleXml, Turtle)
}
