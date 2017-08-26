/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.XQST0031;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVersionRef;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.util.List;

/** XPST0081 error condition
 *
 * It is a <em>static error</em> if a VersionDecl specifies a version that is
 * not supported by the implementation.
 */
public class UnsupportedXQueryVersionInspection extends LocalInspectionTool {
    @NotNull
    public String getDisplayName() {
        return XQueryBundle.message("inspection.XQST0031.unsupported-version.display-name");
    }

    @Override
    public String getDescriptionFileName() {
        return getID() + ".html";
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof XQueryFile)) return null;

        XQueryVersionRef version = ((XQueryFile)file).getXQueryVersion();
        if (version.getVersion() == XQueryVersion.UNSUPPORTED || version.getDeclaration() == null) {
            if (version.getDeclaration() != null) {
                return createUnsupportedVersionProblemDescriptors(manager, version, isOnTheFly);
            }
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        XQueryProjectSettings settings = XQueryProjectSettings.Companion.getInstance(file.getProject());
        ImplementationItem implementation = settings.getImplementationVersionItem();
        List<ImplementationItem> dialect = implementation.getItemsByVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQuery.INSTANCE, version.getVersion());
        if (dialect.get(0).getID() != null) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        return createUnsupportedVersionProblemDescriptors(manager, version, isOnTheFly);
    }

    private ProblemDescriptor[] createUnsupportedVersionProblemDescriptors(@NotNull final InspectionManager manager, final XQueryVersionRef version, final boolean isOnTheFly) {
        String description = XQueryBundle.message("inspection.XQST0031.unsupported-version.message");
        return new ProblemDescriptor[] {
            manager.createProblemDescriptor(version.getDeclaration(), description, (LocalQuickFix)null, ProblemHighlightType.GENERIC_ERROR, isOnTheFly)
        };
    }
}
