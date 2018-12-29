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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import com.intellij.execution.executors.DefaultRunExecutor
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorInstanceManager
import java.io.InputStream

object EXistDBRest : QueryProcessorApi {
    override val id: String = "existdb.rest"
    override val displayName: String = "eXist-db"

    override val requireJar: Boolean = false
    override val hasAdminPort: Boolean = false
    override val hasConfiguration: Boolean = false

    override val canCreate: Boolean = false
    override val canConnect: Boolean = true

    override fun canExecute(mimetype: String, executorId: String): Boolean {
        val run = executorId == DefaultRunExecutor.EXECUTOR_ID
        return when (mimetype) {
            MimeTypes.XQUERY -> run
            else -> false
        }
    }

    override fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager {
        return EXistDB()
    }
}
