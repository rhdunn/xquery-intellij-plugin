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
package uk.co.reecedunn.intellij.plugin.processor.query

import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsModel
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStream
import javax.swing.DefaultComboBoxModel

class QueryProcessorSettings : Closeable {
    constructor()

    constructor(
        name: String?,
        apiId: String,
        jar: String?,
        configurationPath: String?,
        connection: ConnectionSettings?
    ) {
        this.name = name
        this.apiId = apiId
        this.jar = jar
        this.configurationPath = configurationPath
        this.connection = connection
    }

    var id: Int = 0

    var name: String? = null

    @Suppress("MemberVisibilityCanBePrivate") // Needed in the settings file!
    var apiId: String = QueryProcessorApis.first().id
        set(value) {
            field = value
            instance = null
        }

    var jar: String? = null
        set(value) {
            field = value
            instance = null
        }

    var configurationPath: String? = null
        set(value) {
            field = value
            instance = null
        }

    var connection: ConnectionSettings? = null
        set(value) {
            field = value
            processor = null
        }

    var api: QueryProcessorApi
        get() = QueryProcessorApis.find { value -> value.id == apiId }!!
        set(value) {
            apiId = value.id
        }

    val configuration: InputStream? get() = configurationPath?.let { FileInputStream(it) }

    private var instance: QueryProcessorInstanceManager? = null

    private var processor: QueryProcessor? = null

    val session: QueryProcessor
        get() {
            if (instance == null) {
                instance = api.newInstanceManager(jar, configuration)
                processor = null
            }

            if (processor == null) {
                processor = if (connection == null)
                    instance!!.create()
                else
                    instance!!.connect(connection!!)
            }

            return processor!!
        }

    override fun close() {
        if (processor != null) {
            processor!!.close()
            processor = null
        }
    }
}

data class QueryProcessorSettingsWithVersionCache(
    val settings: QueryProcessorSettings,
    var version: Any? = null
)

fun List<QueryProcessorSettings>.addToModel(
    model: QueryProcessorSettingsModel,
    serversOnly: Boolean = false
) {
    forEach { processor ->
        if (processor.connection != null || !serversOnly) {
            val settings = QueryProcessorSettingsWithVersionCache(processor)
            model.addElement(settings)
            model.calculateVersion(settings)
        }
    }
}
