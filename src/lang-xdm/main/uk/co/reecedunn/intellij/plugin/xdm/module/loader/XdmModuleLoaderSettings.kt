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
package uk.co.reecedunn.intellij.plugin.xdm.module.loader

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath

@State(name = "XIJPModuleLoaderSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class XdmModuleLoaderSettings : XdmModuleLoader, PersistentStateComponent<XdmModuleLoaderSettings> {
    @Attribute("loader")
    var loaderBeans: List<XdmModuleLoaderBean> = arrayListOf(
        XdmModuleLoaderBean("java", null),
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

    override fun context(path: XdmModulePath, context: PsiElement): XdmStaticContext? {
        return loaders.get()?.asSequence()?.mapNotNull { it.context(path, context) }?.firstOrNull()
    }

    // endregion
    // region PersistentStateComponent

    override fun getState(): XdmModuleLoaderSettings? = this

    override fun loadState(state: XdmModuleLoaderSettings) = XmlSerializerUtil.copyBean(state, this)

    // endregion

    companion object {
        fun getInstance(project: Project): XdmModuleLoaderSettings {
            return ServiceManager.getService(project, XdmModuleLoaderSettings::class.java)
        }
    }
}
