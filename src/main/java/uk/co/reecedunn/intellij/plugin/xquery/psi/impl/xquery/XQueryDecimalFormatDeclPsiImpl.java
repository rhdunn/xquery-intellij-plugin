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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDFPropertyName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDecimalFormatDecl;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import static uk.co.reecedunn.intellij.plugin.xquery.functional.PsiTreeWalker.children;

public class XQueryDecimalFormatDeclPsiImpl extends ASTWrapperPsiElement implements XQueryDecimalFormatDecl, XQueryConformanceCheck {
    public XQueryDecimalFormatDeclPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    private XQueryVersion getRequiredVersion() {
        return getConformanceElement() instanceof XQueryDFPropertyName ? XQueryVersion.VERSION_3_1 : XQueryVersion.VERSION_3_0;
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        final XQueryVersion minimalConformance = implementation.getVersion(XQueryConformance.MINIMAL_CONFORMANCE);
        return minimalConformance != null && minimalConformance.supportsVersion(getRequiredVersion());
    }

    @Override
    public PsiElement getConformanceElement() {
        return children(this)
              .findFirst((e) -> e instanceof XQueryDFPropertyName && e.getFirstChild().getNode().getElementType() == XQueryTokenType.K_EXPONENT_SEPARATOR )
              .orElse(() -> findChildByType(XQueryTokenType.K_DECIMAL_FORMAT))
              .get();
    }

    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.feature.minimal-conformance.version", getRequiredVersion());
    }
}
