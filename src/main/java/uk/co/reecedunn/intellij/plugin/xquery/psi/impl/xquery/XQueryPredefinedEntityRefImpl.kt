/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEntityRefType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryPredefinedEntityRef
import uk.co.reecedunn.intellij.plugin.xquery.resources.Resources
import java.io.InputStreamReader

private fun loadPredefinedEntities(entities: HashMap<String, XQueryEntityRef>, path: String, type: XQueryEntityRefType) {
    val data = JsonParser().parse(InputStreamReader(Resources.load(path))) as JsonObject
    data.keySet().forEach { entity -> entities.putIfAbsent(entity, XQueryEntityRef(entity, type)) }
}

private var ENTITIES: HashMap<String, XQueryEntityRef>? = null
    get() {
        if (field == null) {
            field = HashMap()
            // Dynamically load the predefined entities on their first use. This loads the entities:
            //     XML ⊂ HTML 4 ⊂ HTML 5
            // ensuring that the subset type is reported correctly.
            loadPredefinedEntities(field!!, "predefined-entities/xml.json", XQueryEntityRefType.XML)
            loadPredefinedEntities(field!!, "predefined-entities/html4.json", XQueryEntityRefType.HTML4)
            loadPredefinedEntities(field!!, "predefined-entities/html5.json", XQueryEntityRefType.HTML5)
        }
        return field
    }

class XQueryPredefinedEntityRefImpl(type: IElementType, text: CharSequence) : LeafPsiElement(type, text), XQueryPredefinedEntityRef {
    override val entityRef get(): XQueryEntityRef {
        val entity = node.chars
        return ENTITIES!!.get(entity) ?: XQueryEntityRef(entity, XQueryEntityRefType.Unknown)
    }
}
