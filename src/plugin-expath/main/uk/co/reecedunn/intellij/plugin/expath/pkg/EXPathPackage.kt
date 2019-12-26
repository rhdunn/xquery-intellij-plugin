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
package uk.co.reecedunn.intellij.plugin.expath.pkg

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.ZipFileSystem
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class EXPathPackage internal constructor(private val descriptor: XmlDocument) {
    val name: XsAnyUriValue? by lazy {
        descriptor.root.attribute("name")?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE, null as? PsiElement?)
        }
    }

    val abbrev: String? by lazy { descriptor.root.attribute("abbrev") }

    val version: String? by lazy { descriptor.root.attribute("version") }

    val spec: String? by lazy { descriptor.root.attribute("spec") }

    val title: String? by lazy { descriptor.root.children("title").firstOrNull()?.text() }

    val home: XsAnyUriValue? by lazy {
        descriptor.root.children("home").firstOrNull()?.text()?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE, null as? PsiElement?)
        }
    }

    val dependencies: List<EXPathPackageDependency> by lazy {
        descriptor.root.children("dependency").map { EXPathPackageDependency(it) }.toList()
    }

    val components: List<EXPathPackageComponent> by lazy {
        descriptor.root.children().mapNotNull {
            when (it.element.localName) {
                "dtd" -> EXPathPackageDtd(it)
                "nvdl" -> EXPathPackageImport(it, XdmModuleType.NVDL)
                "resource" -> EXPathPackageResource(it)
                "rnc" -> EXPathPackageImport(it, XdmModuleType.RelaxNGCompact)
                "rng" -> EXPathPackageImport(it, XdmModuleType.RelaxNG)
                "schematron" -> EXPathPackageImport(it, XdmModuleType.Schematron)
                "xproc" -> EXPathPackageImport(it, XdmModuleType.XProc)
                "xquery" -> EXPathPackageImport(it, XdmModuleType.XQuery)
                "xsd" -> EXPathPackageImport(it, XdmModuleType.XMLSchema)
                "xslt" -> EXPathPackageImport(it, XdmModuleType.XSLT)
                else -> null
            }
        }.toList()
    }

    companion object {
        private val NAMESPACES = mapOf("pkg" to "http://expath.org/ns/pkg")

        private const val DESCRIPTOR_FILE = "expath-pkg.xml"

        fun create(pkg: VirtualFile): EXPathPackage {
            return if (pkg.isDirectory) {
                val descriptor = pkg.findFileByRelativePath(DESCRIPTOR_FILE)?.let { XmlDocument.parse(it, NAMESPACES) }
                    ?: throw EXPathPackageMissingDescriptor()
                EXPathPackage(descriptor)
            } else
                create(ZipFileSystem(pkg))
        }

        fun create(pkg: VirtualFileSystem): EXPathPackage {
            val descriptor = pkg.findFileByPath(DESCRIPTOR_FILE)?.let { XmlDocument.parse(it, NAMESPACES) }
                ?: throw EXPathPackageMissingDescriptor()
            return EXPathPackage(descriptor)
        }
    }
}

class EXPathPackageMissingDescriptor : Exception("Cannot find expath-pkg.xml in the EXPath package.")
