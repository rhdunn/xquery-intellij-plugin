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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryConformance;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

public class XQuerySupportedConstructAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryVersionedConstruct)) return;
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)element;

        XQueryVersion xqueryVersion = PsiNavigation.findParentByClass(element, XQueryFile.class).getXQueryVersion();
        XQueryProjectSettings settings = XQueryProjectSettings.getInstance(element.getProject());
        ImplementationItem dialect = settings.getDialectForXQueryVersion(xqueryVersion);

        checkVersion(holder, versioned, XQueryConformance.XQUERY, xqueryVersion, "annotator.requires.xquery.version");
        checkVersion(holder, versioned, XQueryConformance.UPDATE_FACILITY_EXTENSION, dialect, "annotator.requires.update-facility.version");
        checkVersion(holder, versioned, XQueryConformance.MARKLOGIC_EXTENSION, dialect, "annotator.requires.marklogic.version");
    }

    private void checkVersion(AnnotationHolder holder, XQueryVersionedConstruct versioned, XQueryConformance type, ImplementationItem dialect, String key) {
        XQueryVersion version = dialect.getVersion(type);
        if (version == null) {
            XQueryVersion typeVersion = versioned.getConformanceVersion(type);
            if (typeVersion != null) {
                final PsiElement node = versioned.getConformanceElement(type);
                final String message = XQueryBundle.message(key, typeVersion.toString());
                holder.createWarningAnnotation(node, message);
            }
        } else {
            checkVersion(holder, versioned, type, version, key);
        }
    }

    private void checkVersion(AnnotationHolder holder, XQueryVersionedConstruct versioned, XQueryConformance type, XQueryVersion xqueryVersion, String key) {
        XQueryVersion version = versioned.getConformanceVersion(type);
        if (version != null) {
            if (!xqueryVersion.supportsVersion(version)) {
                final PsiElement node = versioned.getConformanceElement(type);
                final String message = XQueryBundle.message(key, version.toString());
                holder.createWarningAnnotation(node, message);
            }
        }
    }
}
