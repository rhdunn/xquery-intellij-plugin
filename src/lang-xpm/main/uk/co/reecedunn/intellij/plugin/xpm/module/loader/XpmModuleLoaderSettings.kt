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
package uk.co.reecedunn.intellij.plugin.xpm.module.loader

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xdm.context.XstContext
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.XdmModuleLoader
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.XdmModuleLoaderBean
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.paths
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element

@State(name = "XIJPModuleLoaderSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class XpmModuleLoaderSettings : XdmModuleLoader, PersistentStateComponent<XpmModuleLoaderSettings> {
    @Attribute("loader")
    var loaderBeans: List<XdmModuleLoaderBean> = arrayListOf(
        XdmModuleLoaderBean("java", null),
        XdmModuleLoaderBean("module", "java:source"),
        XdmModuleLoaderBean("module", "java:test-source"),
        XdmModuleLoaderBean("relative", null)
    )
        set(value) {
            field = value
            loaders.invalidate()
        }

    @Transient
    private val loaders = CacheableProperty { loaderBeans.mapNotNull { it.loader } }

    // region XdmModuleLoader

    override fun resolve(path: XdmModulePath, context: PsiElement): PsiElement? {
        return loaders.get()?.asSequence()?.mapNotNull { it.resolve(path, context) }?.firstOrNull()
    }

    override fun context(path: XdmModulePath, context: PsiElement): XstContext? {
        return loaders.get()?.asSequence()?.mapNotNull { it.context(path, context) }?.firstOrNull()
    }

    // endregion
    // region PersistentStateComponent

    override fun getState(): XpmModuleLoaderSettings? = this

    override fun loadState(state: XpmModuleLoaderSettings) = XmlSerializerUtil.copyBean(state, this)

    // endregion

    companion object {
        fun getInstance(project: Project): XpmModuleLoaderSettings {
            return ServiceManager.getService(project, XpmModuleLoaderSettings::class.java)
        }
    }
}

fun XsAnyUriValue.resolve(): PsiElement? {
    val element = element!!
    val loaders = XpmModuleLoaderSettings.getInstance(element.project)
    return paths(element.project).mapNotNull { loaders.resolve(it, element) }.firstOrNull()
}

fun XsAnyUriValue.context(): XstContext? {
    val element = element!!
    val loaders = XpmModuleLoaderSettings.getInstance(element.project)
    return paths(element.project).mapNotNull { loaders.context(it, element) }.firstOrNull()
}
