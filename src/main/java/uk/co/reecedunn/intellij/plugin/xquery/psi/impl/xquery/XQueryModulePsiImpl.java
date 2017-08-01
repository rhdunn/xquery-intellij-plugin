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
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.TextPsiElementImpl;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.util.HashMap;
import java.util.Map;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryModulePsiImpl extends ASTWrapperPsiElement implements XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    private Map<String, XQueryNamespace> predefinedNamespaces = new HashMap<>();
    private XQueryProjectSettings settings;
    private String dialectId = "";

    public XQueryModulePsiImpl(@NotNull ASTNode node) {
        super(node);
        settings = XQueryProjectSettings.getInstance(getProject());
    }

    @Nullable
    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        XQueryNamespace ns = getPredefinedNamespaces().getOrDefault(prefix != null ? prefix.toString() : null, null);
        return Option.of(ns);
    }

    @Override
    public Option<XQueryProlog> resolveProlog() {
        return children(this).findFirst(XQueryProlog.class);
    }

    private Map<String, XQueryNamespace> getPredefinedNamespaces() {
        XQueryVersion version = ((XQueryFile)getContainingFile()).getXQueryVersion();
        String id = settings.getDialectForXQueryVersion(version).getID();
        if (!id.equals(dialectId)) {
            dialectId = id;
            predefinedNamespaces.clear();

            Project project = getProject();

            // XQuery Predefined Namespaces [https://www.w3.org/TR/xquery/#id-basics]
            createPredefinedNamespace(project, "xml", "http://www.w3.org/XML/1998/namespace");
            createPredefinedNamespace(project, "xs", "http://www.w3.org/2001/XMLSchema");
            createPredefinedNamespace(project, "xsi", "http://www.w3.org/2001/XMLSchema-instance");
            createPredefinedNamespace(project, "fn", "http://www.w3.org/2005/xpath-functions");
            createPredefinedNamespace(project, "local", "http://www.w3.org/2005/xquery-local-functions");

            if (version.supportsVersion(XQueryVersion.VERSION_3_1)) {
                // XQuery 3.1 Predefined Namespaces [https://www.w3.org/TR/xquery-31/#id-basics]
                // NOTE: The math namespace is predefined in XQuery 3.1, not XQuery 3.0!
                createPredefinedNamespace(project, "map", "http://www.w3.org/2005/xpath-functions/map");
                createPredefinedNamespace(project, "array", "http://www.w3.org/2005/xpath-functions/array");
                createPredefinedNamespace(project, "math", "http://www.w3.org/2005/xpath-functions/math");
            }
        }
        return predefinedNamespaces;
    }

    private void createPredefinedNamespace(Project project, String prefix, String uri) {
        PsiElement prefixElement = new TextPsiElementImpl(project, prefix);
        PsiElement uriElement = new TextPsiElementImpl(project, uri);
        predefinedNamespaces.put(prefix, new XQueryNamespace(prefixElement, uriElement, this));
    }
}
