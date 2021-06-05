/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.processor.query.connection.AWSConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.connection.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessorSettingsModel
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class QueryProcessorSettings : Closeable {
    var id: Int = 0

    var name: String? = null

    @Suppress("MemberVisibilityCanBePrivate") // Needed in the settings file!
    var apiId: String = QueryProcessorApi.apis.first().id
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

    var awsConnection: AWSConnectionSettings? = null
        set(value) {
            field = value
            processor = null
        }

    var api: QueryProcessorApi
        get() = QueryProcessorApi.apis.find { value -> value.id == apiId }!!
        set(value) {
            apiId = value.id
        }

    val configuration: InputStream?
        get() {
            return try {
                configurationPath?.let { FileInputStream(it) }
            } catch (e: FileNotFoundException) {
                throw ConfigurationFileNotFoundException(e)
            }
        }

    private var instance: QueryProcessorInstanceManager? = null

    private var processor: QueryProcessor? = null

    val session: QueryProcessor
        get() {
            if (instance == null) {
                instance = api.newInstanceManager(jar, configuration)
                processor = null
            }

            if (processor == null) {
                processor = when {
                    connection != null -> instance!!.connect(connection!!)
                    awsConnection != null -> instance!!.connect(awsConnection!!)
                    else -> instance!!.create()
                }
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

data class CachedQueryProcessorSettings(
    var settings: QueryProcessorSettings,
    var presentation: Any? = null
)

fun List<QueryProcessorSettings>.addToModel(
    model: QueryProcessorSettingsModel,
    selectedServer: Int,
    predicate: (QueryProcessorSettings) -> Boolean = { true }
) {
    asSequence().filter(predicate).forEach { processor ->
        val settings = CachedQueryProcessorSettings(processor)
        model.addElement(settings)
        if (settings.settings.id == selectedServer) {
            model.selectedItem = settings
        }
    }
}
