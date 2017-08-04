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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.util.List;

/** Checks non-XQuery 1.0 constructs against the selected implementation.
 *
 * Constructs that are not in the base XQuery 1.0 syntax implement the
 * {@link uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck}
 * interface to determine if the construct is valid for the given XQuery
 * implementation and associated dialect.
 */
public class UnsupportedConstructInspection extends LocalInspectionTool {
    @NotNull
    public String getDisplayName() {
        return XQueryBundle.message("inspection.XPST0003.unsupported-construct.display-name");
    }

    @Override
    public String getDescriptionFileName() {
        return getID() + ".html";
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof XQueryFile)) return null;

        XQueryVersion xqueryVersion = ((XQueryFile)file).getXQueryVersion().getVersionOrDefault(file.getProject());
        XQueryProjectSettings settings = XQueryProjectSettings.getInstance(file.getProject());
        ImplementationItem dialect = settings.getDialectForXQueryVersion(xqueryVersion);

        final List<ProblemDescriptor> descriptors = new SmartList<>();
        checkElement(file, dialect, manager, descriptors, isOnTheFly);
        return descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }

    private void checkElement(@NotNull PsiElement element,
                              @NotNull final ImplementationItem dialect,
                              @NotNull final InspectionManager manager,
                              @NotNull final List<ProblemDescriptor> descriptors,
                              boolean isOnTheFly) {
        if (element instanceof XQueryConformanceCheck) {
            XQueryConformanceCheck versioned = (XQueryConformanceCheck) element;
            if (!versioned.conformsTo(dialect)) {
                PsiElement context = versioned.getConformanceElement();
                String description = versioned.getConformanceErrorMessage();
                descriptors.add(manager.createProblemDescriptor(context, description, (LocalQuickFix)null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly));
            }
        }

        element = element.getFirstChild();
        while (element != null) {
            checkElement(element, dialect, manager, descriptors, isOnTheFly);
            element = element.getNextSibling();
        }
    }
}
