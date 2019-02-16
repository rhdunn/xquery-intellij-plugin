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
package uk.co.reecedunn.intellij.plugin.marklogic.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.xpath.model.ImportPathResolver

private object MarkLogicBuiltInFunctionFileSystem : VirtualFileSystemImpl("res") {
    override fun findCacheableFile(path: String): VirtualFile? {
        return ResourceVirtualFile(this::class.java.classLoader, path, this)
    }
}

object BuiltInFunctions : ImportPathResolver {
    override fun match(path: String): Boolean = MODULES.containsKey(path)

    override fun resolve(path: String): VirtualFile? {
        return MODULES[path]?.let { MarkLogicBuiltInFunctionFileSystem.findFileByPath(it) }
    }

    private val MODULES = mapOf(
        "http://marklogic.com/cts" to "builtin/marklogic.com/cts.xqy",
        "http://marklogic.com/geospatial" to "builtin/marklogic.com/geospatial.xqy",
        "http://marklogic.com/xdmp" to "builtin/marklogic.com/xdmp.xqy",
        "http://marklogic.com/xdmp/dbg" to "builtin/marklogic.com/xdmp/dbg.xqy",
        "http://marklogic.com/xdmp/json" to "builtin/marklogic.com/xdmp/json.xqy",
        "http://marklogic.com/xdmp/map" to "builtin/marklogic.com/xdmp/map.xqy",
        "http://marklogic.com/xdmp/math" to "builtin/marklogic.com/xdmp/math.xqy",
        "http://marklogic.com/xdmp/profile" to "builtin/marklogic.com/xdmp/profile.xqy",
        "http://marklogic.com/xdmp/schema-components" to "builtin/marklogic.com/xdmp/schema-components.xqy",
        "http://marklogic.com/xdmp/semantics" to "builtin/marklogic.com/xdmp/semantics.xqy",
        "http://marklogic.com/xdmp/spell" to "builtin/marklogic.com/xdmp/spell.xqy",
        "http://marklogic.com/xdmp/sql" to "builtin/marklogic.com/xdmp/sql.xqy",
        "http://marklogic.com/xdmp/tde" to "builtin/marklogic.com/xdmp/tde.xqy",
        "http://marklogic.com/xdmp/temporal" to "builtin/marklogic.com/xdmp/temporal.xqy",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#" to "builtin/www.w3.org/1999/02/22-rdf-syntax-ns.xqy"
    )
}
