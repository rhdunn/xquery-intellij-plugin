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
package uk.co.reecedunn.intellij.plugin.existdb.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.xpath.model.ImportPathResolver

private object EXistDBBuiltInFunctionFileSystem : VirtualFileSystemImpl("res") {
    override fun findCacheableFile(path: String): VirtualFile? {
        return ResourceVirtualFile(this::class.java.classLoader, path, this)
    }
}

object BuiltInFunctions : ImportPathResolver {
    override fun match(path: String): Boolean = MODULES.containsKey(path)

    override fun resolve(path: String): VirtualFile? {
        return MODULES[path]?.let { EXistDBBuiltInFunctionFileSystem.findFileByPath(it) }
    }

    private val MODULES = mapOf(
        "http://exist-db.org/xquery/compression" to "builtin/exist-db.org/xquery/compression.xqy",
        "http://exist-db.org/xquery/console" to "builtin/exist-db.org/xquery/console.xqy",
        "http://exist-db.org/xquery/contextextraction" to "builtin/exist-db.org/xquery/contextextraction.xqy",
        "http://exist-db.org/xquery/counter" to "builtin/exist-db.org/xquery/counter.xqy",
        "http://exist-db.org/xquery/datetime" to "builtin/exist-db.org/xquery/datetime.xqy",
        "http://exist-db.org/xquery/example" to "builtin/exist-db.org/xquery/example.xqy",
        "http://exist-db.org/xquery/file" to "builtin/exist-db.org/xquery/file.xqy",
        "http://exist-db.org/xquery/httpclient" to "builtin/exist-db.org/xquery/httpclient.xqy",
        "http://exist-db.org/xquery/image" to "builtin/exist-db.org/xquery/image.xqy",
        "http://exist-db.org/xquery/inspect" to "builtin/exist-db.org/xquery/inspect.xqy",
        "http://exist-db.org/xquery/lucene" to "builtin/exist-db.org/xquery/lucene.xqy",
        "http://exist-db.org/xquery/mail" to "builtin/exist-db.org/xquery/mail.xqy",
        "http://exist-db.org/xquery/math" to "builtin/exist-db.org/xquery/math.xqy",
        "http://exist-db.org/xquery/ngram" to "builtin/exist-db.org/xquery/ngram.xqy",
        "http://exist-db.org/xquery/process" to "builtin/exist-db.org/xquery/process.xqy",
        "http://exist-db.org/xquery/range" to "builtin/exist-db.org/xquery/range.xqy",
        "http://exist-db.org/xquery/repo" to "builtin/exist-db.org/xquery/repo.xqy",
        "http://exist-db.org/xquery/request" to "builtin/exist-db.org/xquery/request.xqy",
        "http://exist-db.org/xquery/response" to "builtin/exist-db.org/xquery/response.xqy",
        "http://exist-db.org/xquery/scheduler" to "builtin/exist-db.org/xquery/scheduler.xqy",
        "http://exist-db.org/xquery/securitymanager" to "builtin/exist-db.org/xquery/securitymanager.xqy",
        "http://exist-db.org/xquery/session" to "builtin/exist-db.org/xquery/session.xqy",
        "http://exist-db.org/xquery/sort" to "builtin/exist-db.org/xquery/sort.xqy",
        "http://exist-db.org/xquery/sql" to "builtin/exist-db.org/xquery/sql.xqy",
        "http://exist-db.org/xquery/system" to "builtin/exist-db.org/xquery/system.xqy",
        "http://exist-db.org/xquery/transform" to "builtin/exist-db.org/xquery/transform.xqy",
        "http://exist-db.org/xquery/util" to "builtin/exist-db.org/xquery/util.xqy",
        "http://exist-db.org/xquery/validation" to "builtin/exist-db.org/xquery/validation.xqy",
        "http://exist-db.org/xquery/xmldb" to "builtin/exist-db.org/xquery/xmldb.xqy",
        "http://exist-db.org/xquery/xmldiff" to "builtin/exist-db.org/xquery/xmldiff.xqy",
        "http://exist-db.org/xquery/xqdoc" to "builtin/exist-db.org/xquery/xqdoc.xqy",
        "http://exist-db.org/xquery/xslfo" to "builtin/exist-db.org/xquery/xslfo.xqy"
    )
}
