package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.testFramework.LightVirtualFileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryUriLiteralPsiImpl;

public class XQueryUriLiteralReference extends PsiReferenceBase<XQueryUriLiteralPsiImpl> {
    public XQueryUriLiteralReference(XQueryUriLiteralPsiImpl element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XQueryStringLiteral element = getElement();
        final CharSequence value = element.getAtomicValue();
        if (value == null) {
            return null;
        }

        final String path = value.toString();
        if (path.contains("://")) {
            return ResourceVirtualFile.resolve(path, element.getProject());
        }

        VirtualFile file = element.getContainingFile().getVirtualFile();
        if (file instanceof LightVirtualFileBase) {
            file = ((LightVirtualFileBase)file).getOriginalFile();
        }

        return resolveFileByPath(file, element.getProject(), path);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiFile resolveFileByPath(VirtualFile parent, Project project, String path) {
        if (parent == null) {
            return null;
        }

        VirtualFile file = parent.findFileByRelativePath(path);
        if (file != null) {
            return PsiManager.getInstance(project).findFile(file);
        }

        return resolveFileByPath(parent.getParent(), project, path);
    }
}
