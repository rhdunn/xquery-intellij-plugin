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
package uk.co.reecedunn.intellij.plugin.xquery.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

public class XQuerySupportedConstructAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryVersionedConstruct)) return;
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)element;

        XQueryVersion xqueryVersion = PsiNavigation.findParentByClass(element, XQueryFile.class).getXQueryVersion();
        XQueryProjectSettings settings = XQueryProjectSettings.getInstance(element.getProject());
        ImplementationItem dialect = settings.getDialectForXQueryVersion(xqueryVersion);

        if (!versioned.conformsTo(dialect)) {
            holder.createWarningAnnotation(versioned.getConformanceElement(), versioned.getConformanceErrorMessage());
        }
    }
}
