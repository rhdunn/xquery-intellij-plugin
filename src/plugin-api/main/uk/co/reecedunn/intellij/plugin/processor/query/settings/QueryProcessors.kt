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
package uk.co.reecedunn.intellij.plugin.processor.query.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.xmlb.XmlSerializerUtil
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings

@State(name = "XIJPQueryProcessors", storages = [Storage("xijp_processors_config.xml")])
class QueryProcessors : PersistentStateComponent<QueryProcessorsData> {
    private val data = QueryProcessorsData()

    // region Processors Event Listener

    private val queryProcessorsListeners = ContainerUtil.createLockFreeCopyOnWriteList<QueryProcessorsListener>()

    fun addQueryProcessorsListener(listener: QueryProcessorsListener) {
        queryProcessorsListeners.add(listener)
    }

    fun removeQueryResultListener(listener: QueryProcessorsListener) {
        queryProcessorsListeners.add(listener)
    }

    // endregion
    // region Processors

    val processors: List<QueryProcessorSettings>
        get() = data.processors

    fun addProcessor(processor: QueryProcessorSettings) {
        (processors as ArrayList<QueryProcessorSettings>).add(processor)
        data.currentProcessorId++
        processor.id = data.currentProcessorId

        queryProcessorsListeners.forEach { it.onAddProcessor(processor) }
    }

    fun setProcessor(index: Int, processor: QueryProcessorSettings) {
        val id = processors[index].id
        (processors as ArrayList<QueryProcessorSettings>)[index] = processor
        processor.id = id

        queryProcessorsListeners.forEach { it.onEditProcessor(index, processor) }
    }

    fun removeProcessor(index: Int) {
        (processors as ArrayList<QueryProcessorSettings>).removeAt(index)

        queryProcessorsListeners.forEach { it.onRemoveProcessor(index) }
    }

    // endregion
    // region PersistentStateComponent

    override fun getState(): QueryProcessorsData = data

    override fun loadState(state: QueryProcessorsData): Unit = XmlSerializerUtil.copyBean(state, data)

    // endregion
    // region Instance

    companion object {
        fun getInstance(): QueryProcessors = ApplicationManager.getApplication().getService(QueryProcessors::class.java)
    }

    // endregion
}
