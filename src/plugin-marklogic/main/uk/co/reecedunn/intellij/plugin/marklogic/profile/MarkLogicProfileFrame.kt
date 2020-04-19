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
package uk.co.reecedunn.intellij.plugin.marklogic.profile

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition

class MarkLogicProfileFrame(private val frame: XmlElement, query: VirtualFile) : XStackFrame() {
    private val sourcePosition = QuerySourcePosition.create(
        path = frame.child("prof:uri")?.text()?.nullize(),
        context = query,
        line = (frame.child("prof:line")?.text()?.toIntOrNull() ?: 1) - 1,
        column = frame.child("prof:column")?.text()?.toIntOrNull() ?: 0
    )

    override fun getSourcePosition(): XSourcePosition? = sourcePosition
}
