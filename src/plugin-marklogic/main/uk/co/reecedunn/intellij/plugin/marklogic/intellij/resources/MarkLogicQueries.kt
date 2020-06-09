/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources

import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode

object MarkLogicQueries {
    private fun resourceFile(path: String): VirtualFile {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, path)
        file.charset = CharsetToolkit.UTF8_CHARSET
        return file
    }

    val Run = resourceFile("queries/marklogic/run.xq").decode()!!

    val Version = resourceFile("queries/marklogic/version.xq")
    val Servers = resourceFile("queries/marklogic/servers.xq")
    val Databases = resourceFile("queries/marklogic/databases.xq")

    val ApiDocs = resourceFile("queries/marklogic/apidocs.xq")

    object Debug {
        val Breakpoint = resourceFile("queries/marklogic/debug/breakpoint.xq")
        val Break = resourceFile("queries/marklogic/debug/break.xq")
        val Continue = resourceFile("queries/marklogic/debug/continue.xq")
        val Stack = resourceFile("queries/marklogic/debug/stack.xq")
        val Status = resourceFile("queries/marklogic/debug/status.xq")
        val StepInto = resourceFile("queries/marklogic/debug/step-into.xq")
        val StepOver = resourceFile("queries/marklogic/debug/step-over.xq")
        val StepOut = resourceFile("queries/marklogic/debug/step-out.xq")
        val Value = resourceFile("queries/marklogic/debug/value.xq")
    }

    object Log {
        val Logs = resourceFile("queries/marklogic/log/logs.xq")
        val Log = resourceFile("queries/marklogic/log/log.xq")
    }

    object Request {
        val Cancel = resourceFile("queries/marklogic/request/cancel.xq")
    }
}
