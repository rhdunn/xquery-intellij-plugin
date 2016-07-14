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

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.tree.TokenSet;
import uk.co.reecedunn.intellij.plugin.xquery.annotator.XQueryStringLiteralAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryStringLiteralPsiImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryStringLiteralAnnotatorTest extends ParserTestCase {
    public void testXMLEntities() {
        final ASTNode node = parseText("\"&lt;&gt;&amp;&quot;&apos;\"").getFirstChildNode();
        assertThat(node.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteralAnnotator annotator = new XQueryStringLiteralAnnotator();
        AnnotationCollector holder = new AnnotationCollector();
        annotator.annotate(new XQueryStringLiteralPsiImpl(node), holder);

        assertThat(holder.annotations.size(), is(0));
    }

    public void testUnknownEntities() {
        final ASTNode node = parseText("\"&xyz;&ABC;\"").getFirstChildNode();
        assertThat(node.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteralAnnotator annotator = new XQueryStringLiteralAnnotator();
        AnnotationCollector holder = new AnnotationCollector();
        annotator.annotate(new XQueryStringLiteralPsiImpl(node), holder);

        assertThat(holder.annotations.size(), is(2));

        assertThat(holder.annotations.get(0).getStartOffset(), is(1));
        assertThat(holder.annotations.get(0).getEndOffset(), is(6));
        assertThat(holder.annotations.get(0).getSeverity(), is(HighlightSeverity.ERROR));
        assertThat(holder.annotations.get(0).getMessage(), is("Predefined entity '&xyz;' is not a valid XML entity."));
        assertThat(holder.annotations.get(0).getTooltip(), is(nullValue()));

        assertThat(holder.annotations.get(1).getStartOffset(), is(6));
        assertThat(holder.annotations.get(1).getEndOffset(), is(11));
        assertThat(holder.annotations.get(1).getSeverity(), is(HighlightSeverity.ERROR));
        assertThat(holder.annotations.get(1).getMessage(), is("Predefined entity '&ABC;' is not a valid XML entity."));
        assertThat(holder.annotations.get(1).getTooltip(), is(nullValue()));
    }
}
