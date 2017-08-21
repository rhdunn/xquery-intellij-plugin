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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryInitialClause;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryIntermediateClause;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryIntermediateClausePsiImpl extends ASTWrapperPsiElement implements XQueryIntermediateClause, XQueryConformanceCheck, XQueryVariableResolver {
    public XQueryIntermediateClausePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    private XQueryVersion getRequiredXQueryVersion() {
        IElementType current = getFirstChild().getNode().getElementType();
        if (current == XQueryElementType.COUNT_CLAUSE || current == XQueryElementType.GROUP_BY_CLAUSE) {
            return XQueryVersion.VERSION_3_0;
        }

        PsiElement prevElement = getPrevSibling();
        IElementType prev = (prevElement instanceof XQueryInitialClause) ? null : prevElement.getFirstChild().getNode().getElementType();
        if (prev == XQueryElementType.WHERE_CLAUSE) {
            return (current == XQueryElementType.ORDER_BY_CLAUSE) ? XQueryVersion.VERSION_1_0 : XQueryVersion.VERSION_3_0;
        } else if (prev == XQueryElementType.ORDER_BY_CLAUSE) {
            return XQueryVersion.VERSION_3_0;
        }
        return XQueryVersion.VERSION_1_0;
    }

    @Override
    public boolean conformsTo(@NotNull ImplementationItem implementation) {
        final XQueryVersion minimalConformance = implementation.getVersion(XQuery.INSTANCE);
        return minimalConformance != null && minimalConformance.supportsVersion(getRequiredXQueryVersion());
    }

    @NotNull
    @Override
    public PsiElement getConformanceElement() {
        return getFirstChild().getFirstChild();
    }

    @NotNull
    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_3_0);
    }

    @Nullable
    @Override
    public XQueryVariable resolveVariable(XQueryEQName name) {
        PsiElement element = getFirstChild();
        if (element instanceof XQueryVariableResolver) {
            return ((XQueryVariableResolver)element).resolveVariable(name);
        }
        return null;
    }
}
