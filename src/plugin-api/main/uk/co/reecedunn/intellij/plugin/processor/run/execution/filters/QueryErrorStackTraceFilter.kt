/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.run.execution.filters

import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.xdebugger.XSourcePosition
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath

class QueryErrorStackTraceFilter(private val project: Project) : Filter {
    private val fileCache = hashMapOf<String, VirtualFile?>()

    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        return SOURCE_LINE.find(line)?.let { match ->
            val path = match.groupValues[1]
            val lineNumber = match.groupValues[2]
            val columnNumber = match.groupValues.getOrNull(4)

            val navigatable = createPosition(path, lineNumber, columnNumber)?.createNavigatable(project)
            if (navigatable?.canNavigate() == true) {
                val start = entireLength - line.length + 4
                val end = start + path.length
                Filter.Result(start, end) { navigatable.navigate(true) }
            } else {
                null
            }
        }
    }

    private fun getVirtualFile(path: String): VirtualFile? = when (val cached = fileCache[path]) {
        null -> XpmModuleLocationPath.create(project, path, XdmModuleType.MODULE)?.let { location ->
            val file = XpmModuleLoaderSettings.getInstance(project).resolve(location, null) as? PsiFile
            fileCache[path] = file?.virtualFile
            file?.virtualFile
        }
        else -> cached
    }

    private fun createPosition(path: String, line: String, column: String?): XSourcePosition? {
        return QuerySourcePosition.create(getVirtualFile(path), line.toInt() - 1, column?.toInt()?.minus(1))
    }

    companion object {
        private val SOURCE_LINE = "^\tat ([^:]+):([0-9]+)(:([0-9]+))?$".toRegex()
    }
}
