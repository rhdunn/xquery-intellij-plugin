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
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.MarkLogicConfiguration
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.child
import uk.co.reecedunn.intellij.plugin.xdm.xml.impl.XmlPsiAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurationFactory
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class RoxyProjectConfiguration(private val project: Project, override val baseDir: VirtualFile) :
    UserDataHolderBase(),
    XpmProjectConfiguration,
    MarkLogicConfiguration {
    // region Roxy

    private val deployDir = baseDir.findChild("deploy")

    private fun getPropertiesFile(name: String): PropertiesFile? {
        return deployDir?.findChild("$name.properties")?.toPsiFile(project) as? PropertiesFile
    }

    private val default: PropertiesFile? = getPropertiesFile("default") // Default roxy properties
    private val build: PropertiesFile? = getPropertiesFile("build") // Project-specific properties
    private var env: PropertiesFile? = getPropertiesFile("local") // Environment-specific properties

    private fun getProperty(property: String): Sequence<IProperty> = sequenceOf(
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

    fun getVirtualFile(property: String): VirtualFile? {
        return getPropertyValue(property, expand = false)?.takeIf { it.startsWith("\${basedir}") }?.let {
            baseDir.findFileByRelativePath(it.substringAfter("\${basedir}"))
        }
    }

    // endregion
    // region MarkLogicConfiguration

    private val configuration: XmlTag?
        get() = CachedValuesManager.getManager(project).getCachedValue(this, CONFIGURATION, {
            val file = getVirtualFile(CONFIG_FILE)?.toPsiFile(project) as? XmlFile
            CachedValueProvider.Result.create(Optional.ofNullable(file?.rootTag), file, default, build, env)
        }, false).orElse(null)

    private fun computeDatabases(configuration: XmlTag?, accessors: XmlAccessors): List<Any>? {
        if (configuration == null) return null
        val root = accessors.child(configuration, DATABASE_NAMESPACE, "databases").firstOrNull() ?: return null
        return accessors.child(root, DATABASE_NAMESPACE, "database").toList()
    }

    private val databases: List<Any>
        get() = CachedValuesManager.getManager(project).getCachedValue(this, DATABASES, {
            val databases = computeDatabases(configuration, XmlPsiAccessorsProvider) ?: emptyList()
            CachedValueProvider.Result.create(databases, configuration, default, build, env)
        }, false)

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
        get() = sequenceOf(getVirtualFile(XQUERY_DIR)).filterNotNull()

    override val processorId: Int?
        get() = QueryProcessors.getInstance().processors.find {
            it.api === MarkLogicRest && it.connection?.hostname in LOCALHOST_STRINGS
        }?.id

    override val databaseName: String?
        get() = getPropertyValue(CONTENT_DB)

    // endregion
    companion object : XpmProjectConfigurationFactory {
        override fun create(project: Project, baseDir: VirtualFile): XpmProjectConfiguration? {
            return baseDir.children.find { ML_COMMAND.contains(it.name) }?.let {
                RoxyProjectConfiguration(project, baseDir)
            }
        }

        fun getInstance(project: Project): RoxyProjectConfiguration {
            val configurations = XpmProjectConfigurations.getInstance(project).configurations
            return configurations.filterIsInstance<RoxyProjectConfiguration>().first()
        }

        private val CONFIGURATION = Key.create<CachedValue<Optional<XmlTag>>>("CONFIGURATION")
        private val DATABASES = Key.create<CachedValue<List<Any>>>("DATABASES")

        private val ML_COMMAND = setOf("ml", "ml.bat")

        private const val APP_NAME = "app-name"
        private const val CONFIG_FILE = "config.file"
        private const val CONTENT_DB = "content-db"
        private const val XQUERY_DIR = "xquery.dir"

        private val LOCALHOST_STRINGS = setOf("localhost", "127.0.0.1")

        private const val DATABASE_NAMESPACE = "http://marklogic.com/xdmp/database"
    }
}
