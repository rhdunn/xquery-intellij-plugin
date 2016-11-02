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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;

public class XQueryModuleImportPsiImpl extends ASTWrapperPsiElement implements XQueryModuleImport, XQueryNamespaceResolver, XQueryPrologResolver {
    public XQueryModuleImportPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        XQueryNCName name = findChildByType(XQueryElementType.NCNAME);
        if (name == null) {
            return Option.none();
        }

        if (name.getLocalNameElement().getText().equals(prefix)) {
            PsiElement element = findChildByType(XQueryElementType.URI_LITERAL);
            return Option.some(new XQueryNamespace(name.getLocalNameElement(), element, this));
        }
        return Option.none();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Option<XQueryProlog> resolveProlog() {
        PsiElement uri = getFirstChild();
        while (uri != null) {
            if (uri instanceof XQueryUriLiteral) {
                PsiElement file = uri.getReference().resolve();
                if (file instanceof XQueryFile) {
                    PsiElement module = PsiNavigation.findChildByClass(file, XQueryModule.class);
                    return Option.of(PsiNavigation.findChildByClass(module, XQueryProlog.class));
                }
            }
            uri = uri.getNextSibling();
        }
        return Option.none();
    }
}
