package uk.co.reecedunn.intellij.plugin.xquery.resolve.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryUriLiteralPsiImpl;

public class XQueryUriLiteralManipulator extends AbstractElementManipulator<XQueryUriLiteralPsiImpl> {
    @Override
    public XQueryUriLiteralPsiImpl handleContentChange(@NotNull XQueryUriLiteralPsiImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }
}
