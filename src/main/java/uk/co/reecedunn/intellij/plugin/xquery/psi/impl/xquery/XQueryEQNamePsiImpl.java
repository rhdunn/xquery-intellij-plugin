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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryBracedURILiteral;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.INCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceProvider;
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryEQNamePrefixReference;
import uk.co.reecedunn.intellij.plugin.xquery.resolve.reference.XQueryFunctionNameReference;

public class XQueryEQNamePsiImpl extends ASTWrapperPsiElement implements XQueryEQName {
    private static TokenSet QNAME_SEPARATORS = TokenSet.create(
        XQueryTokenType.QNAME_SEPARATOR,
        XQueryTokenType.XML_TAG_QNAME_SEPARATOR,
        XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR);

    public XQueryEQNamePsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof XQueryEQName)) {
            return false;
        }

        XQueryEQName rhs = (XQueryEQName)other;
        if (!getLocalName().getText().equals(rhs.getLocalName().getText())) {
            return false;
        }

        PsiElement lhsPrefix = getPrefix();
        PsiElement rhsPrefix = rhs.getPrefix();
        return (lhsPrefix == null && rhsPrefix == null) ||
               (lhsPrefix != null && lhsPrefix.getText().equals(rhsPrefix.getText()));
    }

    @Override
    public PsiReference getReference() {
        PsiReference[] references = getReferences();
        return references.length == 0 ? null : references[0];
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public PsiReference[] getReferences() {
        PsiElement prefix = getPrefix();
        if (prefix == null || prefix instanceof XQueryBracedURILiteral) {
            return PsiReference.EMPTY_ARRAY;
        }

        IElementType parent = getParent().getNode().getElementType();
        if (parent == XQueryElementType.FUNCTION_CALL ||
            parent == XQueryElementType.NAMED_FUNCTION_REF) {
            int eqnameStart = getTextOffset();
            return new PsiReference[] {
                new XQueryEQNamePrefixReference(this, prefix.getTextRange().shiftRight(-eqnameStart)),
                new XQueryFunctionNameReference(this, getLocalName().getTextRange().shiftRight(-eqnameStart))
            };
        }

        TextRange range = prefix.getTextRange();
        return new PsiReference[] {
            new XQueryEQNamePrefixReference(this, new TextRange(0, range.getLength()))
        };
    }

    @Override
    public PsiElement getPrefix() {
        PsiElement element = getFirstChild();
        if (element.getNode().getElementType() == XQueryElementType.URI_QUALIFIED_NAME) {
            return ((XQueryEQName)element).getPrefix();
        }

        PsiElement match = null;
        while (element != null) {
            if (element.getNode().getElementType() instanceof INCNameType) {
                match = element;
            } else if (QNAME_SEPARATORS.contains(element.getNode().getElementType())) {
                return match;
            }
            element = element.getNextSibling();
        }
        return null;
    }

    @Override
    public PsiElement getLocalName() {
        PsiElement element = findChildByType(QNAME_SEPARATORS);
        if (element == null) { // NCName | URIQualifiedName
            element = getFirstChild();
            if (element.getNode().getElementType() == XQueryElementType.URI_QUALIFIED_NAME) {
                return ((XQueryEQName)element).getLocalName();
            }

            while (element != null) {
                if (element.getNode().getElementType() instanceof INCNameType) {
                    return element;
                }
                element = element.getNextSibling();
            }
        } else { // QName
            while (element != null) {
                if (element.getNode().getElementType() instanceof INCNameType) {
                    return element;
                }
                element = element.getNextSibling();
            }
        }
        return null;
    }

    @Override
    public XQueryNamespace resolvePrefixNamespace() {
        PsiElement element = getPrefix();
        if (element instanceof XQueryBracedURILiteral) {
            return null;
        }

        CharSequence prefix = element.getText();
        while (element != null) {
            if (element instanceof XQueryNamespaceProvider) {
                XQueryNamespace resolved = ((XQueryNamespaceProvider) element).resolveNamespace(prefix);
                if (resolved != null) {
                    return resolved;
                }
            }

            PsiElement next = element.getPrevSibling();
            if (next == null) {
                next = element.getParent();
            }
            element = next;
        }
        return null;
    }
}
