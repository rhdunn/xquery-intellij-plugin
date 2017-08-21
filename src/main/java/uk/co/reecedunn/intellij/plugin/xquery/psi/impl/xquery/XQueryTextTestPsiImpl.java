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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryTextTest;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryTextTestPsiImpl extends ASTWrapperPsiElement implements XQueryTextTest, XQueryConformanceCheck {
    public XQueryTextTestPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(@NotNull ImplementationItem implementation) {
        if (getConformanceElement() == getFirstChild()) {
            return true;
        }

        final XQueryVersion marklogic = implementation.getVersion(MarkLogic.INSTANCE);
        return marklogic != null && marklogic.supportsVersion(XQueryVersion.VERSION_8_0);
    }

    @NotNull
    @Override
    public PsiElement getConformanceElement() {
        PsiElement marklogic = findChildByType(XQueryElementType.STRING_LITERAL);
        return marklogic == null ? getFirstChild() : marklogic;
    }

    @NotNull
    @Override
    public String getConformanceErrorMessage() {
        return XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_8_0);
    }
}
