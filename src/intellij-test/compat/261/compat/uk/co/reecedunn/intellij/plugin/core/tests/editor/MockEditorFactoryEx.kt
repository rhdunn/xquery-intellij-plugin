// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.editor

import com.intellij.mock.MockEditorEventMulticaster
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.event.EditorEventMulticaster
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.CharArrayCharSequence
import java.util.stream.Stream

class MockEditorFactoryEx : EditorFactory() {
    override fun createDocument(text: CharSequence): Document = DocumentImpl(text)

    override fun createDocument(text: CharArray): Document = DocumentImpl(CharArrayCharSequence(text, 0, text.size))

    override fun createEditor(document: Document): Editor = MockEditor(document)

    override fun createViewer(document: Document): Editor? = null

    override fun createEditor(document: Document, project: Project?): Editor? = null

    override fun createEditor(document: Document, project: Project?, kind: EditorKind): Editor? = null

    override fun createEditor(
        document: Document, project: Project?, fileType: FileType, isViewer: Boolean
    ): Editor? = null

    override fun createEditor(
        document: Document, project: Project?, file: VirtualFile, isViewer: Boolean
    ): Editor? = null

    override fun createEditor(
        document: Document, project: Project?, file: VirtualFile, isViewer: Boolean, kind: EditorKind
    ): Editor? = null

    override fun createViewer(document: Document, project: Project?): Editor? = null

    override fun createViewer(document: Document, project: Project?, kind: EditorKind): Editor? = null

    override fun releaseEditor(editor: Editor) {
    }

    override fun editors(document: Document, project: Project?): Stream<Editor> = Stream.empty()

    override fun getAllEditors(): Array<Editor> = arrayOf()

    override fun getEditorList(): List<Editor> = listOf()

    @Deprecated("Deprecated in Java")
    @Suppress("removal")
    override fun addEditorFactoryListener(listener: EditorFactoryListener) {
    }

    override fun addEditorFactoryListener(listener: EditorFactoryListener, parentDisposable: Disposable) {
    }

    @Deprecated("Deprecated in Java")
    @Suppress("removal")
    override fun removeEditorFactoryListener(listener: EditorFactoryListener) {
    }

    override fun getEventMulticaster(): EditorEventMulticaster = MockEditorEventMulticaster()

    override fun refreshAllEditors() {
    }
}
