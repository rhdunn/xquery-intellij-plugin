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

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Transient
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import java.io.File

@State(name = "XQueryProjectSettings", storages = arrayOf(Storage(StoragePathMacros.WORKSPACE_FILE), Storage("xquery_config.xml")))
class XQueryProjectSettings : PersistentStateComponent<XQueryProjectSettings>, ExportableComponent {
    // region Settings

    private var IMPLEMENTATION = Implementations.getDefaultImplementation()
    private var IMPLEMENTATION_VERSION = IMPLEMENTATION.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION)
    private var PRODUCT_VERSION = VersionedProductId("w3c/spec/v1ed")
    private var XQUERY_1_0_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery, XQuery.REC_1_0_20070123)
    private var XQUERY_3_0_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery, XQuery.REC_3_0_20140408)
    private var XQUERY_3_1_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery, XQuery.REC_3_1_20170321)

    @Transient
    fun getDialectForXQueryVersion(version: Version): ImplementationItem = when (version) {
        XQuery.REC_1_0_20070123, XQuery.REC_1_0_20101214 -> {
            if (XQUERY_1_0_DIALECT === ImplementationItem.NULL_ITEM) {
                Implementations.getItemById("w3c/1.0")
            } else XQUERY_1_0_DIALECT
        }
        XQuery.REC_3_0_20140408 -> {
            if (XQUERY_3_0_DIALECT === ImplementationItem.NULL_ITEM) {
                Implementations.getItemById("w3c/3.0")
            } else XQUERY_3_0_DIALECT
        }
        XQuery.CR_3_1_20151217, XQuery.REC_3_1_20170321 -> {
            if (XQUERY_3_1_DIALECT === ImplementationItem.NULL_ITEM) {
                Implementations.getItemById("w3c/3.1")
            } else XQUERY_3_1_DIALECT
        }
        XQuery.MARKLOGIC_0_9 -> {
            val default09ml = IMPLEMENTATION_VERSION.getDefaultItemByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery, XQuery.MARKLOGIC_0_9)
            if (default09ml === ImplementationItem.NULL_ITEM) {
                Implementations.getItemById("marklogic/v8/1.0-ml")
            } else default09ml
        }
        XQuery.MARKLOGIC_1_0 -> {
            val default10ml = IMPLEMENTATION_VERSION.getDefaultItemByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery, XQuery.MARKLOGIC_1_0)
            if (default10ml === ImplementationItem.NULL_ITEM) {
                Implementations.getItemById("marklogic/v8/1.0-ml")
            } else default10ml
        }
        else -> throw AssertionError("Unknown XQuery version: " + version)
    }

    @Transient
    fun setDialectForXQueryVersion(version: Version, dialect: ImplementationItem) = when (version) {
        XQuery.REC_1_0_20070123, XQuery.REC_1_0_20101214 -> XQUERY_1_0_DIALECT = dialect
        XQuery.REC_3_0_20140408 -> XQUERY_3_0_DIALECT = dialect
        XQuery.CR_3_1_20151217, XQuery.REC_3_1_20170321 -> XQUERY_3_1_DIALECT = dialect
        else -> throw AssertionError("Unknown XQuery version: " + version)
    }

    @get:Transient
    val product: Product?
        get() = PRODUCT_VERSION.product

    @get:Transient
    val productVersion: Version?
        get() = PRODUCT_VERSION.productVersion

    // endregion
    // region Persisted Settings

    var implementation: String
        get() = IMPLEMENTATION.id
        set(implementation) {
            IMPLEMENTATION = Implementations.getItemById(implementation)
        }

    var implementationVersion: String?
        get() = PRODUCT_VERSION.id
        set(version) {
            IMPLEMENTATION_VERSION = Implementations.getItemById(version)
            PRODUCT_VERSION.id = version
        }

    var XQueryVersion: String? = "1.0"

    var XQuery10Dialect: String?
        get() = XQUERY_1_0_DIALECT.id
        set(dialect) {
            XQUERY_1_0_DIALECT = Implementations.getItemById(dialect)
        }

    var XQuery30Dialect: String?
        get() = XQUERY_3_0_DIALECT.id
        set(dialect) {
            XQUERY_3_0_DIALECT = Implementations.getItemById(dialect)
        }

    var XQuery31Dialect: String?
        get() = XQUERY_3_1_DIALECT.id
        set(dialect) {
            XQUERY_3_1_DIALECT = Implementations.getItemById(dialect)
        }

    // endregion
    // region PersistentStateComponent

    override fun getState(): XQueryProjectSettings? = this

    override fun loadState(state: XQueryProjectSettings) = XmlSerializerUtil.copyBean(state, this)

    // endregion
    // region ExportableComponent

    override fun getExportFiles(): Array<File> = arrayOf(PathManager.getOptionsFile("xquery_project_settings"))

    override fun getPresentableName(): String = XQueryBundle.message("xquery.settings.project.title")

    // endregion

    companion object {
        fun getInstance(project: Project): XQueryProjectSettings {
            return ServiceManager.getService(project, XQueryProjectSettings::class.java)
        }
    }
}
