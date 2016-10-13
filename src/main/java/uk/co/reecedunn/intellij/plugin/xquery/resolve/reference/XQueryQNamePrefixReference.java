package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceProvider;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryQNamePsiImpl;

public class XQueryQNamePrefixReference extends PsiReferenceBase<XQueryQNamePsiImpl> {
    private CharSequence mPrefix;

    public XQueryQNamePrefixReference(XQueryQNamePsiImpl element, TextRange range, CharSequence prefix) {
        super(element, range);
        mPrefix = prefix;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement element = getElement();
        return resolvePrefix(element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiElement resolvePrefix(PsiElement element) {
        while (element != null) {
            if (element instanceof XQueryNamespaceProvider) {
                XQueryNamespace resolved = ((XQueryNamespaceProvider) element).resolveNamespace(mPrefix);
                if (resolved != null) {
                    return resolved.getPrefix();
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
