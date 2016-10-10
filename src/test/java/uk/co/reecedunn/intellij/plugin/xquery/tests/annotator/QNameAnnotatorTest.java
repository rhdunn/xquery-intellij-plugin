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
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.markup.TextAttributes;
import uk.co.reecedunn.intellij.plugin.xquery.annotation.QNameAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class QNameAnnotatorTest extends AnnotatorTestCase {
    // region NCName

    public void testNCName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/OptionDecl.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(0));
    }

    public void testNCName_Keyword() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(24));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(15));
        assertThat(annotations.get(1).getEndOffset(), is(24));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.IDENTIFIER));
    }

    // endregion
    // region QName

    public void testQName() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(18));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(15));
        assertThat(annotations.get(1).getEndOffset(), is(18));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    public void testQName_KeywordPrefixPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(20));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(15));
        assertThat(annotations.get(1).getEndOffset(), is(20));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    public void testQName_MissingPrefixPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(0));
    }

    public void testQName_KeywordLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(4));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(19));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(15));
        assertThat(annotations.get(1).getEndOffset(), is(19));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));

        assertThat(annotations.get(2).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(2).getStartOffset(), is(20));
        assertThat(annotations.get(2).getEndOffset(), is(25));
        assertThat(annotations.get(2).getMessage(), is(nullValue()));
        assertThat(annotations.get(2).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(2).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(3).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(3).getStartOffset(), is(20));
        assertThat(annotations.get(3).getEndOffset(), is(25));
        assertThat(annotations.get(3).getMessage(), is(nullValue()));
        assertThat(annotations.get(3).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(3).getTextAttributes(), is(SyntaxHighlighter.IDENTIFIER));
    }

    public void testQName_MissingLocalPart() {
        final ASTNode node = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(15));
        assertThat(annotations.get(0).getEndOffset(), is(18));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(15));
        assertThat(annotations.get(1).getEndOffset(), is(18));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    // endregion
    // region URIQualifiedName

    public void testURIQualifiedName() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(0));
    }

    public void testURIQualifiedName_Keyword() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(21));
        assertThat(annotations.get(0).getEndOffset(), is(25));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(21));
        assertThat(annotations.get(1).getEndOffset(), is(25));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.IDENTIFIER));
    }

    // endregion
    // region DirAttributeList

    public void testDirAttributeList_XmlnsAttribute() {
        final ASTNode node = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(6));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(1));
        assertThat(annotations.get(0).getEndOffset(), is(2));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(1));
        assertThat(annotations.get(1).getEndOffset(), is(2));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.XML_TAG));

        assertThat(annotations.get(2).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(2).getStartOffset(), is(1));
        assertThat(annotations.get(2).getEndOffset(), is(2));
        assertThat(annotations.get(2).getMessage(), is(nullValue()));
        assertThat(annotations.get(2).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(2).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));

        assertThat(annotations.get(3).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(3).getStartOffset(), is(11));
        assertThat(annotations.get(3).getEndOffset(), is(12));
        assertThat(annotations.get(3).getMessage(), is(nullValue()));
        assertThat(annotations.get(3).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(3).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(4).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(4).getStartOffset(), is(11));
        assertThat(annotations.get(4).getEndOffset(), is(12));
        assertThat(annotations.get(4).getMessage(), is(nullValue()));
        assertThat(annotations.get(4).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(4).getTextAttributes(), is(SyntaxHighlighter.XML_TAG));

        assertThat(annotations.get(5).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(5).getStartOffset(), is(11));
        assertThat(annotations.get(5).getEndOffset(), is(12));
        assertThat(annotations.get(5).getMessage(), is(nullValue()));
        assertThat(annotations.get(5).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(5).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));
    }

    // endregion
    // region Annotation

    public void testAnnotation() {
        final ASTNode node = parseResource("tests/parser/xquery-3.0/Annotation.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(2));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(10));
        assertThat(annotations.get(0).getEndOffset(), is(17));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(10));
        assertThat(annotations.get(1).getEndOffset(), is(17));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.ANNOTATION));
    }

    public void testAnnotation_QName() {
        final ASTNode node = parseResource("tests/psi/xquery-3.0/Annotation_QName.xq");
        final List<Annotation> annotations = annotateTree(node, new QNameAnnotator());
        assertThat(annotations.size(), is(4));

        assertThat(annotations.get(0).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(0).getStartOffset(), is(10));
        assertThat(annotations.get(0).getEndOffset(), is(12));
        assertThat(annotations.get(0).getMessage(), is(nullValue()));
        assertThat(annotations.get(0).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(0).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(1).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(1).getStartOffset(), is(10));
        assertThat(annotations.get(1).getEndOffset(), is(12));
        assertThat(annotations.get(1).getMessage(), is(nullValue()));
        assertThat(annotations.get(1).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(1).getTextAttributes(), is(SyntaxHighlighter.NS_PREFIX));

        assertThat(annotations.get(2).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(2).getStartOffset(), is(13));
        assertThat(annotations.get(2).getEndOffset(), is(19));
        assertThat(annotations.get(2).getMessage(), is(nullValue()));
        assertThat(annotations.get(2).getEnforcedTextAttributes(), is(TextAttributes.ERASE_MARKER));
        assertThat(annotations.get(2).getTextAttributes(), is(HighlighterColors.NO_HIGHLIGHTING));

        assertThat(annotations.get(3).getSeverity(), is(HighlightSeverity.INFORMATION));
        assertThat(annotations.get(3).getStartOffset(), is(13));
        assertThat(annotations.get(3).getEndOffset(), is(19));
        assertThat(annotations.get(3).getMessage(), is(nullValue()));
        assertThat(annotations.get(3).getEnforcedTextAttributes(), is(nullValue()));
        assertThat(annotations.get(3).getTextAttributes(), is(SyntaxHighlighter.ANNOTATION));
    }

    // endregion
}
