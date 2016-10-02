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
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNamedFunctionRef;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.IXQueryKeywordOrNCNameType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public class XQueryNamedFunctionRefPsiImpl extends ASTWrapperPsiElement implements XQueryNamedFunctionRef, XQueryConformanceCheck {
    public XQueryNamedFunctionRefPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean conformsTo(ImplementationItem implementation) {
        IElementType type = getConformanceElement().getNode().getElementType();
        if (type instanceof IXQueryKeywordOrNCNameType) {
            switch (((IXQueryKeywordOrNCNameType)type).getKeywordType()) {
                case KEYWORD:
                    break;
                case RESERVED_FUNCTION_NAME:
                    return false;
                case MARKLOGIC_RESERVED_FUNCTION_NAME:
                    final XQueryVersion marklogicVersion = implementation.getVersion(XQueryConformance.MARKLOGIC);
                    return marklogicVersion == null || !marklogicVersion.supportsVersion(XQueryVersion.VERSION_8_0);
                case XQUERY30_RESERVED_FUNCTION_NAME:
                    final XQueryVersion marklogic = implementation.getVersion(XQueryConformance.MARKLOGIC);
                    final XQueryVersion xquery = implementation.getVersion(XQueryConformance.MINIMAL_CONFORMANCE);
                    return xquery == null ||
                           (!xquery.supportsVersion(XQueryVersion.VERSION_3_0) &&
                            (marklogic == null || !marklogic.supportsVersion(XQueryVersion.VERSION_6_0)));
            }
        }

        final XQueryVersion minimalConformance = implementation.getVersion(XQueryConformance.MINIMAL_CONFORMANCE);
        final XQueryVersion marklogic = implementation.getVersion(XQueryConformance.MARKLOGIC);
        return (minimalConformance != null && minimalConformance.supportsVersion(XQueryVersion.VERSION_3_0))
            || (marklogic != null && marklogic.supportsVersion(XQueryVersion.VERSION_6_0));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public PsiElement getConformanceElement() {
        PsiElement name = findChildByClass(XQueryEQName.class);
        if (name.getNode().getElementType() == XQueryElementType.NCNAME) {
            name = name.getFirstChild();
            IElementType type = name.getNode().getElementType();
            if (type instanceof IXQueryKeywordOrNCNameType) {
                switch (((IXQueryKeywordOrNCNameType)type).getKeywordType()) {
                    case KEYWORD:
                        break;
                    case RESERVED_FUNCTION_NAME:
                    case MARKLOGIC_RESERVED_FUNCTION_NAME:
                    case XQUERY30_RESERVED_FUNCTION_NAME:
                        return name;
                }
            }
        }
        return findChildByType(XQueryTokenType.FUNCTION_REF_OPERATOR);
    }

    @Override
    public String getConformanceErrorMessage() {
        IElementType type = getConformanceElement().getNode().getElementType();
        if (type instanceof IXQueryKeywordOrNCNameType) {
            switch (((IXQueryKeywordOrNCNameType)type).getKeywordType()) {
                case KEYWORD:
                    break;
                case RESERVED_FUNCTION_NAME:
                case MARKLOGIC_RESERVED_FUNCTION_NAME:
                case XQUERY30_RESERVED_FUNCTION_NAME:
                    return XQueryBundle.message("requires.error.reserved-keyword-as-function-name");
            }
        }
        return XQueryBundle.message("requires.feature.marklogic-xquery.version");
    }
}
