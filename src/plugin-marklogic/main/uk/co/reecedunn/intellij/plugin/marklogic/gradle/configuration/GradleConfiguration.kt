/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.gradle.configuration

import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurationFactory

class GradleConfiguration(private val project: Project, val baseDir: VirtualFile) : XpmProjectConfiguration {
    // region XpmProjectConfiguration

    override val modulePaths: Sequence<VirtualFile>
        get() = sequenceOf()

    // endregion
    // region XpmProjectConfigurationFactory

    companion object : XpmProjectConfigurationFactory {
        override fun create(project: Project, baseDir: VirtualFile): XpmProjectConfiguration? {
            val properties = baseDir.findChild(GRADLE_PROPERTIES)?.toPsiFile(project) as? PropertiesFile ?: return null
            return properties.findPropertyByKey(ML_APP_NAME)?.let { GradleConfiguration(project, baseDir) }
        }

        private const val GRADLE_PROPERTIES = "gradle.properties"
        private const val ML_APP_NAME = "mlAppName"
    }

    // endregion
}
