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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty

abstract class QueryStackFrame(private val query: VirtualFile) : XStackFrame() {
    abstract val uri: String?
    abstract val line: Int
    abstract val column: Int
    abstract val context: String?

    val file: VirtualFile get() = sourcePosition!!.file

    private fun findFileByPath(path: String?): VirtualFile? {
        return if (path == null)
            query
        else
            query.findFileByRelativePath(path)
    }

    private val cachedSourcePosition = CacheableProperty {
        val file = uri?.let { findFileByPath(it) } ?: query
        XDebuggerUtil.getInstance().createPosition(file, line - 1, column - 1)
    }

    override fun getSourcePosition(): XSourcePosition? = cachedSourcePosition.get()

    override fun customizePresentation(component: ColoredTextContainer) {
        component.append(file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        component.append(":", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        component.append(line.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        context?.let {
            component.append(", ", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            component.append(it, SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES)
        }

        component.setIcon(AllIcons.Debugger.Frame)
    }
}
