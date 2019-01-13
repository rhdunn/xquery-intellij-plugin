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
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageAssociations

val N3: Language by lazy {
    Language.findInstancesByMimeType("text/n3").firstOrNull() ?: {
        val language = object : Language("N3", "text/n3") {
            override fun getDisplayName(): String = "N3"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("n3")
            )
        })
        language
    }()
}

val NQuads: Language by lazy {
    Language.findInstancesByMimeType("application/n-quads").firstOrNull() ?: {
        val language = object : Language("NQuads", "application/n-quads") {
            override fun getDisplayName(): String = "N-Quads"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("nq")
            )
        })
        language
    }()
}

val NTriples: Language by lazy {
    Language.findInstancesByMimeType("application/n-triples").firstOrNull() ?: {
        val language = object : Language("NTriples", "application/n-triples") {
            override fun getDisplayName(): String = "N-Triples"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("nt")
            )
        })
        language
    }()
}

val RdfJson: Language by lazy {
    Language.findInstancesByMimeType("application/rdf+json").firstOrNull() ?: {
        val language = object : Language("RDFJSON", "application/rdf+json") {
            override fun getDisplayName(): String = "RDF/JSON"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("rj")
            )
        })
        language
    }()
}

val RdfXml: Language by lazy {
    Language.findInstancesByMimeType("application/rdf+xml").firstOrNull() ?: {
        val language = object : Language("RDFXML", "application/rdf+xml") {
            override fun getDisplayName(): String = "RDF/XML"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("rdf")
            )
        })
        language
    }()
}

val TriG: Language by lazy {
    Language.findInstancesByMimeType("application/trig").firstOrNull() ?: {
        val language = object : Language("TriG", "application/trig") {
            override fun getDisplayName(): String = "TriG"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("trig")
            )
        })
        language
    }()
}

val Turtle: Language by lazy {
    Language.findInstancesByMimeType("text/turtle").firstOrNull() ?: {
        val language = object : Language("Turtle", "text/turtle") {
            override fun getDisplayName(): String = "Turtle"
        }
        language.putUserData(LanguageAssociations.KEY, object : LanguageAssociations {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("ttl")
            )
        })
        language
    }()
}

val RDF_FORMATS: List<Language> by lazy {
    listOf(N3, NQuads, NTriples, RdfJson, RdfXml, TriG, Turtle)
}
