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
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryArgumentList;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionCall;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryFunctionCallPsiImpl extends ASTWrapperPsiElement implements XQueryFunctionCall, XQueryConformanceCheck {
    public XQueryFunctionCallPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    private Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType> getLocalName() {
        final PsiElement name = getFirstChild();
        if (name.getNode().getElementType() == XQueryElementType.NCNAME) {
            final PsiElement localname = name.getFirstChild();
            final IElementType type = localname.getNode().getElementType();
            if (type instanceof IXQueryKeywordOrNCNameType) {
                return new Pair<>(localname, ((IXQueryKeywordOrNCNameType)type).getKeywordType());
            }
        }
        return null;
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        final Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType> localname = getLocalName();
        if (localname != null) switch (localname.getSecond()) {
            case MARKLOGIC_RESERVED_FUNCTION_NAME:
                final XQueryVersion marklogic = implementation.getVersion(XQueryConformance.MARKLOGIC);
                return marklogic == null || !marklogic.supportsVersion(XQueryVersion.VERSION_8_0);
            case SCRIPTING10_RESERVED_FUNCTION_NAME:
                final XQueryVersion scripting = implementation.getVersion(XQueryConformance.SCRIPTING);
                return scripting == null || !scripting.supportsVersion(XQueryVersion.VERSION_1_0);
            default:
                break;
        }
        return true;
    }

    @Override
    public PsiElement getConformanceElement() {
        final Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType> localname = getLocalName();
        if (localname != null) switch (localname.getSecond()) {
            case MARKLOGIC_RESERVED_FUNCTION_NAME:
            case SCRIPTING10_RESERVED_FUNCTION_NAME:
                return localname.getFirst();
            default:
                break;
        }
        return getFirstChild();
    }

    @Override
    public String getConformanceErrorMessage() {
        final Pair<PsiElement, IXQueryKeywordOrNCNameType.KeywordType> localname = getLocalName();
        if (localname != null) switch (localname.getSecond()) {
            case SCRIPTING10_RESERVED_FUNCTION_NAME:
                return XQueryBundle.message("requires.error.scripting-keyword-as-function-name", XQueryVersion.VERSION_1_0);
            case MARKLOGIC_RESERVED_FUNCTION_NAME:
            default:
                break;
        }
        return XQueryBundle.message("requires.error.marklogic-json-keyword-as-function-name", XQueryVersion.VERSION_8_0);
    }

    @Override
    public int getArity() {
        XQueryArgumentList arguments = children(this).findFirst(XQueryArgumentList.class).get();
        return arguments.getArity();
    }
}
