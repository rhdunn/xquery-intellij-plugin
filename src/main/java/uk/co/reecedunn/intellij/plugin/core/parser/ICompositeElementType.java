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
package uk.co.reecedunn.intellij.plugin.core.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ICompositeElementType extends IElementType {
    private Constructor<?> mPsiConstructor;

    public ICompositeElementType(@NotNull @NonNls String debugName, Class<?> psiClass, Language language) {
        super(debugName, language);
        try {
            mPsiConstructor = psiClass.getConstructor(ASTNode.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Cannot find the appropriate constructor for " + psiClass.getName());
        }
    }

    public PsiElement createPsiElement(ASTNode node) {
        try {
            final Object[] arguments = new Object[]{ node };
            return (PsiElement)mPsiConstructor.newInstance(arguments);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ProcessCanceledException) {
                throw (ProcessCanceledException)cause;
            }
            throw new AssertionError("Cannot create XQuery PsiElement for " + this, cause);
        } catch (Exception e) {
            throw new AssertionError("Cannot create XQuery PsiElement for " + this, e);
        }
    }
}