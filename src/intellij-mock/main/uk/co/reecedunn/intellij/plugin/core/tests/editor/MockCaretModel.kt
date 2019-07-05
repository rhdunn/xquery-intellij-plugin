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

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.markup.TextAttributes

class MockCaretModel(private val editor: Editor) : CaretModel {
    private var offset: Int = 0

    override fun getVisualLineStart(): Int = TODO()

    override fun removeSecondaryCarets() = TODO()

    override fun removeCaret(caret: Caret): Boolean = TODO()

    override fun getPrimaryCaret(): Caret = TODO()

    override fun getTextAttributes(): TextAttributes = TODO()

    override fun getOffset(): Int = offset

    override fun removeCaretListener(listener: CaretListener) = TODO()

    override fun getCaretAt(pos: VisualPosition): Caret? = TODO()

    override fun isUpToDate(): Boolean = TODO()

    override fun getCaretCount(): Int = TODO()

    override fun moveCaretRelatively(
        columnShift: Int,
        lineShift: Int,
        withSelection: Boolean,
        blockSelection: Boolean,
        scrollToCaret: Boolean
    ) {
        TODO()
    }

    override fun setCaretsAndSelections(caretStates: MutableList<CaretState>) = TODO()

    override fun setCaretsAndSelections(caretStates: MutableList<CaretState>, updateSystemSelection: Boolean) {
        TODO()
    }

    override fun getLogicalPosition(): LogicalPosition = TODO()

    override fun addCaretListener(listener: CaretListener) = TODO()

    override fun moveToVisualPosition(pos: VisualPosition) = TODO()

    override fun supportsMultipleCarets(): Boolean = TODO()

    override fun getCaretsAndSelections(): MutableList<CaretState> = TODO()

    override fun getVisualLineEnd(): Int = TODO()

    override fun addCaret(pos: VisualPosition): Caret? = TODO()

    override fun addCaret(pos: VisualPosition, makePrimary: Boolean): Caret? = TODO()

    override fun runForEachCaret(action: CaretAction) = TODO()

    override fun runForEachCaret(action: CaretAction, reverseOrder: Boolean) = TODO()

    override fun getAllCarets(): MutableList<Caret> = TODO()

    override fun addCaretActionListener(listener: CaretActionListener, disposable: Disposable) = TODO()

    override fun runBatchCaretOperation(runnable: Runnable) = TODO()

    override fun moveToLogicalPosition(pos: LogicalPosition) = TODO()

    override fun moveToOffset(offset: Int) = TODO()

    override fun moveToOffset(offset: Int, locateBeforeSoftWrap: Boolean) = TODO()

    override fun getCurrentCaret(): Caret = TODO()

    override fun getVisualPosition(): VisualPosition = TODO()
}