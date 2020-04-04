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

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.progress.TaskProgressListener
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQDocBundle
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationDownloader
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSourceProvider
import javax.swing.JComponent

class XQDocDocumentationSourcesConfigurable : Configurable, TaskProgressListener<XQDocDocumentationSource> {
    // region Configurable

    private lateinit var cachePath: TextFieldWithBrowseButton
    private lateinit var sources: TableView<XQDocDocumentationSource>

    override fun getDisplayName(): String = XQDocBundle.message("settings.document-sources.title")

    override fun createComponent(): JComponent? = panel {
        label(XQDocBundle.message("documentation-source.cache-path.label"), grid(0, 0))
        cachePath = textFieldWithBrowseButton(grid(1, 0).hgap(6).vgap(4)) {
            val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
            addBrowseFolderListener(null, null, null, descriptor)
        }

        toolbarPanel(grid(0, 1)) {
            action(XQDocBundle.message("action.download.label"), AllIcons.Actions.Download) {
                sources.selectedObject?.let {
                    XQDocDocumentationDownloader.getInstance().download(it)
                }
            }

            sources = tableView {
                columns {
                    nameColumn()
                    versionColumn()
                    statusColumn()
                }

                XQDocDocumentationSourceProvider.allSources.forEach { source -> add(source) }
                XQDocDocumentationDownloader.getInstance().addListener(this@XQDocDocumentationSourcesConfigurable)
            }
            sources
        }
    }

    override fun disposeUIResources() {
        XQDocDocumentationDownloader.getInstance().removeListener(this)
    }

    override fun isModified(): Boolean {
        return XQDocDocumentationDownloader.getInstance().basePath != cachePath.text
    }

    override fun apply() {
        XQDocDocumentationDownloader.getInstance().basePath = cachePath.text.nullize()
        sources.updateAll()
    }

    override fun reset() {
        cachePath.text = XQDocDocumentationDownloader.getInstance().basePath!!
        sources.updateAll()
    }

    // endregion
    // region TaskProgressListener<XdmDocumentationSource>

    override fun started(context: XQDocDocumentationSource) {
        sources.update(context)
    }

    override fun stopped(context: XQDocDocumentationSource) {
        sources.update(context)
    }

    // endregion
}
