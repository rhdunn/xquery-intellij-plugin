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

public class XQueryNamespace {
    private PsiElement mPrefix;
    private PsiElement mUri;
    private PsiElement mDeclaration;

    /**
     * Creates a namespace object.
     *
     * @param prefix The <code>NCName</code> or <code>QName</code> PSI node that specifies the namespace prefix.
     * @param uri The <code>URILiteral</code> or <code>URIExpr</code> PSI node that specifies the namespace URI.
     * @param declaration The element on which this namespace is declared.
     */
    public XQueryNamespace(@NotNull PsiElement prefix, @Nullable PsiElement uri, @NotNull PsiElement declaration) {
        mPrefix = prefix;
        mUri = uri;
        mDeclaration = declaration;
    }

    public PsiElement getPrefix() {
        return mPrefix;
    }

    public PsiElement getUri() {
        return mUri;
    }

    public PsiElement getDeclaration() {
        return mDeclaration;
    }
}
