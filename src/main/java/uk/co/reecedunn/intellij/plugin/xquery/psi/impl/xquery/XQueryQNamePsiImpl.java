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
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQName;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.INCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

public class XQueryQNamePsiImpl extends ASTWrapperPsiElement implements XQueryQName {
    private static TokenSet QNAME_SEPARATORS = TokenSet.create(
        XQueryTokenType.QNAME_SEPARATOR,
        XQueryTokenType.XML_TAG_QNAME_SEPARATOR,
        XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR);

    public XQueryQNamePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getPrefix() {
        PsiElement element = getFirstChild();
        while (element != null) {
            if (element.getNode().getElementType() instanceof INCNameType) {
                return element;
            } else if (QNAME_SEPARATORS.contains(element.getNode().getElementType())) {
                return null;
            }
            element = element.getNextSibling();
        }
        return null;
    }

    @Override
    public PsiElement getLocalName() {
        PsiElement element = findChildByType(QNAME_SEPARATORS);
        while (element != null) {
            if (element.getNode().getElementType() instanceof INCNameType) {
                return element;
            }
            element = element.getNextSibling();
        }
        return null;
    }
}
