/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.configuration.roxy

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.MarkLogicDatabaseConfiguration
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.indices.MarkLogicAttributeIndex
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.indices.MarkLogicElementIndex
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.roxy.indices.RoxyElementRangeIndex
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.child

class RoxyDatabaseConfiguration(private val database: PsiElement, private val accessors: XmlAccessors) :
    UserDataHolderBase(),
    MarkLogicDatabaseConfiguration {
    companion object {
        const val NAMESPACE: String = "http://marklogic.com/xdmp/database"

        private val ELEMENT_INDICES = Key.create<CachedValue<List<MarkLogicElementIndex>>>("ELEMENT_INDICES")
        private const val RANGE_ELEMENT_INDEXES = "range-element-indexes"
        private const val RANGE_ELEMENT_INDEX = "range-element-index"
    }

    private fun computeElementIndices(): List<MarkLogicElementIndex>? {
        val root = accessors.child(database, NAMESPACE, RANGE_ELEMENT_INDEXES).firstOrNull() ?: return null
        return accessors.child(root, NAMESPACE, RANGE_ELEMENT_INDEX).mapTo(mutableListOf()) {
            RoxyElementRangeIndex(it, accessors)
        }
    }

    override val elementIndices: List<MarkLogicElementIndex>
        get() = CachedValuesManager.getManager(database.project).getCachedValue(this, ELEMENT_INDICES, {
            val indices = computeElementIndices() ?: emptyList()
            CachedValueProvider.Result.create(indices, database)
        }, false)

    override val attributeIndices: List<MarkLogicAttributeIndex>
        get() = emptyList()
}
