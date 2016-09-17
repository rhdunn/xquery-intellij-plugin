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
import com.intellij.lang.annotation.HighlightSeverity;
import uk.co.reecedunn.intellij.plugin.xquery.annotator.XQuerySupportedConstructAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQuerySupportedConstructAnnotatorTest extends AnnotatorTestCase {
    // region XQuery

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

    public void testXQuery30VersionDecl() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_3_0);
        final ASTNode node = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(0));
    }

    // endregion
    // region Update Facility

    public void testUpdateFacility10InsertExprInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.WARNING));
        assertThat(annotations.get(0).getMessage(), is("XPST0003: This expression requires Update Facility 1.0 or later."));
        assertThat(annotations.get(0).getTooltip(), is(nullValue()));
        assertThat(annotations.get(0).getStartOffset(), is(0));
        assertThat(annotations.get(0).getEndOffset(), is(6));
    }

    public void testUpdateFacility10InsertExpr() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        getSettings().setImplementation("w3c");
        getSettings().setXQuery10Dialect("w3c/1.0-update");
        final ASTNode node = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(0));
    }

    // endregion
    // region MarkLogic

    public void testMarkLogicForwardAxisInXQuery10() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0);
        final ASTNode node = parseResource("tests/parser/marklogic/ForwardAxis_Namespace.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.WARNING));
        assertThat(annotations.get(0).getMessage(), is("XPST0003: This expression requires MarkLogic 6.0 or later with XQuery version '1.0-ml'."));
        assertThat(annotations.get(0).getTooltip(), is(nullValue()));
        assertThat(annotations.get(0).getStartOffset(), is(0));
        assertThat(annotations.get(0).getEndOffset(), is(9));
    }

    public void testMarkLogicForwardAxis() {
        getSettings().setXQueryVersion(XQueryVersion.VERSION_1_0_MARKLOGIC);
        getSettings().setImplementation("marklogic");
        getSettings().setImplementationVersion("marklogic/v6");
        final ASTNode node = parseResource("tests/parser/marklogic/ForwardAxis_Namespace.xq");

        XQuerySupportedConstructAnnotator annotator = new XQuerySupportedConstructAnnotator();
        List<Annotation> annotations = annotateTree(node, annotator);
        assertThat(annotations.size(), is(0));
    }

    // endregion
}
