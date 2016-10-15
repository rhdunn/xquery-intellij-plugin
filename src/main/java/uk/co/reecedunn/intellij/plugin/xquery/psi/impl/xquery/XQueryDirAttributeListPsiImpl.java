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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeList;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQName;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceProvider;

public class XQueryDirAttributeListPsiImpl extends ASTWrapperPsiElement implements XQueryDirAttributeList, XQueryNamespaceProvider {
    public XQueryDirAttributeListPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public XQueryNamespace resolveNamespace(CharSequence prefix) {
        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof XQueryQName) {
                XQueryQName name = (XQueryQName) element;
                if (name.getLocalName().getText().equals(prefix)) {
                    PsiElement uri = element.getNextSibling();
                    while (uri != null) {
                        if (uri.getNode().getElementType() == XQueryElementType.QNAME) {
                            break;
                        } else if (uri.getNode().getElementType() == XQueryElementType.DIR_ATTRIBUTE_VALUE) {
                            return new XQueryNamespace(name.getLocalName(), uri, this);
                        }
                        uri = uri.getNextSibling();
                    }
                    return new XQueryNamespace(name.getLocalName(), null, this);
                }
            }
            element = element.getNextSibling();
        }
        return null;
    }
}
