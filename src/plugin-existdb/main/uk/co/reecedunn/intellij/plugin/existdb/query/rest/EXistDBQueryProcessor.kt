/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.vfs.VirtualFile
import org.apache.http.client.methods.RequestBuilder
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationImpl
import uk.co.reecedunn.intellij.plugin.existdb.log.Log4JPattern
import uk.co.reecedunn.intellij.plugin.existdb.resources.EXistDBIcons
import uk.co.reecedunn.intellij.plugin.existdb.resources.EXistDBQueries
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.query.QueryServer
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType
import uk.co.reecedunn.intellij.plugin.processor.query.connection.InstanceDetails
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.run.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.run.RunnableQueryProvider
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

internal class EXistDBQueryProcessor(
    private val baseUri: String,
    private val connection: HttpConnection,
    private val settings: InstanceDetails
) : RunnableQueryProvider,
    LogViewProvider {
    // region QueryProcessor

    override val presentation: ItemPresentation
        get() {
            val version = createRunnableQuery(EXistDBQueries.Version, XQuery).run().results
            val icon = when (version[0].value) {
                "FusionDB" -> EXistDBIcons.Product.FusionDB
                else -> EXistDBIcons.Product.EXistDB
            }
            return ItemPresentationImpl(icon, "${version[0].value} ${version[1].value}")
        }

    override val servers: List<QueryServer> = listOf(QueryServer(QueryServer.NONE, QueryServer.NONE))

    // endregion
    // region RunnableQueryProvider

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery = when (language) {
        XQuery -> {
            val builder = RequestBuilder.post("$baseUri/db")
            EXistDBQuery(builder, query, connection, settings)
        }
        else -> throw UnsupportedQueryType(language)
    }

    // endregion
    // region LogViewProvider

    override fun logs(): List<String> = createRunnableQuery(EXistDBQueries.Log.Logs, XQuery).run().results.map {
        it.value as String
    }

    override fun log(name: String): List<Any> = createRunnableQuery(EXistDBQueries.Log.Log, XQuery).use { query ->
        query.bindVariable("name", name, "xs:string")

        val results = query.run().results
        val pattern = Log4JPattern.create(results.first().value as String)
        results.subList(1, results.size).map { pattern.parse(it.value as String) }
    }

    override fun defaultLogFile(logs: List<String>): String = "exist.log"

    // endregion
    // region Closeable

    override fun close() = connection.close()

    // endregion
}
