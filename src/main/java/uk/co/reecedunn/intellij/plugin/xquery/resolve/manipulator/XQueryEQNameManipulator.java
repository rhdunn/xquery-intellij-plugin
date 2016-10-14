package uk.co.reecedunn.intellij.plugin.xquery.resolve.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryEQNamePsiImpl;

public class XQueryEQNameManipulator extends AbstractElementManipulator<XQueryEQNamePsiImpl> {
    @Override
    public XQueryEQNamePsiImpl handleContentChange(@NotNull XQueryEQNamePsiImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }
}
