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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.LightVirtualFileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryUriLiteralReference;

public class XQueryUriLiteralPsiImpl extends XQueryStringLiteralPsiImpl implements XQueryUriLiteral {
    public XQueryUriLiteralPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        TextRange range = getTextRange();
        return new XQueryUriLiteralReference(this, new TextRange(1, range.getLength() - 1));
    }

    @Nullable
    public PsiFile resolveUri() {
        final CharSequence value = getAtomicValue();
        if (value == null) {
            return null;
        }

        final String path = value.toString();
        if (path.contains("://")) {
            return ResourceVirtualFile.resolve(path, getProject());
        }

        VirtualFile file = getContainingFile().getVirtualFile();
        if (file instanceof LightVirtualFileBase) {
            file = ((LightVirtualFileBase)file).getOriginalFile();
        }

        return resolveFileByPath(file, getProject(), path);
    }

    @Nullable
    private PsiFile resolveFileByPath(VirtualFile parent, Project project, String path) {
        if (parent == null) {
            return null;
        }

        VirtualFile file = parent.findFileByRelativePath(path);
        if (file != null) {
            return PsiManager.getInstance(project).findFile(file);
        }

        return resolveFileByPath(parent.getParent(), project, path);
    }
}
