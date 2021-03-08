/*
 * Copyright (C) 2016, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.project.settings

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import uk.co.reecedunn.intellij.plugin.core.ui.ConfigurableImpl
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import javax.swing.JComboBox
import javax.swing.JPanel

@Suppress("PrivatePropertyName")
class XQueryProjectSettingsConfigurable(project: Project) :
    ConfigurableImpl<XQueryProjectSettings>(XQueryProjectSettings.getInstance(project)) {
    // region Configurable

    @Nls
    override fun getDisplayName(): String = "XQuery"

    override fun getHelpTopic(): String? = null

    // endregion
    // region SettingsUI

    private var mVersion: JComboBox<Version>? = null
    private var mImplementations: JComboBox<Product>? = null
    private var mImplementationVersions: JComboBox<Version>? = null
    private var mDialectForXQuery1_0: JComboBox<Versioned>? = null
    private var mDialectForXQuery3_0: JComboBox<Versioned>? = null
    private var mDialectForXQuery3_1: JComboBox<Versioned>? = null

    @Suppress("PropertyName")
    private val VERSION_RENDERER = coloredListCellRenderer<Version> { _, value, _, _, _ ->
        if (value != null) {
            append(value.toFeatureString())
        }
    }

    @Suppress("PropertyName")
    private val VERSIONED_RENDERER = coloredListCellRenderer<Versioned> { _, value, _, _, _ ->
        if (value != null) {
            append(value.name)
        }
    }

    override val panel: JPanel = panel {
        row {
            label(XQueryBundle.message("xquery.settings.project.implementation.label"), column.vgap())
            mImplementations = comboBox(column.horizontal().hgap().vgap())
        }
        row {
            label(XQueryBundle.message("xquery.settings.project.implementation.version.label"), column.vgap())
            mImplementationVersions = comboBox(column.horizontal().hgap().vgap()) {
                renderer = VERSION_RENDERER
            }
        }
        row {
            label(XQueryBundle.message("xquery.settings.project.default.version.label"), column.vgap())
            mVersion = comboBox(column.horizontal().hgap().vgap()) {
                renderer = VERSION_RENDERER
            }
        }
        row {
            label(XQueryBundle.message("xquery.settings.project.dialect.1.0.label"), column.vgap())
            mDialectForXQuery1_0 = comboBox(column.horizontal().hgap().vgap()) {
                renderer = VERSIONED_RENDERER
            }
        }
        row {
            label(XQueryBundle.message("xquery.settings.project.dialect.3.0.label"), column.vgap())
            mDialectForXQuery3_0 = comboBox(column.horizontal().hgap().vgap()) {
                renderer = VERSIONED_RENDERER
            }
        }
        row {
            label(XQueryBundle.message("xquery.settings.project.dialect.3.1.label"), column)
            mDialectForXQuery3_1 = comboBox(column.horizontal().hgap()) {
                renderer = VERSIONED_RENDERER
            }
        }
        row {
            spacer(column.vertical())
        }

        createUIComponents()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> populateComboBox(control: JComboBox<T>, items: List<T>, defaultItem: T?) {
        val selected = control.selectedItem as T
        var found = false

        control.removeAllItems()
        for (item in items) {
            control.addItem(item)
            if (selected != null && item.toString() == selected.toString()) {
                control.selectedItem = item
                found = true
            }
        }
        if (!found && items.isNotEmpty()) {
            control.selectedItem = defaultItem ?: items[0]
        }
    }

    private fun createUIComponents() {
        mImplementationVersions!!.addActionListener {
            val product = mImplementations!!.selectedItem as? Product
            val productVersion = mImplementationVersions!!.selectedItem as? Version
            if (product == null || productVersion == null) return@addActionListener

            populateComboBox(mVersion!!, XQuerySpec.versionsFor(product, productVersion), null)
            populateComboBox(mDialectForXQuery1_0!!, product.flavoursForXQueryVersion(productVersion, "1.0"), null)
            populateComboBox(mDialectForXQuery3_0!!, product.flavoursForXQueryVersion(productVersion, "3.0"), null)
            populateComboBox(mDialectForXQuery3_1!!, product.flavoursForXQueryVersion(productVersion, "3.1"), null)
        }

        mImplementations!!.addActionListener {
            val product = mImplementations!!.selectedItem as? Product ?: return@addActionListener

            populateComboBox(mImplementationVersions!!, product.implementation.versions, null)
        }

        populateComboBox(mImplementations!!, PRODUCTS, W3C.SPECIFICATIONS)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <E> isModified(comboBox: JComboBox<E>, setting: E?): Boolean {
        val value = comboBox.selectedItem as E ?: return setting != null
        return value != setting
    }

    private fun isModifiedXQuery(comboBox: JComboBox<Version>, setting: String?): Boolean {
        val value = comboBox.selectedItem ?: return setting != null
        return (value as Specification).versionId != setting
    }

    private fun isModified(comboBox: JComboBox<Versioned>, setting: String?): Boolean {
        val value = comboBox.selectedItem ?: return setting != null
        return (value as Versioned).id != setting
    }

    override fun isModified(configuration: XQueryProjectSettings): Boolean {
        if (isModified(mImplementations!!, configuration.product)) return true
        if (isModified(mImplementationVersions!!, configuration.productVersion)) return true
        if (isModifiedXQuery(mVersion!!, configuration.XQueryVersion)) return true
        if (isModified(mDialectForXQuery1_0!!, configuration.XQuery10Dialect)) return true
        if (isModified(mDialectForXQuery3_0!!, configuration.XQuery30Dialect)) return true
        if (isModified(mDialectForXQuery3_1!!, configuration.XQuery31Dialect)) return true
        return false
    }

    override fun apply(configuration: XQueryProjectSettings) {
        val product = mImplementations!!.selectedItem as? Product
        val productVersion = mImplementationVersions!!.selectedItem as? Version
        val version = VersionedProductId(product, productVersion)
        val xqueryVersion = mVersion!!.selectedItem as? Specification
        val dialect10 = mDialectForXQuery1_0!!.selectedItem as? Versioned
        val dialect30 = mDialectForXQuery3_0!!.selectedItem as? Versioned
        val dialect31 = mDialectForXQuery3_1!!.selectedItem as? Versioned

        configuration.implementationVersion = version.id
        configuration.XQueryVersion = xqueryVersion?.versionId
        configuration.XQuery10Dialect = dialect10?.id
        configuration.XQuery30Dialect = dialect30?.id
        configuration.XQuery31Dialect = dialect31?.id
    }

    private fun getXQueryVersion(version: CharSequence?): Version? {
        val versions = XQuerySpec.versionsForXQuery(version)
        return if (versions.isEmpty()) null else versions[0]
    }

    override fun reset(configuration: XQueryProjectSettings) {
        mImplementations!!.selectedItem = configuration.product
        mImplementationVersions!!.selectedItem = configuration.productVersion
        mVersion!!.selectedItem = getXQueryVersion(configuration.XQueryVersion)
        mDialectForXQuery1_0!!.selectedItem = dialectById(configuration.XQuery10Dialect)
        mDialectForXQuery3_0!!.selectedItem = dialectById(configuration.XQuery30Dialect)
        mDialectForXQuery3_1!!.selectedItem = dialectById(configuration.XQuery31Dialect)
    }

    // endregion
}
