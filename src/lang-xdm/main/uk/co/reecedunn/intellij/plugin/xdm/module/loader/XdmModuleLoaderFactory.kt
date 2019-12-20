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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.xmlb.annotations.Tag
import uk.co.reecedunn.compat.extensions.instantiateBean

interface XdmModuleLoaderFactory {
    companion object {
        val EP_NAME = ExtensionPointName.create<XdmModuleLoaderFactoryBean>("uk.co.reecedunn.intellij.moduleLoaderFactory")

        fun create(name: String, context: String?): XdmModuleLoader? {
            return EP_NAME.extensions.find { it.name == name }?.let {
                val container = ApplicationManager.getApplication().picoContainer
                val instance = it.instantiateBean<XdmModuleLoaderFactory>(it.implementation, container)
                instance.loader(context)
            }
        }
    }

    fun loader(context: String?): XdmModuleLoader?
}

@Tag("moduleLoader")
data class XdmModuleLoaderBean(var name: String = "", var context: String? = null) {
    val loader: XdmModuleLoader? get() = XdmModuleLoaderFactory.create(name, context)
}
