package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryUriLiteralPsiImpl;

public class XQueryUriLiteralReference extends PsiReferenceBase<XQueryUriLiteralPsiImpl> {
    public XQueryUriLiteralReference(XQueryUriLiteralPsiImpl element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement element = getElement();
        final String path = ((XQueryStringLiteral)element).getStringValue().toString();
        if (path.contains("://")) {
            return null;
        }
        return resolveFileByPath(element.getContainingFile().getParent(), path);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiFile resolveFileByPath(PsiDirectory parent, String path) {
        if (parent == null) {
            return null;
        }

        VirtualFile file = parent.getVirtualFile().findFileByRelativePath(path);
        if (file != null) {
            return PsiManager.getInstance(parent.getProject()).findFile(file);
        }

        return resolveFileByPath(parent.getParentDirectory(), path);
    }
}
