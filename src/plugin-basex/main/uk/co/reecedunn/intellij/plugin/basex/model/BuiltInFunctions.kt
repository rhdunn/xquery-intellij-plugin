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
        "http://basex.org/modules/admin" to "org/basex/modules/admin.xqy",
        "http://basex.org/modules/archive" to "org/basex/modules/archive.xqy",
        "http://basex.org/modules/client" to "org/basex/modules/client.xqy",
        "http://basex.org/modules/convert" to "org/basex/modules/convert.xqy",
        "http://basex.org/modules/csv" to "org/basex/modules/csv.xqy",
        "http://basex.org/modules/db" to "org/basex/modules/db.xqy",
        "http://basex.org/modules/fetch" to "org/basex/modules/fetch.xqy",
        "http://basex.org/modules/ft" to "org/basex/modules/ft.xqy",
        "http://basex.org/modules/hash" to "org/basex/modules/hash.xqy",
        "http://basex.org/modules/hof" to "org/basex/modules/hof.xqy",
        "http://basex.org/modules/html" to "org/basex/modules/html.xqy",
        "http://basex.org/modules/index" to "org/basex/modules/index.xqy",
        "http://basex.org/modules/inspect" to "org/basex/modules/inspect.xqy",
        "http://basex.org/modules/jobs" to "org/basex/modules/jobs.xqy",
        "http://basex.org/modules/json" to "org/basex/modules/json.xqy",
        "http://basex.org/modules/lazy" to "org/basex/modules/lazy.xqy",
        "http://basex.org/modules/out" to "org/basex/modules/out.xqy",
        "http://basex.org/modules/proc" to "org/basex/modules/proc.xqy",
        "http://basex.org/modules/prof" to "org/basex/modules/prof.xqy",
        "http://basex.org/modules/random" to "org/basex/modules/random.xqy",
        "http://basex.org/modules/repo" to "org/basex/modules/repo.xqy",
        "http://basex.org/modules/session" to "org/basex/modules/session.xqy",
        "http://basex.org/modules/sessions" to "org/basex/modules/sessions.xqy",
        "http://basex.org/modules/sql" to "org/basex/modules/sql.xqy",
        "http://basex.org/modules/stream" to "org/basex/modules/stream.xqy",
        "http://basex.org/modules/strings" to "org/basex/modules/strings.xqy",
        "http://basex.org/modules/unit" to "org/basex/modules/unit.xqy",
        "http://basex.org/modules/update" to "org/basex/modules/update.xqy",
        "http://basex.org/modules/user" to "org/basex/modules/user.xqy",
        "http://basex.org/modules/util" to "org/basex/modules/util.xqy",
        "http://basex.org/modules/validate" to "org/basex/modules/validate.xqy",
        "http://basex.org/modules/web" to "org/basex/modules/web.xqy",
        "http://basex.org/modules/ws" to "org/basex/modules/ws.xqy",
        "http://basex.org/modules/xquery" to "org/basex/modules/xquery.xqy",
        "http://basex.org/modules/xslt" to "org/basex/modules/xslt.xqy"
    )
}
