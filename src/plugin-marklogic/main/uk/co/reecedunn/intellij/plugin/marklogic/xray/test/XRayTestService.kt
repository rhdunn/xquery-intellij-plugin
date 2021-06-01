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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.test

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.data.ModificationTrackedProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.vfs.replace
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl
import uk.co.reecedunn.intellij.plugin.xquery.testframework.execution.XQueryTestLocationProvider

class XRayTestService(private val project: Project) {
    private val cachedXrayRootDir = ModificationTrackedProperty<ProjectRootManager, VirtualFile> {
        var rootDir: VirtualFile? = null
        it.fileIndex.iterateContent { vf ->
            if (!vf.isDirectory) return@iterateContent true

            val isXRayDir = vf.name == XRAY_DIR
            val hasIndex = vf.findChild(INDEX_XQY) != null
            val hasSrcXRay = vf.findChild(SRC_DIR)?.findChild(XRAY_XQY) != null
            if (isXRayDir && hasIndex && hasSrcXRay) {
                rootDir = vf
            }
            rootDir == null
        }
        rootDir
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val xrayRootDir: VirtualFile?
        get() = cachedXrayRootDir.get(ProjectRootManager.getInstance(project))

    @Suppress("MemberVisibilityCanBePrivate")
    val xrayModuleRoot: String?
        get() = xrayRootDir?.let { XpmProjectConfigurations.getInstance(project).toModulePath(it) }

    val runTestsQuery: VirtualFile?
        get() = xrayModuleRoot?.let { root ->
            MarkLogicQueries.XRay.RunTests.replace("%XRAY_XQY_PATH%", "$root/src/xray.xqy")
        }

    companion object {
        fun getInstance(project: Project): XRayTestService {
            return project.getService(XRayTestService::class.java)
        }

        const val FRAMEWORK_NAME = "XRay"

        private const val XRAY_DIR = "xray"
        private const val SRC_DIR = "src"

        private const val INDEX_XQY = "index.xqy"
        private const val XRAY_XQY = "xray.xqy"

        private const val TEST_NAMESPACE = "http://github.com/robwhitby/xray/test"
        private val TEST_CASE_LOCAL_NAMES = setOf("case", "ignore")

        // region Directory (Test Path)

        fun isTestDirectory(directory: PsiDirectory): Boolean {
            return XpmProjectConfigurations.getInstance(directory.project).isModulesDirectory(directory)
        }

        // endregion
        // region Module (Test Suite)

        fun isTestModule(file: PsiFile): Boolean {
            val module = (file as? XQueryModule)?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
            return isTestModule(module?.children()?.filterIsInstance<XQueryModuleDecl>()?.firstOrNull())
        }

        fun isTestModule(name: XPathEQName): Boolean = isTestModule(name.parent as? XQueryModuleDecl)

        private fun isTestModule(declaration: XQueryModuleDecl?): Boolean {
            return declaration?.namespaceUri?.data == TEST_NAMESPACE
        }

        fun locationHint(file: PsiFile): String {
            val path = XpmProjectConfigurations.getInstance(file.project).toModulePath(file.virtualFile ?: return "")
            return XQueryTestLocationProvider.locationHint(path)
        }

        // endregion
        // region Function (Test Case)

        fun isTestCase(name: XPathEQName): Boolean = isTestCase(name.parent as? XQueryFunctionDecl)

        private fun isTestCase(function: XQueryFunctionDecl?): Boolean {
            return function?.annotations?.find { isTestCaseAnnotation(it) } != null
        }

        private fun isTestCaseAnnotation(annotation: XdmAnnotation): Boolean {
            val name = annotation.name
            if (name?.localName?.data in TEST_CASE_LOCAL_NAMES) {
                return name!!.expand().find { it.namespace?.data == TEST_NAMESPACE } != null
            }
            return false
        }

        fun locationHint(file: PsiFile, name: XPathEQName): String {
            val path = XpmProjectConfigurations.getInstance(file.project).toModulePath(file.virtualFile ?: return "")
            return XQueryTestLocationProvider.locationHint(name.localName!!.data, path)
        }

        // endregion
    }
}
