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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySlidingWindowClause;
import uk.co.reecedunn.intellij.plugin.xquery.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQuerySlidingWindowClausePsiImpl extends ASTWrapperPsiElement implements XQuerySlidingWindowClause, XQueryConformanceCheck, XQueryVariableResolver {
    public XQuerySlidingWindowClausePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        final XQueryVersion minimalConformance = implementation.getVersion(XQueryConformance.MINIMAL_CONFORMANCE);
        return minimalConformance != null && minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0);
    }

    @Override
    public PsiElement getConformanceElement() {
        return getFirstChild();
    }

    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_3_0);
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
            } else if (element instanceof XQueryEQName) {
                if (element.equals(name)) {
                    return Option.some(new XQueryVariable(element, this));
                }
            }
            element = element.getNextSibling();
        }
        return Option.none();
    }
}
