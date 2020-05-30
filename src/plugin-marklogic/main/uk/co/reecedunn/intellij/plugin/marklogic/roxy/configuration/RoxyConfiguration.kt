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
package uk.co.reecedunn.intellij.plugin.marklogic.roxy.configuration

import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

class RoxyConfiguration(private val project: Project) {
    val baseDir: VirtualFile? by lazy {
        var baseDir: VirtualFile? = null
        ProjectRootManager.getInstance(project).fileIndex.iterateContent { vf ->
            if (!vf.isDirectory && ML_COMMAND.contains(vf.name)) {
                baseDir = vf.parent
                return@iterateContent false
            }
            true
        }
        baseDir
    }

    private val deployDir = baseDir?.findChild("deploy")

    private fun getPropertiesFile(name: String): PropertiesFile? {
        return deployDir?.findChild("$name.properties")?.toPsiFile(project) as? PropertiesFile
    }

    private val default: PropertiesFile? = getPropertiesFile("default") // Default roxy properties
    private val build: PropertiesFile? = getPropertiesFile("build") // Project-specific properties
    private var env: PropertiesFile? = getPropertiesFile("local") // Environment-specific properties

    var environmentName: String = "local"
        set(name) {
            field = name
            env = getPropertiesFile(name)
        }

    companion object {
        fun getInstance(project: Project): RoxyConfiguration {
            return ServiceManager.getService(project, RoxyConfiguration::class.java)
        }

        private val ML_COMMAND = setOf("ml", "ml.bat")
    }
}
