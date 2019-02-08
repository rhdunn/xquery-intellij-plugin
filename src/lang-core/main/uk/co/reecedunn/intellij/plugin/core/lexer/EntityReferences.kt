/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.lexer

import com.google.gson.JsonParser
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import java.io.InputStreamReader

enum class EntityReferenceType {
    EmptyEntityReference,
    PartialEntityReference,
    CharacterReference,
    PredefinedEntityReference,
    XmlEntityReference,
    Html4EntityReference,
    Html5EntityReference
}

@Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
private fun Int.isHexDigit(): Boolean {
    return (
        this >= '0'.toInt() && this <= '9'.toInt() ||
        this >= 'a'.toInt() && this <= 'f'.toInt() ||
        this >= 'A'.toInt() && this <= 'F'.toInt()
    )
}

fun CodePointRange.matchEntityReference(): EntityReferenceType {
    match()
    var cc = CharacterClass.getCharClass(codePoint)
    return when (cc) {
        CharacterClass.NAME_START_CHAR -> {
            match()
            cc = CharacterClass.getCharClass(codePoint)
            while (cc == CharacterClass.NAME_START_CHAR || cc == CharacterClass.DIGIT) {
                match()
                cc = CharacterClass.getCharClass(codePoint)
            }
            if (cc == CharacterClass.SEMICOLON) {
                match()
                EntityReferenceType.PredefinedEntityReference
            } else {
                EntityReferenceType.PartialEntityReference
            }
        }
        CharacterClass.HASH -> {
            match()
            var c = codePoint
            when {
                c == 'x'.toInt() -> {
                    match()
                    c = codePoint
                    when {
                        c.isHexDigit() -> {
                            while (c.isHexDigit()) {
                                match()
                                c = codePoint
                            }
                            if (c == ';'.toInt()) {
                                match()
                                EntityReferenceType.CharacterReference
                            } else {
                                EntityReferenceType.PartialEntityReference
                            }
                        }
                        c == ';'.toInt() -> {
                            match()
                            EntityReferenceType.EmptyEntityReference
                        }
                        else -> EntityReferenceType.PartialEntityReference
                    }
                }
                c >= '0'.toInt() && c <= '9'.toInt() -> {
                    match()
                    while (c >= '0'.toInt() && c <= '9'.toInt()) {
                        match()
                        c = codePoint
                    }
                    if (c == ';'.toInt()) {
                        match()
                        EntityReferenceType.CharacterReference
                    } else {
                        EntityReferenceType.PartialEntityReference
                    }
                }
                c == ';'.toInt() -> {
                    match()
                    EntityReferenceType.EmptyEntityReference
                }
                else -> EntityReferenceType.PartialEntityReference
            }
        }
        CharacterClass.SEMICOLON -> {
            match()
            EntityReferenceType.EmptyEntityReference
        }
        else -> EntityReferenceType.PartialEntityReference
    }
}

data class EntityRef(val name: CharSequence, val value: CharSequence, val type: EntityReferenceType)

fun CharSequence.entityReferenceCodePoint(): Int {
    return when {
        startsWith("&#x") -> { // `&#x...;` hexadecimal character reference
            subSequence(3, length - 1).toString().toInt(radix = 16)
        }
        startsWith("&#") -> { // `&#...;` decimal character reference
            subSequence(2, length - 1).toString().toInt(radix = 10)
        }
        else -> 0xFFFE
    }
}

private fun loadPredefinedEntities(entities: HashMap<String, EntityRef>, path: String, type: EntityReferenceType) {
    val file = ResourceVirtualFile(EntityRef::class.java.classLoader, path)
    val data = JsonParser().parse(InputStreamReader(file.inputStream!!)).asJsonObject
    data.entrySet().forEach { entity ->
        val chars = entity.value.asJsonObject.get("characters").asString
        entities.putIfAbsent(entity.key, EntityRef(entity.key, chars, type))
    }
}

var ENTITIES: HashMap<String, EntityRef>? = null
    get() {
        if (field == null) {
            field = HashMap()
            // Dynamically load the predefined entities on their first use. This loads the entities:
            //     XML ⊂ HTML 4 ⊂ HTML 5
            // ensuring that the subset type is reported correctly.
            loadPredefinedEntities(field!!, "predefined-entities/xml.json", EntityReferenceType.XmlEntityReference)
            loadPredefinedEntities(field!!, "predefined-entities/html4.json", EntityReferenceType.Html4EntityReference)
            loadPredefinedEntities(field!!, "predefined-entities/html5.json", EntityReferenceType.Html5EntityReference)
        }
        return field
    }
