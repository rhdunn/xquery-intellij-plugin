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
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryValidateExprPsiImpl extends ASTWrapperPsiElement implements XQueryValidateExpr, XQueryConformanceCheck {
    private TokenSet VALIDATE_BY_TYPENAME = TokenSet.create(XQueryTokenType.K_AS, XQueryTokenType.K_TYPE);

    public XQueryValidateExprPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        // TODO: schema-validation feature check
        PsiElement element = getConformanceElement();
        if (element != getFirstChild()) {
            final XQueryVersion minimalConformance = implementation.getVersion(XQuery.INSTANCE);
            final XQueryVersion marklogic = implementation.getVersion(MarkLogic.INSTANCE);
            if (element.getNode().getElementType() == XQueryTokenType.K_TYPE) {
                return (minimalConformance != null && minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0))
                    || (marklogic != null && marklogic.supportsVersion(XQueryVersion.VERSION_6_0));
            }
            return marklogic != null && marklogic.supportsVersion(XQueryVersion.VERSION_6_0);
        }
        return true;
    }

    @Override
    public PsiElement getConformanceElement() {
        PsiElement validateByTypeName = findChildByType(VALIDATE_BY_TYPENAME);
        return validateByTypeName == null ? getFirstChild() : validateByTypeName;
    }

    @Override
    public String getConformanceErrorMessage() {
        // TODO: schema-validation feature check
        if (getNode().findChildByType(XQueryTokenType.K_AS) != null) {
            return XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_6_0);
        } else if (getNode().findChildByType(XQueryTokenType.K_TYPE) != null) {
            return XQueryBundle.message("requires.feature.marklogic-xquery.version");
        }
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_1_0);
    }
}
