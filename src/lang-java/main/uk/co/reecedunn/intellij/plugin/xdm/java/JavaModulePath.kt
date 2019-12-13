/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.java

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.module.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.XdmModulePathFactory

class JavaModulePath private constructor(val project: Project, val classPath: String) : XdmModulePath {
    override fun resolve(): PsiElement? = JavaModuleManager.getInstance(project).findClass(classPath)

    companion object : XdmModulePathFactory {
        private val NOT_JAVA_PATH: Regex = "([/:]|\\.(xq([lmuy]?|uery|ws)|xslt?|xsd)$)".toRegex()

        override fun create(project: Project, path: String): JavaModulePath? {
            if (path.startsWith("java:")) return JavaModulePath(project, path.substring(5))
            if (path.contains(NOT_JAVA_PATH) || path.isEmpty()) return null
            return JavaModulePath(project, path)
        }
    }
}
