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
package uk.co.reecedunn.intellij.plugin.core.psi

import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.originalFile

private fun PsiFile.resourcePath(): String {
    return ancestorsAndSelf().map { (it as PsiFileSystemItem).name }.toList().reversed().joinToString("/")
}

fun PsiElement.resourcePath(): String {
    val file = containingFile.virtualFile?.originalFile ?: return containingFile.resourcePath()
    return file.path.replace('\\', '/')
}

// The equivalent of this is available in IntelliJ 2019.3 which cannot be used due to
// support for older IntelliJ versions.
val PsiElement?.elementType: IElementType?
    get() = when (this) {
        null -> null
        is StubBasedPsiElement<*> -> this.elementType
        is PsiFile -> this.fileElementType
        else -> node?.elementType
    }

inline fun <reified T : PsiElement> PsiElement.contextOfType(strict: Boolean = true): T? {
    return PsiTreeUtil.getContextOfType(this, T::class.java, strict)
}

fun <T> PsiElement.createElement(text: String, `class`: Class<T>): T? {
    val file = PsiFileFactory.getInstance(project).createFileFromText(text, containingFile)
    return file?.walkTree()?.filterIsInstance(`class`)?.firstOrNull()
}

inline fun <reified T> PsiElement.createElement(text: String): T? = createElement(text, T::class.java)
