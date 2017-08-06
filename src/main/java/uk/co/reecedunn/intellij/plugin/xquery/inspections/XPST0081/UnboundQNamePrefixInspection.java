/*
 * Copyright (C) 2016 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0081;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQName;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.util.List;

/** XPST0081 error condition
 *
 * It is a <em>static error</em> if a QName used in a query contains a
 * namespace prefix that is not in the <em>statically known namespaces</em>.
 */
public class UnboundQNamePrefixInspection extends LocalInspectionTool {
    @NotNull
    public String getDisplayName() {
        return XQueryBundle.message("inspection.XPST0081.unbound-qname-prefix.display-name");
    }

    @Override
    public String getDescriptionFileName() {
        return getID() + ".html";
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof XQueryFile)) return null;

        final List<ProblemDescriptor> descriptors = new SmartList<>();
        checkElement(file, manager, descriptors, isOnTheFly);
        return descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }

    private void checkElement(@NotNull PsiElement element,
                              @NotNull final InspectionManager manager,
                              @NotNull final List<ProblemDescriptor> descriptors,
                              boolean isOnTheFly) {
        if (element instanceof XQueryEQName) {
            XQueryEQName qname = (XQueryEQName)element;
            Option<PsiElement> context = Option.of(qname.getPrefix()).filter(XQueryNCName.class);
            if (context.isDefined() && context.get().getText().equals("xmlns")) {
                return;
            }
            if (context.isDefined() && !qname.resolvePrefixNamespace().isDefined()) {
                String description = XQueryBundle.message("inspection.XPST0081.unbound-qname-prefix.message");
                descriptors.add(manager.createProblemDescriptor(context.get(), description, (LocalQuickFix)null, ProblemHighlightType.GENERIC_ERROR, isOnTheFly));
            }
            return;
        }

        element = element.getFirstChild();
        while (element != null) {
            checkElement(element, manager, descriptors, isOnTheFly);
            element = element.getNextSibling();
        }
    }
}
