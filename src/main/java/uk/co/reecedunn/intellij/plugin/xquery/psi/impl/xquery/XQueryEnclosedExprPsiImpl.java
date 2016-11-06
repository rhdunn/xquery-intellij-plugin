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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompPIConstructor;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEnclosedExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryEnclosedExprPsiImpl extends ASTWrapperPsiElement implements XQueryEnclosedExpr, XQueryConformanceCheck {
    public XQueryEnclosedExprPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    private boolean previousVersionSupportsOptionalExpr() {
        PsiElement parent = getParent();
        return parent instanceof XQueryCompPIConstructor;
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        if (previousVersionSupportsOptionalExpr() || getConformanceElement() != getFirstChild()) {
            return true;
        }

        final XQueryVersion minimalConformance = implementation.getVersion(XQueryConformance.MINIMAL_CONFORMANCE);
        return minimalConformance != null && minimalConformance.supportsVersion(XQueryVersion.VERSION_3_1);
    }

    @Override
    public PsiElement getConformanceElement() {
        PsiElement element = findChildByType(XQueryElementType.EXPR);
        return element == null ? getFirstChild() : element;
    }

    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.empty-expression.version", XQueryVersion.VERSION_3_1);
    }
}
