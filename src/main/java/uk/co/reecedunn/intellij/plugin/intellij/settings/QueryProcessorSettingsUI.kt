package uk.co.reecedunn.intellij.plugin.intellij.settings

import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import javax.swing.*

class QueryProcessorSettingsUI : SettingsUI<QueryProcessorSettings> {
    // region Form

    private var name: JTextField? = null

    private fun createUIComponents() {
        name = JTextField()
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorSettings): Boolean {
        return false
    }

    override fun reset(configuration: QueryProcessorSettings) {
        name!!.text = configuration.name ?: ""
    }

    override fun apply(configuration: QueryProcessorSettings) {
        configuration.name = name!!.text.let { if (it.isEmpty()) null else it }
    }

    // endregion
}
