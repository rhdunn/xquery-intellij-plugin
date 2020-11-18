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
package uk.co.reecedunn.intellij.plugin.core.tests.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import java.awt.Insets
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.geom.Point2D
import javax.swing.JComponent
import javax.swing.border.Border

class MockEditor(private val document: Document) : Editor {
    private val caretModel = MockCaretModel(this)

    override fun offsetToVisualPosition(offset: Int): VisualPosition = TODO()

    override fun offsetToVisualPosition(offset: Int, leanForward: Boolean, beforeSoftWrap: Boolean): VisualPosition {
        TODO()
    }

    override fun getFoldingModel(): FoldingModel = TODO()

    override fun offsetToLogicalPosition(offset: Int): LogicalPosition = TODO()

    override fun getComponent(): JComponent = TODO()

    override fun <T : Any?> putUserData(key: Key<T>, value: T?): Unit = TODO()

    override fun visualPositionToPoint2D(pos: VisualPosition): Point2D = TODO()

    override fun logicalPositionToOffset(pos: LogicalPosition): Int = TODO()

    override fun isViewer(): Boolean = TODO()

    override fun visualPositionToXY(visible: VisualPosition): Point = TODO()

    override fun getGutter(): EditorGutter = TODO()

    override fun logicalPositionToXY(pos: LogicalPosition): Point = TODO()

    override fun getDocument(): Document = document

    override fun getScrollingModel(): ScrollingModel = TODO()

    override fun getIndentsModel(): IndentsModel = TODO()

    override fun getLineHeight(): Int = TODO()

    override fun getProject(): Project = TODO()

    override fun getColorsScheme(): EditorColorsScheme = TODO()

    override fun isInsertMode(): Boolean = TODO()

    override fun getSelectionModel(): SelectionModel = TODO()

    override fun xyToLogicalPosition(p: Point): LogicalPosition = TODO()

    override fun getSoftWrapModel(): SoftWrapModel = TODO()

    override fun removeEditorMouseListener(listener: EditorMouseListener): Unit = TODO()

    override fun isDisposed(): Boolean = TODO()

    override fun getEditorKind(): EditorKind = TODO()

    override fun addEditorMouseListener(listener: EditorMouseListener): Unit = TODO()

    override fun getSettings(): EditorSettings = TODO()

    override fun xyToVisualPosition(p: Point): VisualPosition = TODO()

    override fun xyToVisualPosition(p: Point2D): VisualPosition = TODO()

    override fun getCaretModel(): CaretModel = caretModel

    override fun isColumnMode(): Boolean = TODO()

    override fun getMouseEventArea(e: MouseEvent): EditorMouseEventArea = TODO()

    override fun setBorder(border: Border?): Unit = TODO()

    override fun getMarkupModel(): MarkupModel = TODO()

    override fun visualToLogicalPosition(visiblePos: VisualPosition): LogicalPosition = TODO()

    override fun getInsets(): Insets = TODO()

    override fun addEditorMouseMotionListener(listener: EditorMouseMotionListener): Unit = TODO()

    override fun logicalToVisualPosition(logicalPos: LogicalPosition): VisualPosition = TODO()

    override fun isOneLineMode(): Boolean = TODO()

    @Suppress("UnstableApiUsage")
    override fun getInlayModel(): InlayModel = TODO()

    override fun setHeaderComponent(header: JComponent?): Unit = TODO()

    override fun <T : Any?> getUserData(key: Key<T>): T = TODO()

    override fun getHeaderComponent(): JComponent = TODO()

    override fun removeEditorMouseMotionListener(listener: EditorMouseMotionListener): Unit = TODO()

    override fun hasHeaderComponent(): Boolean = TODO()

    override fun getContentComponent(): JComponent = TODO()
}
