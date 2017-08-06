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
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLetClause;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver;

public class XQueryLetClausePsiImpl extends ASTWrapperPsiElement implements XQueryLetClause, XQueryVariableResolver {
    public XQueryLetClausePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Option<XQueryVariable> resolveVariable(XQueryEQName name) {
        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof XQueryVariableResolver) {
                Option<XQueryVariable> resolved = ((XQueryVariableResolver)element).resolveVariable(name);
                if (resolved.isDefined()) {
                    return resolved;
                }
            }
            element = element.getNextSibling();
        }
        return Option.none();
    }
}
