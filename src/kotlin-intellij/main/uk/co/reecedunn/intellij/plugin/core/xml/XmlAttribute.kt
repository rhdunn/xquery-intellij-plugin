/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

val XmlAttribute.schemaType: String?
    get() {
        val tag = descriptor?.declaration as? XmlTag
        return when (tag?.namespace) {
            "http://www.w3.org/2001/XMLSchema" -> tag.getAttributeValue("type")
            "http://relaxng.org/ns/structure/1.0" -> tag.subTags.mapNotNull {
                when (it.localName) {
                    "data" -> it.getAttributeValue("type")
                    "ref" -> it.getAttributeValue("name")
                    else -> null
                }
            }.firstOrNull()
            else -> null
        }
    }
