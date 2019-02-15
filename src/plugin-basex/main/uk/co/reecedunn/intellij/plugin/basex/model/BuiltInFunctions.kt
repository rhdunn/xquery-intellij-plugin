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
package uk.co.reecedunn.intellij.plugin.basex.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.xpath.model.ImportPathResolver

private object BaseXBuiltInFunctionFileSystem : VirtualFileSystemImpl("res") {
    override fun findCacheableFile(path: String): VirtualFile? {
        return ResourceVirtualFile(this::class.java.classLoader, path, this)
    }
}

object BuiltInFunctions : ImportPathResolver {
    override fun match(path: String): Boolean = MODULES.containsKey(path)

    override fun resolve(path: String): VirtualFile? {
        return MODULES[path]?.let { BaseXBuiltInFunctionFileSystem.findFileByPath(it) }
    }

    private val MODULES = mapOf(
        "http://basex.org/modules/admin" to "builtin/basex.org/modules/admin.xqy",
        "http://basex.org/modules/archive" to "builtin/basex.org/modules/archive.xqy",
        "http://basex.org/modules/client" to "builtin/basex.org/modules/client.xqy",
        "http://basex.org/modules/convert" to "builtin/basex.org/modules/convert.xqy",
        "http://basex.org/modules/csv" to "builtin/basex.org/modules/csv.xqy",
        "http://basex.org/modules/db" to "builtin/basex.org/modules/db.xqy",
        "http://basex.org/modules/fetch" to "builtin/basex.org/modules/fetch.xqy",
        "http://basex.org/modules/ft" to "builtin/basex.org/modules/ft.xqy",
        "http://basex.org/modules/hash" to "builtin/basex.org/modules/hash.xqy",
        "http://basex.org/modules/hof" to "builtin/basex.org/modules/hof.xqy",
        "http://basex.org/modules/html" to "builtin/basex.org/modules/html.xqy",
        "http://basex.org/modules/index" to "builtin/basex.org/modules/index.xqy",
        "http://basex.org/modules/inspect" to "builtin/basex.org/modules/inspect.xqy",
        "http://basex.org/modules/jobs" to "builtin/basex.org/modules/jobs.xqy",
        "http://basex.org/modules/json" to "builtin/basex.org/modules/json.xqy",
        "http://basex.org/modules/lazy" to "builtin/basex.org/modules/lazy.xqy",
        "http://basex.org/modules/out" to "builtin/basex.org/modules/out.xqy",
        "http://basex.org/modules/proc" to "builtin/basex.org/modules/proc.xqy",
        "http://basex.org/modules/prof" to "builtin/basex.org/modules/prof.xqy",
        "http://basex.org/modules/random" to "builtin/basex.org/modules/random.xqy",
        "http://basex.org/modules/repo" to "builtin/basex.org/modules/repo.xqy",
        "http://basex.org/modules/session" to "builtin/basex.org/modules/session.xqy",
        "http://basex.org/modules/sessions" to "builtin/basex.org/modules/sessions.xqy",
        "http://basex.org/modules/sql" to "builtin/basex.org/modules/sql.xqy",
        "http://basex.org/modules/stream" to "builtin/basex.org/modules/stream.xqy",
        "http://basex.org/modules/strings" to "builtin/basex.org/modules/strings.xqy",
        "http://basex.org/modules/unit" to "builtin/basex.org/modules/unit.xqy",
        "http://basex.org/modules/update" to "builtin/basex.org/modules/update.xqy",
        "http://basex.org/modules/user" to "builtin/basex.org/modules/user.xqy",
        "http://basex.org/modules/util" to "builtin/basex.org/modules/util.xqy",
        "http://basex.org/modules/validate" to "builtin/basex.org/modules/validate.xqy",
        "http://basex.org/modules/web" to "builtin/basex.org/modules/web.xqy",
        "http://basex.org/modules/ws" to "builtin/basex.org/modules/ws.xqy",
        "http://basex.org/modules/xquery" to "builtin/basex.org/modules/xquery.xqy",
        "http://basex.org/modules/xslt" to "builtin/basex.org/modules/xslt.xqy"
    )
}
