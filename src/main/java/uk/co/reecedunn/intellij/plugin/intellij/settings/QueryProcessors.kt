/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import kotlin.collections.ArrayList

@State(name = "XIJPQueryProcessors", storages = arrayOf(Storage("xijp_processors_config.xml")))
class QueryProcessors : PersistentStateComponent<QueryProcessors> {
    // region Processors

    var processors: List<QueryProcessorSettings> = ArrayList()

    fun addProcessor(processor: QueryProcessorSettings) {
        (processors as ArrayList<QueryProcessorSettings>).add(processor)
    }

    fun setProcessor(index: Int, processor: QueryProcessorSettings) {
        (processors as ArrayList<QueryProcessorSettings>).set(index, processor)
    }

    fun removeProcessor(index: Int) {
        (processors as ArrayList<QueryProcessorSettings>).removeAt(index)
    }

    // endregion
    // region PersistentStateComponent

    override fun getState(): QueryProcessors? = this

    override fun loadState(state: QueryProcessors) = XmlSerializerUtil.copyBean(state, this)

    // endregion
    // region Instance

    companion object {
        fun getInstance(): QueryProcessors {
            return ServiceManager.getService(QueryProcessors::class.java)
        }
    }

    // endregion
}
