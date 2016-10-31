package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;

public class XQueryEQNamePrefixReference extends PsiReferenceBase<XQueryEQName> {
    public XQueryEQNamePrefixReference(XQueryEQName element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return getElement().resolvePrefixNamespace().map(XQueryNamespace::getPrefix).get();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
