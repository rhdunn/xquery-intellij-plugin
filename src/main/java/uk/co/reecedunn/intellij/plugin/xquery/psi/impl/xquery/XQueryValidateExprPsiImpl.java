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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryValidateExprPsiImpl extends ASTWrapperPsiElement implements XQueryValidateExpr, XQueryVersionedConstruct {
    public XQueryValidateExprPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        // TODO: schema-validation feature check
        final ASTNode node = getNode().findChildByType(XQueryTokenType.K_AS);
        if (node != null) {
            final XQueryVersion version = implementation.getVersion(XQueryConformance.MARKLOGIC);
            return version != null && version.supportsVersion(XQueryVersion.VERSION_6_0);
        }
        return true;
    }

    @Override
    public PsiElement getConformanceElement() {
        PsiElement as = findChildByType(XQueryTokenType.K_AS);
        return as == null ? getFirstChild() : as;
    }

    @Override
    public String getConformanceErrorMessage() {
        // TODO: schema-validation feature check
        final ASTNode as = getNode().findChildByType(XQueryTokenType.K_AS);
        if (as != null) {
            return XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_6_0);
        }
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_1_0);
    }
}
