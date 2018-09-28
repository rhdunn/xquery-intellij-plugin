/*
 * Copyright (C) 2016 Reece H. Dunn
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

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.ColoredListCellRenderer
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JPanel

@Suppress("PrivatePropertyName")
class XQueryPropertiesUI : SettingsUI<XQueryProjectSettings> {
    private var mVersion: JComboBox<Version>? = null
    private var mImplementations: JComboBox<Product>? = null
    private var mImplementationVersions: JComboBox<Version>? = null
    private var mDialectForXQuery1_0: JComboBox<Versioned>? = null
    private var mDialectForXQuery3_0: JComboBox<Versioned>? = null
    private var mDialectForXQuery3_1: JComboBox<Versioned>? = null
    private var mPanel: JPanel? = null

    override val panel get(): JPanel = mPanel!!

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
        if (!found && !items.isEmpty()) {
            control.selectedItem = defaultItem ?: items[0]
        }
    }

    private fun createUIComponents() {
        mImplementations = ComboBox()
        mImplementationVersions = ComboBox()
        mVersion = ComboBox()
        mDialectForXQuery1_0 = ComboBox()
        mDialectForXQuery3_0 = ComboBox()
        mDialectForXQuery3_1 = ComboBox()

        mImplementations!!.name = "Implementation"
        mImplementationVersions!!.name = "ImplementationVersion"
        mVersion!!.name = "XQueryVersion"
        mDialectForXQuery1_0!!.name = "DialectForXQuery1.0"
        mDialectForXQuery3_0!!.name = "DialectForXQuery3.0"
        mDialectForXQuery3_1!!.name = "DialectForXQuery3.1"

        @Suppress("LocalVariableName")
        val VERSION_RENDERER = object : ColoredListCellRenderer<Version>() {
            override fun customizeCellRenderer(list: JList<out Version>, value: Version?, index: Int, selected: Boolean, hasFocus: Boolean) {
                if (value != null) {
                    append(value.toFeatureString())
                }
            }
        }

        @Suppress("LocalVariableName")
        val VERSIONED_RENDERER = object : ColoredListCellRenderer<Versioned>() {
            override fun customizeCellRenderer(list: JList<out Versioned>, value: Versioned?, index: Int, selected: Boolean, hasFocus: Boolean) {
                if (value != null) {
                    append(value.name)
                }
            }
        }

        mVersion!!.renderer = VERSION_RENDERER
        mImplementationVersions!!.renderer = VERSION_RENDERER

        mDialectForXQuery1_0!!.renderer = VERSIONED_RENDERER
        mDialectForXQuery3_0!!.renderer = VERSIONED_RENDERER
        mDialectForXQuery3_1!!.renderer = VERSIONED_RENDERER

        mImplementationVersions!!.addActionListener { _ ->
            val product = mImplementations!!.selectedItem as? Product
            val productVersion = mImplementationVersions!!.selectedItem as? Version
            if (product == null || productVersion == null) return@addActionListener

            populateComboBox(mVersion!!, XQuery.versionsFor(product, productVersion), null)
            populateComboBox(mDialectForXQuery1_0!!, product.flavoursForXQueryVersion(productVersion, "1.0"), null)
            populateComboBox(mDialectForXQuery3_0!!, product.flavoursForXQueryVersion(productVersion, "3.0"), null)
            populateComboBox(mDialectForXQuery3_1!!, product.flavoursForXQueryVersion(productVersion, "3.1"), null)
        }

        mImplementations!!.addActionListener { _ ->
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
        return (value as Specification).label != setting
    }

    private fun isModified(comboBox: JComboBox<Versioned>, setting: String?): Boolean {
        val value = comboBox.selectedItem ?: return setting != null
        return (value as Versioned).id != setting
    }

    override fun isModified(settings: XQueryProjectSettings): Boolean {
        if (isModified(mImplementations!!, settings.product)) return true
        if (isModified(mImplementationVersions!!, settings.productVersion)) return true
        if (isModifiedXQuery(mVersion!!, settings.XQueryVersion)) return true
        if (isModified(mDialectForXQuery1_0!!, settings.XQuery10Dialect)) return true
        if (isModified(mDialectForXQuery3_0!!, settings.XQuery30Dialect)) return true
        if (isModified(mDialectForXQuery3_1!!, settings.XQuery31Dialect)) return true
        return false
    }

    override fun apply(settings: XQueryProjectSettings) {
        val product = mImplementations!!.selectedItem as? Product
        val productVersion = mImplementationVersions!!.selectedItem as? Version
        val version = VersionedProductId(product, productVersion)
        val xqueryVersion = mVersion!!.selectedItem as? Specification
        val dialect10 = mDialectForXQuery1_0!!.selectedItem as? Versioned
        val dialect30 = mDialectForXQuery3_0!!.selectedItem as? Versioned
        val dialect31 = mDialectForXQuery3_1!!.selectedItem as? Versioned

        settings.implementationVersion = version.id
        settings.XQueryVersion = xqueryVersion?.label
        settings.XQuery10Dialect = dialect10?.id
        settings.XQuery30Dialect = dialect30?.id
        settings.XQuery31Dialect = dialect31?.id
    }

    private fun getXQueryVersion(version: CharSequence?): Version? {
        val versions = XQuery.versionsForXQuery(version)
        return if (versions.isEmpty()) null else versions[0]
    }

    override fun reset(settings: XQueryProjectSettings) {
        mImplementations!!.selectedItem = settings.product
        mImplementationVersions!!.selectedItem = settings.productVersion
        mVersion!!.selectedItem = getXQueryVersion(settings.XQueryVersion)
        mDialectForXQuery1_0!!.selectedItem = dialectById(settings.XQuery10Dialect)
        mDialectForXQuery3_0!!.selectedItem = dialectById(settings.XQuery30Dialect)
        mDialectForXQuery3_1!!.selectedItem = dialectById(settings.XQuery31Dialect)
    }
}
