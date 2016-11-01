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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.xquery.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;

public class XQueryModulePsiImpl extends ASTWrapperPsiElement implements XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    public XQueryModulePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        if (prefix != null && prefix.equals("local")) {
            return Option.some(new XQueryNamespace(null, null, this));
        }
        return Option.none();
    }

    @Override
    public Option<XQueryProlog> resolveProlog() {
        return Option.of(PsiNavigation.findChildByClass(this, XQueryProlog.class));
    }
}
