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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT
import uk.co.reecedunn.intellij.plugin.saxon.resources.SaxonIcons
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.processor.query.*
import java.io.File
import java.io.InputStream
import javax.swing.Icon

object SaxonS9API : ItemPresentation, QueryProcessorApi {
    // region ItemPresentation

    override fun getPresentableText(): String = "Saxon"

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = SaxonIcons.Product

    // endregion
    // region QueryProcessorApi

    override val id: String = "saxon.s9api"
    override val presentation: ItemPresentation
        get() = this

    override val requireJar: Boolean = true
    override val hasConfiguration: Boolean = true

    override val canCreate: Boolean = true
    override val canConnect: Boolean = false

    override fun canOutputRdf(language: Language?): Boolean = false

    override fun canUpdate(language: Language?): Boolean = language == XQuery

    override fun canExecute(language: Language, executorId: String): Boolean {
        val run = executorId == DefaultRunExecutor.EXECUTOR_ID
        val profile = executorId == DefaultProfileExecutor.EXECUTOR_ID
        return when (language) {
            XQuery, XSLT -> run || profile
            XPath -> run
            else -> false
        }
    }

    override fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager {
        if (jar == null)
            throw MissingJarFileException(presentation.presentableText!!)
        return try {
            Saxon(File(jar), config)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedJarFileException(presentation.presentableText!!)
        }
    }

    override fun newInstanceManager(classLoader: ClassLoader, config: InputStream?): QueryProcessorInstanceManager {
        return try {
            Saxon(classLoader, config)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedJarFileException(presentation.presentableText!!)
        }
    }

    // endregion
}
