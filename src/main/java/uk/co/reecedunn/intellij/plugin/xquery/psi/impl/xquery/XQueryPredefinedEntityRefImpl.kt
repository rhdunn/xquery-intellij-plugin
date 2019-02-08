/*
 * Copyright (C) 2016-2017, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.google.gson.JsonParser
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.EntityRef
import uk.co.reecedunn.intellij.plugin.core.lexer.EntityReferenceType
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import java.io.InputStreamReader

private fun loadPredefinedEntities(entities: HashMap<String, EntityRef>, path: String, type: EntityReferenceType) {
    val file = ResourceVirtualFile(XQueryPredefinedEntityRefImpl::class.java.classLoader, path)
    val data = JsonParser().parse(InputStreamReader(file.inputStream!!)).asJsonObject
    data.entrySet().forEach { entity ->
        val chars = entity.value.asJsonObject.get("characters").asString
        entities.putIfAbsent(entity.key, EntityRef(entity.key, chars, type))
    }
}

private var ENTITIES: HashMap<String, EntityRef>? = null
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

class XQueryPredefinedEntityRefImpl(type: IElementType, text: CharSequence) : LeafPsiElement(type, text), XQueryPredefinedEntityRef {
    override val entityRef
        get(): EntityRef {
            val entity = node.chars
            return ENTITIES!![entity] ?: EntityRef(entity, entity, EntityReferenceType.PredefinedEntityReference)
        }
}
