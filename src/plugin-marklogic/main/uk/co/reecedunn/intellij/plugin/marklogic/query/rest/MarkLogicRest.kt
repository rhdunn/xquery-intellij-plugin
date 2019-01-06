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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorInstanceManager
import java.io.InputStream

object MarkLogicRest : QueryProcessorApi {
    override val id: String = "marklogic.rest"
    override val displayName: String = "MarkLogic"

    override val requireJar: Boolean = false
    override val hasConfiguration: Boolean = false

    override val canCreate: Boolean = false
    override val canConnect: Boolean = true

    override fun canExecute(language: Language, executorId: String): Boolean {
        val run = executorId == DefaultRunExecutor.EXECUTOR_ID
        val profile = executorId == DefaultProfileExecutor.EXECUTOR_ID
        return when (language) {
            ServerSideJavaScript, SPARQLQuery, SPARQLUpdate, SQL, XSLT -> run
            XQuery -> run || profile
            else -> false
        }
    }

    override fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager {
        return MarkLogic()
    }
}
