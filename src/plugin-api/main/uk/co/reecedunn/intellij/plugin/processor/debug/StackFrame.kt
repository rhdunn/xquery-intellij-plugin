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
package uk.co.reecedunn.intellij.plugin.processor.debug

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule

data class StackFrame(
    val module: VirtualFile?,
    val lineNumber: Int,
    val columnNumber: Int
)

fun StackFrame.getSourcePosition(): XSourcePosition? {
    return when (module) {
        is DatabaseModule -> null
        else -> XDebuggerUtil.getInstance().createPosition(module, lineNumber - 1, columnNumber - 1)
    }
}

fun StackFrame.createNavigatable(project: Project): Navigatable? {
    return getSourcePosition()?.createNavigatable(project)
}
