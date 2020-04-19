/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Location
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo

class SaxonStackFrame(location: Location, query: VirtualFile) : XStackFrame() {
    private val sourcePosition = QuerySourcePosition.create(
        path = location.systemId,
        context = query,
        line = location.lineNumber.let { if (it == -1) 1 else it } - 1,
        column = location.columnNumber.let { if (it == -1) 1 else it } - 1
    )

    override fun getSourcePosition(): XSourcePosition? = sourcePosition

    val context: String? = (location as? InstructionInfo)?.getObjectName()?.toString()

    override fun customizePresentation(component: ColoredTextContainer) {
        component.append(sourcePosition!!.file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        component.append(":", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        component.append(sourcePosition.line.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        context?.let {
            component.append(", ", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            component.append(it, SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES)
        }

        component.setIcon(AllIcons.Debugger.Frame)
    }
}
