package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver;

public class XQueryVariableNameReference extends PsiReferenceBase<XQueryEQName> {
    public XQueryVariableNameReference(XQueryEQName element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XQueryEQName name = getElement();

        PsiElement element = name;
        while (element != null) {
            if (element instanceof XQueryVariableResolver) {
                Option<XQueryVariable> resolved = ((XQueryVariableResolver)element).resolveVariable(name);
                if (resolved.isDefined()) {
                    return resolved.get().getVariable();
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

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
