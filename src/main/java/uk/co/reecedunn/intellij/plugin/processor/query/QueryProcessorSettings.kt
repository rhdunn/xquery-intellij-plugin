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
package uk.co.reecedunn.intellij.plugin.processor.query

import uk.co.reecedunn.intellij.plugin.processor.basex.session.BaseXSession
import uk.co.reecedunn.intellij.plugin.processor.existdb.rest.EXistDBRest
import uk.co.reecedunn.intellij.plugin.processor.marklogic.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.saxon.s9api.SaxonS9API
import java.io.FileInputStream
import java.io.InputStream

val QUERY_PROCESSOR_APIS: List<QueryProcessorApi> = listOf(
    BaseXSession,
    EXistDBRest,
    MarkLogicRest,
    SaxonS9API
)

class QueryProcessorSettings {
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

    var name: String? = null

    var apiId: String = QUERY_PROCESSOR_APIS.first().id
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

    val displayName: String get() = name ?: ""

    var api: QueryProcessorApi
        get() = QUERY_PROCESSOR_APIS.find { value -> value.id == apiId }!!
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
                if(connection == null)
                    processor = instance!!.create()
                else
                    processor = instance!!.connect(connection!!)
            }

            return processor!!
        }
}
