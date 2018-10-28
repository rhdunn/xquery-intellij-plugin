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

import uk.co.reecedunn.intellij.plugin.processor.basex.session.BaseX
import uk.co.reecedunn.intellij.plugin.processor.existdb.rest.EXistDB
import uk.co.reecedunn.intellij.plugin.processor.marklogic.rest.MarkLogic
import uk.co.reecedunn.intellij.plugin.processor.saxon.s9api.Saxon
import java.io.File

enum class QueryProcessorApi(
    val id: String,
    val displayName: String,
    val requireJar: Boolean,
    val instanceManager: (String?) -> QueryProcessorInstanceManager
) {
    BASEX_SESSION("basex.session", "BaseX", true, { jar -> BaseX(File(jar!!)) }),
    EXISTDB_REST("existdb.rest", "eXist-db", false, { _ -> EXistDB() }),
    MARKLOGIC_REST("marklogic.rest", "MarkLogic", false, { _ -> MarkLogic() }),
    SAXON_S9API("saxon.s9api", "Saxon", true, { jar -> Saxon(File(jar!!)) }),
}

data class QueryProcessorSettings(
    var name: String?,
    var apiId: String
) {
    constructor() : this(null, QueryProcessorApi.BASEX_SESSION.id)

    val displayName: String get() = name ?: ""

    var api: QueryProcessorApi
        get() = QueryProcessorApi.values().find { value -> value.id == apiId }!!
        set(value) {
            apiId = value.id
        }
}
