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
import uk.co.reecedunn.intellij.plugin.xquery.annotation.QNameAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class QNameAnnotatorTest extends AnnotatorTestCase {
    public void testQName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(18));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(0).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    public void testQName_KeywordPrefixPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(20));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(0).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    public void testQName_MissingPrefixPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(0));
    }

    public void testQName_KeywordLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(19));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(0).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(20));
        assertThat(annotations.get(1).getEndOffset(), is(25));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.IDENTIFIER));
    }

    public void testQName_MissingLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(1));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(18));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(0).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    public void testDirAttributeList_XmlnsAttribute() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(1));
        assertThat(annotations.get(0).getEndOffset(), is(2));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(0).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(11));
        assertThat(annotations.get(1).getEndOffset(), is(12));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }
}
