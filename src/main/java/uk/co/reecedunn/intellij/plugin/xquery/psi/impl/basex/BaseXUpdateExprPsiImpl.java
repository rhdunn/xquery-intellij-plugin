/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.basex;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.basex.BaseXUpdateExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class BaseXUpdateExprPsiImpl extends ASTWrapperPsiElement implements BaseXUpdateExpr, XQueryConformanceCheck {
    public BaseXUpdateExprPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        final XQueryVersion basex = implementation.getVersion(XQueryConformance.BASEX);
        // NOTE: UpdateExpr was introduced in BaseX 7.8, but this plugin only supports >= 8.4.
        return basex  != null && basex.supportsVersion(XQueryVersion.VERSION_8_4);
    }

    @Override
    public PsiElement getConformanceElement() {
        return findChildByType(XQueryTokenType.K_UPDATE);
    }

    @Override
    public String getConformanceErrorMessage() {
        // NOTE: UpdateExpr was introduced in BaseX 7.8, but this plugin only supports >= 8.4.
        return XQueryBundle.message("requires.feature.basex.version", XQueryVersion.VERSION_8_4);
    }
}
