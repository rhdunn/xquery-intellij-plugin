/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.extensions.ExtensionPointName
import java.io.InputStream

interface QueryProcessorApi {
    companion object {
        private val EP_NAME: ExtensionPointName<QueryProcessorApiBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.queryProcessorApi"
        )

        val apis: Sequence<QueryProcessorApi>
            get() = EP_NAME.extensions.asSequence().map { it.getInstance() }.sortedBy {api ->
                api.presentation.presentableText
            }
    }

    val id: String
    val presentation: ItemPresentation

    val requireJar: Boolean
    val hasConfiguration: Boolean

    val canCreate: Boolean
    val canConnect: Boolean

    fun canOutputRdf(language: Language?): Boolean
    fun canUpdate(language: Language?): Boolean

    fun canExecute(language: Language, executorId: String): Boolean

    fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager

    fun newInstanceManager(classLoader: ClassLoader, config: InputStream?): QueryProcessorInstanceManager
}
