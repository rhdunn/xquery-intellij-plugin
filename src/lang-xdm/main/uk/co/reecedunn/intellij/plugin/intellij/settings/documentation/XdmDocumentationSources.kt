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
package uk.co.reecedunn.intellij.plugin.intellij.settings.documentation

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.text.nullize
import com.intellij.util.ui.JBUI
import uk.co.reecedunn.intellij.plugin.core.progress.TaskProgressListener
import uk.co.reecedunn.intellij.plugin.intellij.resources.XdmBundle
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationDownloader
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSourceProvider
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class XdmDocumentationSources : Configurable, TaskProgressListener<XdmDocumentationSource> {
    // region Configurable

    private val cachePath = TextFieldWithBrowseButton()
    private val sources = XdmDocumentationSourcesTable()

    override fun getDisplayName(): String = XdmBundle.message("settings.document-sources.title")

    override fun createComponent(): JComponent? {
        XdmDocumentationSourceProvider.allSources.forEach { source -> sources.add(source) }
        XdmDocumentationDownloader.getInstance().addListener(this)

        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.NONE
        constraints.weightx = 0.0
        constraints.insets = JBUI.insets(0, 0, 4, 8)
        panel.add(JBLabel(XdmBundle.message("documentation-source.cache-path.label")), constraints)

        constraints.gridx = 1
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 0.0
        constraints.insets = JBUI.insetsBottom(4)
        panel.add(cachePath, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        constraints.insets = JBUI.emptyInsets()
        panel.add(JBScrollPane(sources), constraints)

        return panel
    }

    override fun disposeUIResources() {
        XdmDocumentationDownloader.getInstance().removeListener(this)
    }

    override fun isModified(): Boolean {
        return XdmDocumentationDownloader.getInstance().basePath != cachePath.text
    }

    override fun apply() {
        XdmDocumentationDownloader.getInstance().basePath = cachePath.text.nullize()
    }

    override fun reset() {
        cachePath.text = XdmDocumentationDownloader.getInstance().basePath!!
    }

    // endregion
    // region TaskProgressListener<XdmDocumentationSource>

    override fun started(context: XdmDocumentationSource) {
        sources.update(context)
    }

    override fun stopped(context: XdmDocumentationSource) {
        sources.update(context)
    }

    // endregion
}
