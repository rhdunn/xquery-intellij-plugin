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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.update.facility;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityTransformWithExpr;
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class UpdateFacilityTransformWithExprPsiImpl extends ASTWrapperPsiElement implements UpdateFacilityTransformWithExpr, XQueryConformanceCheck {
    public UpdateFacilityTransformWithExprPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(@NotNull ImplementationItem implementation) {
        final XQueryVersion update = implementation.getVersion(UpdateFacility.INSTANCE);
        final XQueryVersion basex = implementation.getVersion(BaseX.INSTANCE);
        return (update != null && update.supportsVersion(XQueryVersion.VERSION_3_0))
            || (basex  != null && basex.supportsVersion(XQueryVersion.VERSION_8_5));
    }

    @NotNull
    @Override
    public PsiElement getConformanceElement() {
        return findChildByType(XQueryTokenType.K_TRANSFORM);
    }

    @NotNull
    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.feature.update-facility.version", XQueryVersion.VERSION_3_0);
    }
}
