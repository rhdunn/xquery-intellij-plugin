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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver;

public class XQueryPrologPsiImpl extends ASTWrapperPsiElement implements XQueryProlog, XQueryNamespaceResolver, XQueryVariableResolver {
    public XQueryPrologPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public XQueryNamespace resolveNamespace(CharSequence prefix) {
        PsiElement element = getLastChild();
        while (element != null) {
            if (element instanceof XQueryNamespaceResolver) {
                XQueryNamespace resolved = ((XQueryNamespaceResolver)element).resolveNamespace(prefix);
                if (resolved != null) {
                    return resolved;
                }
            }
            element = element.getPrevSibling();
        }
        return null;
    }

    @Nullable
    @Override
    public XQueryVariable resolveVariable(XQueryEQName name) {
        PsiElement element = getLastChild();
        while (element != null) {
            if (element instanceof XQueryVariableResolver) {
                XQueryVariable resolved = ((XQueryVariableResolver)element).resolveVariable(name);
                if (resolved != null) {
                    return resolved;
                }
            }
            element = element.getPrevSibling();
        }
        return null;
    }
}
