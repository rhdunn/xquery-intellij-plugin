/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.testFramework.LightVirtualFileBase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryUriLiteralReference

class XQueryUriLiteralPsiImpl(node: ASTNode): XQueryStringLiteralPsiImpl(node), XQueryUriLiteral {
    override fun getReference(): PsiReference {
        val range = textRange
        return XQueryUriLiteralReference(this, TextRange(1, range.length - 1))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: PsiFile> resolveUri(): T? {
        val path = atomicValue?.toString()
        if (path == null || path.contains("://")) {
            return ResourceVirtualFile.resolve(path, project) as? T
        }

        var file = containingFile.virtualFile
        if (file is LightVirtualFileBase) {
            file = file.originalFile
        }

        return resolveFileByPath(file, project, path) as? T
    }

    private fun resolveFileByPath(parent: VirtualFile?, project: Project, path: String): PsiFile? {
        if (parent == null) {
            return null
        }

        val file = parent.findFileByRelativePath(path)
        if (file != null) {
            return PsiManager.getInstance(project).findFile(file)
        }

        return resolveFileByPath(parent.parent, project, path)
    }
}
