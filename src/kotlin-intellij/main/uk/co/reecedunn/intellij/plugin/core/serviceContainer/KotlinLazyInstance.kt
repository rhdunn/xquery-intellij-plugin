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
package uk.co.reecedunn.intellij.plugin.core.serviceContainer

import com.intellij.diagnostic.PluginException
import com.intellij.openapi.application.ApplicationManager

@Suppress("UsePropertyAccessSyntax") // getImplementationClassName is not available as a property on IntelliJ <= 2020.1
abstract class KotlinLazyInstance<T> : BaseKeyedLazyInstance<T>() {
    // region Bean Properties

    abstract var implementationClass: String

    override fun getImplementationClassName(): String = implementationClass

    abstract var fieldName: String

    // endregion
    // region Instance

    private var instance: T? = null

    override fun getInstance(): T = instance ?: createInstance()

    private fun createInstance(): T {
        instance = when {
            fieldName != "" -> getFieldInstance(getImplementationClassName(), fieldName)
            getImplementationClassName().endsWith("\$Companion") -> getCompanionObjectInstance()
            else -> getInstance(ApplicationManager.getApplication(), pluginDescriptor!!)
        }
        return instance!!
    }

    private fun getCompanionObjectInstance(): T {
        return getFieldInstance(getImplementationClassName().substringBefore("\$Companion"), "Companion")
    }

    private fun getFieldInstance(className: String, fieldName: String): T {
        try {
            @Suppress("UNCHECKED_CAST")
            val aClass = Class.forName(className, true, pluginDescriptor!!.pluginClassLoader) as Class<T>

            val field = aClass.getDeclaredField(fieldName)
            field.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            return field.get(null) as T
        } catch (e: Throwable) {
            throw PluginException(e, pluginDescriptor!!.pluginId)
        }
    }

    // endregion
}
