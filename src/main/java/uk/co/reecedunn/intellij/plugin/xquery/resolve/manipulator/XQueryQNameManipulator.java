package uk.co.reecedunn.intellij.plugin.xquery.resolve.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryQNamePsiImpl;

public class XQueryQNameManipulator extends AbstractElementManipulator<XQueryQNamePsiImpl> {
    @Override
    public XQueryQNamePsiImpl handleContentChange(@NotNull XQueryQNamePsiImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }
}
