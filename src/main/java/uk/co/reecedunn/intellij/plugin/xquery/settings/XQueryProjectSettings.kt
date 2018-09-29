/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Transient
import uk.co.reecedunn.intellij.plugin.intellij.lang.*

@State(name = "XQueryProjectSettings", storages = arrayOf(Storage(StoragePathMacros.WORKSPACE_FILE), Storage("xquery_config.xml")))
class XQueryProjectSettings : PersistentStateComponent<XQueryProjectSettings> {
    // region Settings

    @Suppress("PrivatePropertyName")
    private var PRODUCT_VERSION = VersionedProductId("w3c/spec/v1ed")

    @get:Transient
    val product: Product
        get() = PRODUCT_VERSION.product ?: W3C.SPECIFICATIONS

    @get:Transient
    val productVersion: Version
        get() = PRODUCT_VERSION.productVersion ?: when (product) {
            BaseX.BASEX -> BaseX.VERSION_8_6
            MarkLogic.MARKLOGIC -> MarkLogic.VERSION_9_0
            Saxon.HE, Saxon.PE, Saxon.EE, Saxon.EE_Q, Saxon.EE_T, Saxon.EE_V -> Saxon.VERSION_9_8
            W3C.SPECIFICATIONS -> W3C.FIRST_EDITION
            else -> throw RuntimeException("Unknown product: $product")
        }

    // endregion
    // region Persisted Settings

    var implementationVersion: String?
        get() = PRODUCT_VERSION.id
        set(version) {
            PRODUCT_VERSION.id = version
        }

    @Suppress("PropertyName")
    var XQueryVersion: String? = "1.0"

    @Suppress("PropertyName")
    var XQuery10Dialect: String? = "xquery"

    @Suppress("PropertyName")
    var XQuery30Dialect: String? = "xquery"

    @Suppress("PropertyName")
    var XQuery31Dialect: String? = "xquery"

    // endregion
    // region PersistentStateComponent

    override fun getState(): XQueryProjectSettings? = this

    override fun loadState(state: XQueryProjectSettings) = XmlSerializerUtil.copyBean(state, this)

    // endregion

    companion object {
        fun getInstance(project: Project): XQueryProjectSettings {
            return ServiceManager.getService(project, XQueryProjectSettings::class.java)
        }
    }
}
