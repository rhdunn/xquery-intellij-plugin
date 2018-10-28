package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.openapi.fileChooser.FileTypeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.ui.ColoredListCellRenderer
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import javax.swing.*

class QueryProcessorSettingsUI(private val project: Project) : SettingsUI<QueryProcessorSettings> {
    // region Processor APIs

    private var api: JComboBox<QueryProcessorApi>? = null

    private fun createQueryProcessorApiUI() {
        api = ComboBox()
        api!!.renderer = object : ColoredListCellRenderer<QueryProcessorApi>() {
            override fun customizeCellRenderer(
                list: JList<out QueryProcessorApi>,
                value: QueryProcessorApi?,
                index: Int, selected: Boolean, hasFocus: Boolean
            ) {
                if (value != null) {
                    append(value.displayName)
                }
            }
        }

        QueryProcessorApi.values().forEach { value -> api!!.addItem(value) }
    }

    // endregion
    // region Processor APIs

    private var jar: ComponentWithBrowseButton<JTextField>? = null

    private fun createJarUI() {
        jar = ComponentWithBrowseButton(JTextField(), null)
        jar!!.addBrowseFolderListener(
            XQueryBundle.message("browser.choose.implementation-api-jar"), null,
            project,
            FileTypeDescriptor(XQueryBundle.message("browser.choose.implementation-api-jar"), "jar"),
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

    // endregion
    // region Form

    private var name: JTextField? = null

    private fun createUIComponents() {
        name = JTextField()
        createQueryProcessorApiUI()
        createJarUI()
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorSettings): Boolean {
        return false
    }

    override fun reset(configuration: QueryProcessorSettings) {
        name!!.text = configuration.name ?: ""
        api!!.selectedItem = configuration.api
        jar!!.childComponent.text = configuration.jar ?: ""
    }

    override fun apply(configuration: QueryProcessorSettings) {
        configuration.name = name!!.text.let { if (it.isEmpty()) null else it }
        configuration.api = api!!.selectedItem as QueryProcessorApi
        configuration.jar = jar!!.childComponent.text.let { if (it.isEmpty()) null else it }
    }

    // endregion
}
