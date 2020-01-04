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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.ZipFileSystem
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationIndex
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationReference
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

data class ApiDocs(private val filesystem: VirtualFileSystem, private val root: VirtualFile) : XdmDocumentationIndex {
    // region XdmDocumentationIndex

    override fun invalidate() {}

    override fun lookup(ref: XdmFunctionReference): XdmDocumentationReference? {
        return null
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XdmDocumentationReference? {
        return modules.find { it.namespaceUri == decl.namespaceUri?.data }
    }

    // endregion

    val modules: List<ApiDocsModule> by lazy {
        root.children[0].findFileByRelativePath("pubs/raw/apidoc")!!.children.asSequence().map {
            if (it.name.endsWith(".xml")) {
                val xml = XmlDocument.parse(it, NAMESPACES)
                when {
                    xml.root.`is`("apidoc:module") -> ApiDocsModule(xml.root)
                    else -> null
                }
            } else
                null
        }.filterNotNull().toList()
    }

    companion object {
        private val NAMESPACES = mapOf(
            "apidoc" to "http://marklogic.com/xdmp/apidoc"
        )

        fun create(docs: VirtualFile): ApiDocs {
            return if (docs.isDirectory) {
                ApiDocs(docs.fileSystem, docs)
            } else
                create(ZipFileSystem(docs.inputStream)) // contentsToByteArray can throw a file too big exception.
        }

        fun create(pkg: ByteArray): ApiDocs = create(ZipFileSystem(pkg))

        private fun create(pkg: ZipFileSystem): ApiDocs = ApiDocs(pkg, pkg.findFileByPath("")!!)
    }
}
