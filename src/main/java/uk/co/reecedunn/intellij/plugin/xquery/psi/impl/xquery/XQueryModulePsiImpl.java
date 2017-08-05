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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryUriLiteralReference;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryModulePsiImpl extends ASTWrapperPsiElement implements XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    private XQueryProjectSettings settings;
    private XQueryNamespaceResolver staticContext;
    private String dialectId = "";

    public XQueryModulePsiImpl(@NotNull ASTNode node) {
        super(node);
        settings = XQueryProjectSettings.getInstance(getProject());
    }

    @Nullable
    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        XQueryVersion version = ((XQueryFile)getContainingFile()).getXQueryVersion().getVersionOrDefault(getProject());
        ImplementationItem dialect = settings.getDialectForXQueryVersion(version);
        if (!dialect.getID().equals(dialectId)) {
            dialectId = dialect.getID();
            staticContext = null;

            Project project = getProject();
            XQueryFile file = (XQueryFile)XQueryUriLiteralReference.resolveResource(dialect.getStaticContext(), project);
            if (file != null) {
                Option<XQueryModule> module = children(file).findFirst(XQueryModule.class);
                if (module.isDefined()) {
                    staticContext = (XQueryNamespaceResolver)children(module.get()).findFirst(XQueryProlog.class).getOrElse(null);
                }
            }
        }

        if (staticContext != null) {
            return staticContext.resolveNamespace(prefix);
        }
        return Option.none();
    }

    @Override
    public Option<XQueryProlog> resolveProlog() {
        return children(this).findFirst(XQueryProlog.class);
    }
}
