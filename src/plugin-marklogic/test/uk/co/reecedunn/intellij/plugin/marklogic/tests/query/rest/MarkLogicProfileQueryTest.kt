/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.query.rest

import org.apache.http.HttpEntityEnclosingRequest
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.BuildableQuery
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - Profiling a MarkLogic query")
class MarkLogicProfileQueryTest {
    @Test
    @DisplayName("invoke XQuery module")
    fun invokeXQueryModule() {
        val params = listOf(
            "xquery" to MarkLogicQueries.Run,
            "vars" to listOf(
                "mode" to "profile",
                "mimetype" to "application/xquery",
                "module-path" to "/test/script.xqy",
                "query" to "",
                "vars" to "{}",
                "types" to "{}",
                "rdf-output-format" to "",
                "updating" to "false",
                "server" to "",
                "database" to "",
                "module-root" to "",
                "context-value" to "",
                "context-path" to ""
            )
        )

        val processor = create("localhost", 8000, "testuser")
        val query = processor.createProfileableQuery(DatabaseModule("/test/script.xqy"), XQuery)
        val request = (query as BuildableQuery).request()

        assertThat(request.method, `is`("POST"))
        assertThat(request.uri.toASCIIString(), `is`("http://localhost:8000/v1/eval"))

        val body = (request as HttpEntityEnclosingRequest).entity
        assertThat(body.contentType.value, `is`("application/x-www-form-urlencoded; charset=UTF-8"))
        assertThat(body.content.decode(Charsets.US_ASCII), `is`(params.toFormParamString()))
    }

    private fun create(hostname: String, databasePort: Int, username: String?): QueryProcessor {
        val manager = MarkLogicRest.newInstanceManager(null, null)
        return manager.connect(ConnectionSettings(hostname, databasePort, username))
    }
}
