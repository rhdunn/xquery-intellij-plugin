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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement

class MarkLogicDebugFrame(private val frame: XmlElement, private val query: VirtualFile) : XStackFrame() {
    private fun findFileByPath(path: String?): VirtualFile? {
        return if (path == null)
            query
        else
            query.findFileByRelativePath(path)
    }

    private val sourcePosition = CacheableProperty {
        val uri = frame.child("dbg:uri")?.text()?.nullize()

        val file = uri?.let { findFileByPath(it) } ?: query
        val line = frame.child("dbg:line")?.text()?.toIntOrNull() ?: 1
        XDebuggerUtil.getInstance().createPosition(file, line - 1, 0)
    }

    override fun getSourcePosition(): XSourcePosition? = sourcePosition.get()
}
