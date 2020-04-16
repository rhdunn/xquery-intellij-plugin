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
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.QueryStackFrame

class MarkLogicDebugFrame(frame: XmlElement, query: VirtualFile) : QueryStackFrame(query) {
    override val uri: String? = frame.child("dbg:uri")?.text()?.nullize()
    override val line: Int = frame.child("dbg:line")?.text()?.toIntOrNull() ?: 1
    override val column: Int = frame.child("dbg:column")?.text()?.toIntOrNull() ?: 1
}
