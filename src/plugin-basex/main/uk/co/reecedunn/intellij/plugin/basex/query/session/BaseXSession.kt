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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.processor.query.*
import java.io.File
import java.io.InputStream

object BaseXSession : QueryProcessorApi {
    override val id: String = "basex.session"
    override val presentation: ItemPresentation = uk.co.reecedunn.intellij.plugin.basex.lang.BaseX.presentation

    override val requireJar: Boolean = true
    override val hasConfiguration: Boolean = false

    override val canCreate: Boolean = true
    override val canConnect: Boolean = true

    override fun canOutputRdf(language: Language?): Boolean = false

    override fun canUpdate(language: Language?): Boolean = false

    override fun canExecute(language: Language, executorId: String): Boolean {
        val run = executorId == DefaultRunExecutor.EXECUTOR_ID
        val profile = executorId == DefaultProfileExecutor.EXECUTOR_ID
        return when (language) {
            XQuery -> run || profile
            else -> false
        }
    }

    override fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager {
        if (jar == null)
            throw MissingJarFileException(presentation.presentableText!!)
        return try {
            BaseX(File(jar))
        } catch (e: ClassNotFoundException) {
            throw UnsupportedJarFileException(presentation.presentableText!!)
        }
    }

    override fun newInstanceManager(classLoader: ClassLoader, config: InputStream?): QueryProcessorInstanceManager {
        return try {
            BaseX(classLoader)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedJarFileException(presentation.presentableText!!)
        }
    }
}
