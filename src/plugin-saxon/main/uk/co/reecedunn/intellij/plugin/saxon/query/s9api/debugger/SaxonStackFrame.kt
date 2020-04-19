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

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.QueryStackFrame
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Location
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo

class SaxonStackFrame(location: Location, query: VirtualFile) : QueryStackFrame(query) {
    override val uri: String? = location.systemId
    override val line: Int = location.lineNumber.let { if (it == -1) 1 else it }
    override val column: Int = location.columnNumber.let { if (it == -1) 1 else it }
    override val context: String? = (location as? InstructionInfo)?.getObjectName()?.toString()
}
