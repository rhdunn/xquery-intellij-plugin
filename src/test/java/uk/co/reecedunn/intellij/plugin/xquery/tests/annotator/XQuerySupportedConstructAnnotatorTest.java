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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import uk.co.reecedunn.intellij.plugin.xquery.annotator.XQuerySupportedConstructAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuerySupportedConstructAnnotatorTest extends AnnotatorTestCase {
    public void testXQuery30VersionDeclInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.WARNING));
        assertThat(annotations.get(0).getMessage(), is("XPST0003: This expression requires XQuery 3.0 or later."));
        assertThat(annotations.get(0).getTooltip(), is(nullValue()));
        assertThat(annotations.get(0).getStartOffset(), is(7));
        assertThat(annotations.get(0).getEndOffset(), is(15));
    }

    public void testXQuery30VersionDeclInXQuery30() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(0));
    }
}
