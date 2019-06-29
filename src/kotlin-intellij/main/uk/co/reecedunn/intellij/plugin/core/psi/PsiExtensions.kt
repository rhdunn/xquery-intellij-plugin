/*
 * Copyright (C) 2018 Reece H. Dunn
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

import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.tree.IElementType

fun ASTDelegatePsiElement.contains(type: IElementType): Boolean {
    return node.findChildByType(type) != null
}

private fun prettyPrintASTNode(prettyPrinted: StringBuilder, node: ASTNode, depth: Int) {
    for (i in 0 until depth) {
        prettyPrinted.append("   ")
    }

    val names = node.psi.javaClass.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    prettyPrinted.append(names[names.size - 1].replace("PsiImpl", "Impl"))

    prettyPrinted.append('[')
    prettyPrinted.append(node.elementType)
    prettyPrinted.append('(')
    prettyPrinted.append(node.textRange.startOffset)
    prettyPrinted.append(':')
    prettyPrinted.append(node.textRange.endOffset)
    prettyPrinted.append(')')
    prettyPrinted.append(']')
    if (node is LeafElement || node is PsiErrorElement) {
        prettyPrinted.append('(')
        prettyPrinted.append('\'')
        if (node is PsiErrorElement) {
            val error = node as PsiErrorElement
            prettyPrinted.append(error.errorDescription)
        } else {
            prettyPrinted.append(node.text.replace("\n", "\\n"))
        }
        prettyPrinted.append('\'')
        prettyPrinted.append(')')
    }
    prettyPrinted.append('\n')

    for (child in node.getChildren(null)) {
        prettyPrintASTNode(prettyPrinted, child, depth + 1)
    }
}

fun PsiElement.toPsiTreeString(): String {
    val prettyPrinted = StringBuilder()
    prettyPrintASTNode(prettyPrinted, node, 0)
    return prettyPrinted.toString()
}
