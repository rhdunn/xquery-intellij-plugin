/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.configuration.roxy

import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurationFactory

@Suppress("MemberVisibilityCanBePrivate")
class RoxyProjectConfiguration(private val project: Project, override val baseDir: VirtualFile) :
    XpmProjectConfiguration {
    // region Roxy

    private val deployDir = baseDir.findChild("deploy")

    private fun getPropertiesFile(name: String): PropertiesFile? {
        return deployDir?.findChild("$name.properties")?.toPsiFile(project) as? PropertiesFile
    }

    private val default: PropertiesFile? = getPropertiesFile("default") // Default roxy properties
    private val build: PropertiesFile? = getPropertiesFile("build") // Project-specific properties
    private var env: PropertiesFile? = getPropertiesFile("local") // Environment-specific properties

    fun getProperty(property: String): Sequence<IProperty> = sequenceOf(
        env?.findPropertyByKey(property),
        build?.findPropertyByKey(property),
        default?.findPropertyByKey(property)
    ).filterNotNull()

    fun getPropertyValue(property: String, expand: Boolean = true): String? {
        val value = getProperty(property).firstOrNull()?.value
        return when {
            value == null -> null
            value.contains("\${") -> if (expand) expandPropertyValue(value) else value
            else -> value
        }
    }

    private fun expandPropertyValue(value: String): String = value.split("\${").withIndex().joinToString("") {
        when {
            it.index == 0 -> it.value
            it.value.contains('}') -> {
                it.value.split('}').withIndex().joinToString("") { (index, value) ->
                    when (index) {
                        0 -> getPropertyValue(value) ?: ""
                        1 -> value
                        else -> "}$value"
                    }
                }
            }
            else -> it.value
        }
    }

    fun getDirectory(property: String): VirtualFile? {
        return getPropertyValue(property, expand = false)?.takeIf { it.startsWith("\${basedir}") }?.let {
            baseDir.findFileByRelativePath(it.substringAfter("\${basedir}"))
        }
    }

    // endregion
    // region XpmProjectConfiguration

    override val applicationName: String?
        get() = getPropertyValue(APP_NAME)

    override var environmentName: String = "local"
        set(name) {
            field = name
            env = getPropertiesFile(name)
        }

    override val modulePaths: Sequence<VirtualFile>
        get() = sequenceOf(getDirectory(XQUERY_DIR)).filterNotNull()

    override val processorId: Int?
        get() = QueryProcessors.getInstance().processors.find {
            it.api === MarkLogicRest && it.connection?.hostname in LOCALHOST_STRINGS
        }?.id

    override val databaseName: String?
        get() = getPropertyValue(CONTENT_DB)

    // endregion
    // region XpmProjectConfigurationFactory

    companion object : XpmProjectConfigurationFactory {
        override fun create(project: Project, baseDir: VirtualFile): XpmProjectConfiguration? {
            return baseDir.children.find { ML_COMMAND.contains(it.name) }?.let { RoxyProjectConfiguration(project, baseDir) }
        }

        private val ML_COMMAND = setOf("ml", "ml.bat")

        private const val APP_NAME = "app-name"
        private const val XQUERY_DIR = "xquery.dir"
        private const val CONTENT_DB = "content-db"

        private val LOCALHOST_STRINGS = setOf("localhost", "127.0.0.1")
    }

    // endregion
}
