package uk.co.reecedunn.intellij.plugin.xquery.resolve.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathEQNamePsiImpl;

public class XQueryEQNameManipulator extends AbstractElementManipulator<XPathEQNamePsiImpl> {
    @Override
    public XPathEQNamePsiImpl handleContentChange(@NotNull XPathEQNamePsiImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }
}
