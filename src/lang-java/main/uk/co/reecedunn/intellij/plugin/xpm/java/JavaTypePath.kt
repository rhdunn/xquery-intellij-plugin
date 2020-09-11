/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.java

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import java.lang.reflect.InvocationTargetException

data class JavaTypePath(val project: Project) : XpmModulePath, XpmStaticContext {
    private val facadeClass: Class<*>? = javaClass.classLoader.loadClassOrNull("com.intellij.psi.JavaPsiFacade")
    private val facade: Any? = try {
        facadeClass?.getMethod("getInstance", Project::class.java)?.invoke(null, project)
    } catch (_: Throwable) {
        null
    }

    fun findClass(qualifiedName: String): PsiElement? = facade?.let {
        val scope = GlobalSearchScope.allScope(project)
        try {
            facadeClass?.getMethod("findClass", String::class.java, GlobalSearchScope::class.java)
                ?.invoke(it, qualifiedName, scope) as PsiElement?
        } catch (e: InvocationTargetException) {
            throw e.targetException // e.g. ProcessCancelledException, or IndexNotReadyException
        }
    }

    // region XpmModulePath

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.JAVA

    // endregion
    // region XstContext

    override fun getUsageType(element: PsiElement): XstUsageType? = null

    override fun expandQName(qname: XsQNameValue): Sequence<XsQNameValue> = emptySequence()

    // endregion

    companion object : XpmModulePathFactory {
        private const val JAVA_TYPE_NS = "http://saxon.sf.net/java-type"

        fun getInstance(project: Project): JavaTypePath = ServiceManager.getService(project, JavaTypePath::class.java)

        override fun create(project: Project, uri: XsAnyUriValue): JavaTypePath? = when (uri.context) {
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
