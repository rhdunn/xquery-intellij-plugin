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
package uk.co.reecedunn.intellij.plugin.xquery.tests.mocks;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"CloneDoesntCallSuperClone", "SameParameterValue"})
public class MockASTNode implements ASTNode {
    private final IElementType mElementType;

    public MockASTNode(IElementType elementType) {
        mElementType = elementType;
    }

    @NotNull
    @Override
    public IElementType getElementType() {
        return mElementType;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public String getText() {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public CharSequence getChars() {
        return null;
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    public int getStartOffset() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Override
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public ASTNode getTreeParent() {
        return null;
    }

    @Override
    public ASTNode getFirstChildNode() {
        return null;
    }

    @Override
    public ASTNode getLastChildNode() {
        return null;
    }

    @Override
    public ASTNode getTreeNext() {
        return null;
    }

    @Override
    public ASTNode getTreePrev() {
        return null;
    }

    @NotNull
    @Override
    public ASTNode[] getChildren(@Nullable TokenSet filter) {
        return new ASTNode[0];
    }

    @Override
    public void addChild(@NotNull ASTNode child) {
    }

    @Override
    public void addChild(@NotNull ASTNode child, @Nullable ASTNode anchorBefore) {
    }

    @Override
    public void addLeaf(@NotNull IElementType leafType, CharSequence leafText, @Nullable ASTNode anchorBefore) {
    }

    @Override
    public void removeChild(@NotNull ASTNode child) {
    }

    @Override
    public void removeRange(@NotNull ASTNode firstNodeToRemove, ASTNode firstNodeToKeep) {
    }

    @Override
    public void replaceChild(@NotNull ASTNode oldChild, @NotNull ASTNode newChild) {
    }

    @Override
    public void replaceAllChildrenToChildrenOf(ASTNode anotherParent) {
    }

    @Override
    public void addChildren(ASTNode firstChild, ASTNode firstChildToNotAdd, ASTNode anchorBefore) {
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public Object clone() {
        return null;
    }

    @Override
    public ASTNode copyElement() {
        return null;
    }

    @Nullable
    @Override
    public ASTNode findLeafElementAt(int offset) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putCopyableUserData(@NotNull Key<T> key, T value) {
    }

    @Nullable
    @Override
    public ASTNode findChildByType(IElementType type) {
        return null;
    }

    @Nullable
    @Override
    public ASTNode findChildByType(IElementType type, @Nullable ASTNode anchor) {
        return null;
    }

    @Nullable
    @Override
    public ASTNode findChildByType(@NotNull TokenSet typesSet) {
        return null;
    }

    @Nullable
    @Override
    public ASTNode findChildByType(@NotNull TokenSet typesSet, @Nullable ASTNode anchor) {
        return null;
    }

    @Override
    public PsiElement getPsi() {
        return null;
    }

    @Override
    public <T extends PsiElement> T getPsi(@NotNull Class<T> clazz) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    }
}
