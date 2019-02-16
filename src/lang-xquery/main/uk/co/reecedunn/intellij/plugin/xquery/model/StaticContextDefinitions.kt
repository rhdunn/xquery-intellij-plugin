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
package uk.co.reecedunn.intellij.plugin.xquery.model

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.xpath.model.ImportPathResolver

private object XQueryBuiltInModuleFileSystem : VirtualFileSystemImpl("res") {
    override fun findCacheableFile(path: String): VirtualFile? {
        return ResourceVirtualFile(this::class.java.classLoader, path, this)
    }
}

object StaticContextDefinitions : ImportPathResolver {
    override fun match(path: String): Boolean = MODULES.containsKey(path)

    override fun resolve(path: String): VirtualFile? {
        return MODULES[path]?.let { XQueryBuiltInModuleFileSystem.findFileByPath(it) }
    }

    private val MODULES = mapOf(
        "urn:static-context:basex" to "static-context/basex.org/xquery.xqy",
        "urn:static-context:exist-db" to "static-context/exist-db.org/xquery.xqy",
        "urn:static-context:marklogic:0.9-ml" to "static-context/marklogic.com/0.9-ml.xqy",
        "urn:static-context:marklogic:1.0" to "static-context/marklogic.com/1.0.xqy",
        "urn:static-context:marklogic:1.0-ml" to "static-context/marklogic.com/1.0-ml.xqy",
        "urn:static-context:saxon" to "static-context/saxon.sf.net/xquery.xqy",
        "urn:static-context:w3" to "static-context/www.w3.org/xquery.xqy"
    )
}
