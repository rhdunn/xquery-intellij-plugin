/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class MockASTNode(private val mElementType: IElementType) : ASTNode {
    override fun getElementType(): IElementType = mElementType

    override fun getText(): String = TODO()

    override fun getChars(): CharSequence = TODO()

    override fun textContains(c: Char): Boolean = false

    override fun getStartOffset(): Int = 0

    override fun getTextLength(): Int = 0

    override fun getTextRange(): TextRange? = null

    override fun getTreeParent(): ASTNode? = null

    override fun getFirstChildNode(): ASTNode? = null

    override fun getLastChildNode(): ASTNode? = null

    override fun getTreeNext(): ASTNode? = null

    override fun getTreePrev(): ASTNode? = null

    override fun getChildren(filter: TokenSet?): Array<ASTNode> = arrayOf()

    override fun addChild(child: ASTNode) {}

    override fun addChild(child: ASTNode, anchorBefore: ASTNode?) {}

    override fun addLeaf(leafType: IElementType, leafText: CharSequence, anchorBefore: ASTNode?) {}

    override fun removeChild(child: ASTNode) {}

    override fun removeRange(firstNodeToRemove: ASTNode, firstNodeToKeep: ASTNode) {}

    override fun replaceChild(oldChild: ASTNode, newChild: ASTNode) {}

    override fun replaceAllChildrenToChildrenOf(anotherParent: ASTNode) {}

    override fun addChildren(firstChild: ASTNode, firstChildToNotAdd: ASTNode, anchorBefore: ASTNode) {}

    override fun clone(): Any = TODO()

    override fun copyElement(): ASTNode? = null

    override fun findLeafElementAt(offset: Int): ASTNode? = null

    override fun <T> getCopyableUserData(key: Key<T>): T? = null

    override fun <T> putCopyableUserData(key: Key<T>, value: T) {}

    override fun findChildByType(type: IElementType): ASTNode? = null

    override fun findChildByType(type: IElementType, anchor: ASTNode?): ASTNode? = null

    override fun findChildByType(typesSet: TokenSet): ASTNode? = null

    override fun findChildByType(typesSet: TokenSet, anchor: ASTNode?): ASTNode? = null

    override fun getPsi(): PsiElement? = null

    override fun <T : PsiElement> getPsi(clazz: Class<T>): T? = null

    override fun <T> getUserData(key: Key<T>): T? = null

    override fun <T> putUserData(key: Key<T>, value: T?) {}
}
