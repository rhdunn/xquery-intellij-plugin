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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.basex.intellij.resources.BaseXIcons
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.Session
import uk.co.reecedunn.intellij.plugin.basex.intellij.resources.BaseXQueries
import uk.co.reecedunn.intellij.plugin.basex.log.BaseXLogLine
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.*

internal class BaseXQueryProcessor(
    val session: Session,
    val classLoader: ClassLoader
) : ProfileableQueryProvider,
    RunnableQueryProvider,
    LogViewProvider {
    // region QueryProcessor

    override val presentation: ItemPresentation
        get() {
            val version = createRunnableQuery(BaseXQueries.Version, XQuery).run().results.first().value
            return ItemPresentationImpl(BaseXIcons.Product, "BaseX $version")
        }

    override val servers: Map<String, String> = mapOf()

    override val databases: List<String> = listOf()

    // endregion
    // region ProfileableQueryProvider

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery = when (language) {
        XQuery -> BaseXProfileableQuery(session, query.decode()!!, query, classLoader)
        else -> throw UnsupportedQueryType(language)
    }

    // endregion
    // region RunnableQueryProvider

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery = when (language) {
        XQuery -> BaseXRunnableQuery(session, query.decode()!!, query, classLoader)
        else -> throw UnsupportedQueryType(language)
    }

    // endregion
    // region LogViewProvider

    override fun logs(): List<String> = createRunnableQuery(BaseXQueries.Log.Logs, XQuery).run().results.map {
        it.value as String
    }

    override fun log(name: String): List<Any> = createRunnableQuery(BaseXQueries.Log.Log, XQuery).use { query ->
        query.bindVariable("name", name, "xs:string")
        query.run().results.chunked(6).map {
            BaseXLogLine(
                name,
                it[0].value as String,
                (it[1].value as String).takeIf { value -> value.isNotEmpty() },
                it[2].value as String,
                it[3].value as String,
                it[4].value as String,
                it[5].value as String
            )
        }
    }

    override fun defaultLogFile(logs: List<String>): String? = logs.lastOrNull()

    // endregion
    // region Closeable

    override fun close() {
        session.close()
    }

    // endregion
}
