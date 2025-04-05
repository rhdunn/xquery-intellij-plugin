// Copyright (C) 2019, 2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xqdoc.documentation.settings

import com.intellij.compat.openapi.ui.addBrowseFolderListenerEx
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.progress.TaskProgressListener
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.xpm.lang.documentation.XpmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationDownloader
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSourceProvider
import uk.co.reecedunn.intellij.plugin.xqdoc.resources.XQDocBundle
import javax.swing.JComponent

class XQDocDocumentationSourcesConfigurable : Configurable, TaskProgressListener<XpmDocumentationSource> {
    // region Configurable

    private lateinit var cachePath: TextFieldWithBrowseButton
    private lateinit var sources: TableView<XpmDocumentationSource>

    override fun getDisplayName(): String = XQDocBundle.message("settings.document-sources.title")

    override fun createComponent(): JComponent = panel {
        row {
            label(XQDocBundle.message("documentation-source.cache-path.label"), column.vgap())
            cachePath = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                addBrowseFolderListenerEx(null, descriptor)
            }
        }
        row {
            val decorator = toolbarDecorator {
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

            decorator.actionButton(XQDocBundle.message("action.download.label"), AllIcons.Actions.Download) {
                sources.selectedObject?.let {
                    XQDocDocumentationDownloader.getInstance().download(it)
                }
            }

            toolbarPanel(decorator, column.fill().spanCols())
        }
    }

    override fun disposeUIResources() {
        XQDocDocumentationDownloader.getInstance().removeListener(this)
    }

    override fun isModified(): Boolean = XQDocDocumentationDownloader.getInstance().basePath != cachePath.text

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

    override fun started(context: XpmDocumentationSource) {
        sources.update(context)
    }

    override fun stopped(context: XpmDocumentationSource) {
        sources.update(context)
    }

    // endregion
}
