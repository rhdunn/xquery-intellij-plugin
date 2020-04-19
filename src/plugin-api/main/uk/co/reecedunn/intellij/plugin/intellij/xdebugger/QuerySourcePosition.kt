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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.impl.QuerySourcePositionImpl
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule

interface QuerySourcePosition : XSourcePosition {
    val column: Int

    companion object {
        fun create(file: VirtualFile, line: Int, column: Int): QuerySourcePosition? {
            val position = XDebuggerUtil.getInstance().createPosition(file, line, column) ?: return null
            return QuerySourcePositionImpl(position, column)
        }

        fun create(path: String?, context: VirtualFile, line: Int, column: Int): QuerySourcePosition? {
            val file = path?.let {
                when (context) {
                    is DatabaseModule -> null
                    else -> context.findFileByRelativePath(it)
                } ?: DatabaseModule(it)
            } ?: context
            return create(file, line, column)
        }
    }
}
