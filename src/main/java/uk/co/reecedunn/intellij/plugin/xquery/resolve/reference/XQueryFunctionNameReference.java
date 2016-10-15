package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;

import java.util.List;

public class XQueryFunctionNameReference extends PsiReferenceBase<XQueryEQName> {
    public XQueryFunctionNameReference(XQueryEQName element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XQueryNamespace ns = getElement().resolvePrefixNamespace();
        if (ns == null) {
            return null;
        }

        if (ns.getDeclaration() instanceof XQueryModuleDecl) {
            CharSequence localName = getElement().getLocalName().getText();

            PsiElement file = ns.getDeclaration();
            while (!(file instanceof XQueryFile)) {
                file = file.getParent();
            }

            PsiElement module = PsiNavigation.findChildrenByClass(file, XQueryModule.class).get(0);
            PsiElement prolog = PsiNavigation.findChildrenByClass(module, XQueryProlog.class).get(0);
            PsiElement annotation = prolog.getFirstChild();
            while (annotation != null) {
                if (annotation instanceof XQueryAnnotatedDecl) {
                    List<XQueryFunctionDecl> functionDecls = PsiNavigation.findChildrenByClass(annotation, XQueryFunctionDecl.class);
                    if (functionDecls.size() == 1) {
                        XQueryFunctionDecl functionDecl = functionDecls.get(0);
                        List<XQueryEQName> functionName = PsiNavigation.findChildrenByClass(functionDecl, XQueryEQName.class);
                        if (functionName.size() != 0 && functionName.get(0).getLocalName().getText().equals(localName)) {
                            // TODO: Check function arity.
                            return functionName.get(0);
                        }
                    }
                }

                annotation = annotation.getNextSibling();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
