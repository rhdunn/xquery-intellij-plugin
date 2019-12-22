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

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import java.lang.reflect.InvocationTargetException

class JavaTypePath(val project: Project) : XdmModulePath, XdmStaticContext {
    private val facadeClass: Class<*>? = javaClass.classLoader.loadClassOrNull("com.intellij.psi.JavaPsiFacade")
    private val facade: Any? = try {
        facadeClass?.getMethod("getInstance", Project::class.java)?.invoke(null, project)
    } catch (_: Throwable) {
        null
    }

    val isJavaPluginEnabled: Boolean = facade != null

    fun findClass(qualifiedName: String): PsiElement? {
        return facade?.let {
            val scope = GlobalSearchScope.allScope(project)
            try {
                facadeClass?.getMethod("findClass", String::class.java, GlobalSearchScope::class.java)
                    ?.invoke(it, qualifiedName, scope) as PsiElement?
            } catch (e: InvocationTargetException) {
                throw e.targetException // e.g. ProcessCancelledException, or IndexNotReadyException
            }
        }
    }

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.JAVA

    override fun resolve(): PsiElement? = null

    companion object : XdmModulePathFactory {
        private const val JAVA_TYPE_NS = "http://saxon.sf.net/java-type"

        fun getInstance(project: Project): JavaTypePath {
            return ServiceManager.getService(project, JavaTypePath::class.java)
        }

        override fun create(project: Project, uri: XsAnyUriValue): JavaTypePath? {
            return when (uri.context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    if (uri.data == JAVA_TYPE_NS)
                        getInstance(project)
                    else
                        null
                }
                else -> null
            }
        }
    }
}
