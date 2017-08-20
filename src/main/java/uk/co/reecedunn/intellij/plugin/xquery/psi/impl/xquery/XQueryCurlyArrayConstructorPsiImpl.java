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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCurlyArrayConstructor;
import uk.co.reecedunn.intellij.plugin.xquery.lang.*;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryCurlyArrayConstructorPsiImpl extends ASTWrapperPsiElement implements XQueryCurlyArrayConstructor, XQueryConformanceCheck {
    public XQueryCurlyArrayConstructorPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        if (getConformanceElement().getNode().getElementType() == XQueryTokenType.K_ARRAY_NODE) {
            final XQueryVersion version = implementation.getVersion(MarkLogic.INSTANCE);
            return version != null && version.supportsVersion(XQueryVersion.VERSION_8_0);
        }
        final XQueryVersion version = implementation.getVersion(XQuery.INSTANCE);
        return version != null && version.supportsVersion(XQueryVersion.VERSION_3_1);
    }

    @Override
    public PsiElement getConformanceElement() {
        return getFirstChild();
    }

    @Override
    public String getConformanceErrorMessage() {
        if (getConformanceElement().getNode().getElementType() == XQueryTokenType.K_ARRAY_NODE) {
            return XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_8_0);
        }
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_3_1);
    }
}
