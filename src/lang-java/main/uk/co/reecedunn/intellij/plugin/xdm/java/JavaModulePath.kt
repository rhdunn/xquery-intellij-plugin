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

class JavaModulePath private constructor(val project: Project, val classPath: String) {
    fun resolve(): PsiElement? = JavaModuleManager.getInstance(project).findClass(classPath)

    companion object {
        fun create(project: Project, path: String): JavaModulePath? {
            if (path.startsWith("java:")) return JavaModulePath(project, path.substring(5))
            return null
        }
    }
}
