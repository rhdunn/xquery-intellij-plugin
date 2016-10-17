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
package uk.co.reecedunn.intellij.plugin.xquery.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XQueryVariable {
    private PsiElement mVariable;
    private PsiElement mDeclaration;

    /**
     * Creates a variable object.
     *
     * @param variable The PSI node that specifies the variable name.
     * @param declaration The element on which this variable is declared.
     */
    public XQueryVariable(@Nullable PsiElement variable, @NotNull PsiElement declaration) {
        mVariable = variable;
        mDeclaration = declaration;
    }

    public PsiElement getVariable() {
        return mVariable;
    }

    public PsiElement getDeclaration() {
        return mDeclaration;
    }
}
