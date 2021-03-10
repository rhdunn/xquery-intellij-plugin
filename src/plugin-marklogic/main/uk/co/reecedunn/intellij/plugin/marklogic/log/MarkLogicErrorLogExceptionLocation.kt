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
package uk.co.reecedunn.intellij.plugin.marklogic.log

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath

class MarkLogicErrorLogExceptionLocation(
    date: String,
    time: String,
    logLevel: String,
    appServer: String?,
    continuation: Boolean,
    val path: String,
    val line: Int,
    val column: Int,
    val xqueryVersion: String?
) : MarkLogicErrorLogLine(date, time, logLevel, appServer, continuation) {

    private fun getSourcePosition(project: Project): QuerySourcePosition? {
        val location = XpmModuleLocationPath.create(project, path, arrayOf()) ?: return null
        val file = XpmModuleLoaderSettings.getInstance(project).resolve(location, null)?.containingFile?.virtualFile
            ?: return null
        return QuerySourcePosition.create(file, line, column)
    }

    override val message: String
        get() = when (xqueryVersion) {
            null -> "in $path, at $line:$column"
            else -> "in $path, at $line:$column [$xqueryVersion]"
        }

    override fun print(consoleView: ConsoleView) {
        consoleView.print("$date $time ", LogFileContentType.DATE_TIME)

        val separator = if (continuation) '+' else ' '
        when (appServer) {
            null -> consoleView.print("$logLevel:${separator}in ", contentType)
            else -> consoleView.print("$logLevel:$separator$appServer: in", contentType)
        }

        consoleView.printHyperlink(path) {
            val nav = getSourcePosition(it)?.createNavigatable(it)
            if (nav?.canNavigate() == true) {
                nav.navigate(true)
            }
        }

        when (xqueryVersion) {
            null -> consoleView.print(", at $line:$column", contentType)
            else -> consoleView.print(", at $line:$column [$xqueryVersion]", contentType)
        }
    }
}
