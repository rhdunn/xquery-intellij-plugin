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
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.TextPsiElementImpl;

import java.util.HashMap;
import java.util.Map;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryModulePsiImpl extends ASTWrapperPsiElement implements XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    private Map<String, TextPsiElementImpl> strings = new HashMap<>();

    public XQueryModulePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        if (prefix != null && prefix.equals("local")) {
            PsiElement prefixElement = getStringElement("local");
            PsiElement uriElement = getStringElement("http://www.w3.org/2005/xquery-local-functions");
            return Option.some(new XQueryNamespace(prefixElement, uriElement, this));
        }
        return Option.none();
    }

    @Override
    public Option<XQueryProlog> resolveProlog() {
        return children(this).findFirst(XQueryProlog.class);
    }

    private PsiElement getStringElement(String string) {
        TextPsiElementImpl element = strings.getOrDefault(string, null);
        if (element == null) {
            element = new TextPsiElementImpl(getProject(), string);
            strings.put(string, element);
        }
        return element;
    }
}
