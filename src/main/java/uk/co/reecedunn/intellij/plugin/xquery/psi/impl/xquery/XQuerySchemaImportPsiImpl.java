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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaImport;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaPrefix;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceProvider;

public class XQuerySchemaImportPsiImpl extends ASTWrapperPsiElement implements XQuerySchemaImport, XQueryNamespaceProvider {
    public XQuerySchemaImportPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public XQueryNamespace resolveNamespace(CharSequence prefix) {
        XQuerySchemaPrefix schema = findChildByType(XQueryElementType.SCHEMA_PREFIX);
        if (schema == null) {
            return null;
        }

        PsiElement name = schema.getFirstChild();
        while (name != null) {
            if (name instanceof XQueryNCName && ((XQueryNCName)name).getLocalName().getText().equals(prefix)) {
                PsiElement element = findChildByType(XQueryElementType.URI_LITERAL);
                return new XQueryNamespace(((XQueryNCName)name).getLocalName(), element);
            }
            name = name.getNextSibling();
        }
        return null;
    }
}
