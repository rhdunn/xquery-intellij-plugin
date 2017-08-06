package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryFunctionNameReference extends PsiReferenceBase<XQueryEQName> {
    public XQueryFunctionNameReference(XQueryEQName element, TextRange range) {
        super(element, range);
    }

    @Override
    public PsiElement resolve() {
        return getElement().resolvePrefixNamespace().map((ns) -> {
            if (ns.getDeclaration() instanceof XQueryPrologResolver) {
                XQueryPrologResolver provider = (XQueryPrologResolver) ns.getDeclaration();
                XQueryProlog prolog = provider.resolveProlog();
                if (prolog == null) {
                    return null;
                }

                return Option.of(getElement().getLocalName()).map(PsiElement::getText).map((localName) -> {
                    PsiElement parent = getElement().getParent();
                    int arity = 0;
                    if (parent instanceof XQueryFunctionCall) {
                        arity = ((XQueryFunctionCall)parent).getArity();
                    } else if (parent instanceof XQueryNamedFunctionRef) {
                        arity = ((XQueryNamedFunctionRef)parent).getArity();
                    } else if (parent instanceof XQueryArrowFunctionSpecifier) {
                        arity = ((XQueryArrowFunctionSpecifier)parent).getArity();
                    }

                    PsiElement annotation = prolog.getFirstChild();
                    while (annotation != null) {
                        if (annotation instanceof XQueryAnnotatedDecl) {
                            XQueryFunctionDecl functionDecl = children(annotation).findFirst(XQueryFunctionDecl.class).getOrElse(null);
                            if (functionDecl != null) {
                                XQueryEQName functionName = children(functionDecl).findFirst(XQueryEQName.class).getOrElse(null);
                                Option<PsiElement> functionLocalName = functionName == null ? Option.none() : Option.of(functionName.getLocalName());
                                if (functionLocalName.map(PsiElement::getText).map((name) -> name.equals(localName)).getOrElse(false)) {
                                    if (functionDecl.getArity() == arity) {
                                        return functionName;
                                    }
                                }
                            }
                        }

                        annotation = annotation.getNextSibling();
                    }
                    return null;
                }).getOrElse(null);
            }
            return null;
        }).getOrElse(null);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
