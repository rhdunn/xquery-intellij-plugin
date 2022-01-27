/*
 * Copyright (C) 2020-2022 Reece H. Dunn
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
import com.intellij.lang.properties.PropertiesLanguage
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurationFactory
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class RoxyProjectConfiguration(private val project: Project, override val baseDir: VirtualFile) :
    UserDataHolderBase(),
    XpmProjectConfiguration {
    // region Roxy

    private val deployDir = baseDir.findChild("deploy")

    private fun getPropertiesFile(name: String, key: Key<CachedValue<Optional<PropertiesFile>>>): PropertiesFile? {
        return cached(key) {
            val file = deployDir?.findChild("$name.properties")?.toPsiFile(project) as? PropertiesFile
            Optional.ofNullable(file) to null
        }.orElse(null)
    }

    private val default: PropertiesFile?
        get() = getPropertiesFile("default", DEFAULT_PROPERTIES) // Default roxy properties

    private val build: PropertiesFile?
        get() = getPropertiesFile("build", BUILD_PROPERTIES) // Project-specific properties

    private val env: PropertiesFile?
        get() = getPropertiesFile("local", ENV_PROPERTIES)

    private fun <T> cached(key: Key<CachedValue<T>>, compute: () -> Pair<T, PsiElement?>): T {
        return CachedValuesManager.getManager(project).getCachedValue(this, key, {
            val (value, context) = compute()
            val dependencies = listOfNotNull(context, getModificationTracker(project))
            CachedValueProvider.Result.create(value, dependencies)
        }, false)
    }

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
    // region XpmProjectConfiguration

    override fun getModificationTracker(project: Project): ModificationTracker {
        return PsiModificationTracker.SERVICE.getInstance(project).forLanguage(PropertiesLanguage.INSTANCE)
    }

    override val applicationName: String?
        get() = cached(APPLICATION_NAME) {
            Optional.ofNullable(getPropertyValue(APP_NAME)) to null
        }.orElse(null)

    override var environmentName: String = "local"

    override val modulePaths: Sequence<VirtualFile>
        get() = cached(MODULE_PATHS) {
            sequenceOf(getVirtualFile(XQUERY_DIR)).filterNotNull().toList() to null
        }.asSequence()

    override val processorId: Int?
        get() = QueryProcessors.getInstance().processors.find {
            it.api === MarkLogicRest && it.connection?.hostname in LOCALHOST_STRINGS
        }?.id

    override val databaseName: String?
        get() = cached(DATABASE_NAME) {
            Optional.ofNullable(getPropertyValue(CONTENT_DB)) to null
        }.orElse(null)

    // endregion
    // region XpmProjectConfigurationFactory

    companion object : XpmProjectConfigurationFactory {
        override fun create(project: Project, baseDir: VirtualFile): XpmProjectConfiguration? {
            return baseDir.children.find { ML_COMMAND.contains(it.name) }?.let {
                RoxyProjectConfiguration(project, baseDir)
            }
        }

        private val ML_COMMAND = setOf("ml", "ml.bat")

        private val APPLICATION_NAME = Key.create<CachedValue<Optional<String>>>("APPLICATION_NAME")
        private const val APP_NAME = "app-name"

        private val MODULE_PATHS = Key.create<CachedValue<List<VirtualFile>>>("MODULE_PATHS")
        private const val XQUERY_DIR = "xquery.dir"

        private val DATABASE_NAME = Key.create<CachedValue<Optional<String>>>("DATABASE_NAME")
        private const val CONTENT_DB = "content-db"

        private val DEFAULT_PROPERTIES = Key.create<CachedValue<Optional<PropertiesFile>>>("DEFAULT_PROPERTIES")
        private val BUILD_PROPERTIES = Key.create<CachedValue<Optional<PropertiesFile>>>("BUILD_PROPERTIES")
        private val ENV_PROPERTIES = Key.create<CachedValue<Optional<PropertiesFile>>>("ENV_PROPERTIES")

        private val LOCALHOST_STRINGS = setOf("localhost", "127.0.0.1")
    }

    // endregion
}
