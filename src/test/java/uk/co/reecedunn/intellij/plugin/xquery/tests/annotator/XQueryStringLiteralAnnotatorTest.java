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
import com.intellij.lang.annotation.HighlightSeverity;
import uk.co.reecedunn.intellij.plugin.xquery.annotator.XQueryStringLiteralAnnotator;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryStringLiteralPsiImpl;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryStringLiteralAnnotatorTest extends ParserTestCase {
    private static final String HTML4_ENTITIES
            = "\""
            + "&Aacute;"
            + "&aacute;"
            + "&Acirc;"
            + "&acirc;"
            + "&acute;"
            + "&AElig;"
            + "&aelig;"
            + "&Agrave;"
            + "&agrave;"
            + "&alefsym;"
            + "&Alpha;"
            + "&alpha;"
            + "&and;"
            + "&ang;"
            + "&Aring;"
            + "&aring;"
            + "&asymp;"
            + "&Atilde;"
            + "&atilde;"
            + "&Auml;"
            + "&auml;"
            + "&bdquo;"
            + "&Beta;"
            + "&beta;"
            + "&brvbar;"
            + "&bull;"
            + "&cap;"
            + "&Ccedil;"
            + "&ccedil;"
            + "&cedil;"
            + "&cent;"
            + "&Chi;"
            + "&chi;"
            + "&circ;"
            + "&clubs;"
            + "&cong;"
            + "&copy;"
            + "&crarr;"
            + "&cup;"
            + "&curren;"
            + "&dagger;"
            + "&Dagger;"
            + "&darr;"
            + "&dArr;"
            + "&deg;"
            + "&Delta;"
            + "&delta;"
            + "&diams;"
            + "&divide;"
            + "&Eacute;"
            + "&eacute;"
            + "&Ecirc;"
            + "&ecirc;"
            + "&Egrave;"
            + "&egrave;"
            + "&empty;"
            + "&emsp;"
            + "&ensp;"
            + "&Epsilon;"
            + "&epsilon;"
            + "&equiv;"
            + "&Eta;"
            + "&eta;"
            + "&ETH;"
            + "&eth;"
            + "&Euml;"
            + "&euml;"
            + "&euro;"
            + "&exist;"
            + "&fnof;"
            + "&forall;"
            + "&frac12;"
            + "&frac14;"
            + "&frac34;"
            + "&frasl;"
            + "&Gamma;"
            + "&gamma;"
            + "&ge;"
            + "&harr;"
            + "&hArr;"
            + "&hearts;"
            + "&hellip;"
            + "&Iacute;"
            + "&iacute;"
            + "&Icirc;"
            + "&icirc;"
            + "&iexcl;"
            + "&Igrave;"
            + "&igrave;"
            + "&image;"
            + "&infin;"
            + "&int;"
            + "&Iota;"
            + "&iota;"
            + "&iquest;"
            + "&isin;"
            + "&Iuml;"
            + "&iuml;"
            + "&Kappa;"
            + "&kappa;"
            + "&Lambda;"
            + "&lambda;"
            + "&lang;"
            + "&laquo;"
            + "&larr;"
            + "&lArr;"
            + "&lceil;"
            + "&ldquo;"
            + "&le;"
            + "&lfloor;"
            + "&lowast;"
            + "&loz;"
            + "&lrm;"
            + "&lsaquo;"
            + "&lsquo;"
            + "&macr;"
            + "&mdash;"
            + "&micro;"
            + "&middot;"
            + "&minus;"
            + "&Mu;"
            + "&mu;"
            + "&nabla;"
            + "&nbsp;"
            + "&ndash;"
            + "&ne;"
            + "&ni;"
            + "&not;"
            + "&notin;"
            + "&nsub;"
            + "&Ntilde;"
            + "&ntilde;"
            + "&Nu;"
            + "&nu;"
            + "&Oacute;"
            + "&oacute;"
            + "&Ocirc;"
            + "&ocirc;"
            + "&OElig;"
            + "&oelig;"
            + "&Ograve;"
            + "&ograve;"
            + "&oline;"
            + "&Omega;"
            + "&omega;"
            + "&Omicron;"
            + "&omicron;"
            + "&oplus;"
            + "&or;"
            + "&ordf;"
            + "&ordm;"
            + "&Oslash;"
            + "&oslash;"
            + "&Otilde;"
            + "&otilde;"
            + "&otimes;"
            + "&Ouml;"
            + "&ouml;"
            + "&para;"
            + "&part;"
            + "&permil;"
            + "&perp;"
            + "&Phi;"
            + "&phi;"
            + "&Pi;"
            + "&pi;"
            + "&piv;"
            + "&plusmn;"
            + "&pound;"
            + "&prime;"
            + "&Prime;"
            + "&prod;"
            + "&prop;"
            + "&Psi;"
            + "&psi;"
            + "&radic;"
            + "&rang;"
            + "&raquo;"
            + "&rarr;"
            + "&rArr;"
            + "&rceil;"
            + "&rdquo;"
            + "&real;"
            + "&reg;"
            + "&rfloor;"
            + "&Rho;"
            + "&rho;"
            + "&rlm;"
            + "&rsaquo;"
            + "&rsquo;"
            + "&sbquo;"
            + "&Scaron;"
            + "&scaron;"
            + "&sdot;"
            + "&sect;"
            + "&shy;"
            + "&Sigma;"
            + "&sigma;"
            + "&sigmaf;"
            + "&sim;"
            + "&spades;"
            + "&sub;"
            + "&sube;"
            + "&sum;"
            + "&sup;"
            + "&sup1;"
            + "&sup2;"
            + "&sup3;"
            + "&supe;"
            + "&szlig;"
            + "&Tau;"
            + "&tau;"
            + "&there4;"
            + "&Theta;"
            + "&theta;"
            + "&thetasym;"
            + "&thinsp;"
            + "&THORN;"
            + "&thorn;"
            + "&tilde;"
            + "&times;"
            + "&trade;"
            + "&Uacute;"
            + "&uacute;"
            + "&uarr;"
            + "&uArr;"
            + "&Ucirc;"
            + "&ucirc;"
            + "&Ugrave;"
            + "&ugrave;"
            + "&uml;"
            + "&upsih;"
            + "&Upsilon;"
            + "&upsilon;"
            + "&Uuml;"
            + "&uuml;"
            + "&weierp;"
            + "&Xi;"
            + "&xi;"
            + "&Yacute;"
            + "&yacute;"
            + "&yen;"
            + "&yuml;"
            + "&Yuml;"
            + "&Zeta;"
            + "&zeta;"
            + "&zwj;"
            + "&zwnj;"
            + "\"";

    public void testXMLEntities() {
        final ASTNode node = parseText("\"&lt;&gt;&amp;&quot;&apos;\"").getFirstChildNode();
        assertThat(node.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteralAnnotator annotator = new XQueryStringLiteralAnnotator();
        AnnotationCollector holder = new AnnotationCollector();
        annotator.annotate(new XQueryStringLiteralPsiImpl(node), holder);

        assertThat(holder.annotations.size(), is(0));
    }

    @Specification(name="HTML Latin 1 DTD", reference="http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent")
    @Specification(name="HTML Symbols DTD", reference="http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent")
    @Specification(name="HTML Special DTD", reference="http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent")
    public void testHTML4Entities() {
        final ASTNode node = parseText(HTML4_ENTITIES).getFirstChildNode();
        assertThat(node.getElementType(), is(XQueryElementType.STRING_LITERAL));

        XQueryStringLiteralAnnotator annotator = new XQueryStringLiteralAnnotator();
        AnnotationCollector holder = new AnnotationCollector();
        annotator.annotate(new XQueryStringLiteralPsiImpl(node), holder);

        assertThat(holder.annotations.size(), is(248));

        assertThat(holder.annotations.get(0).getStartOffset(), is(1));
        assertThat(holder.annotations.get(0).getEndOffset(), is(9));
        assertThat(holder.annotations.get(0).getSeverity(), is(HighlightSeverity.ERROR));
        assertThat(holder.annotations.get(0).getMessage(), is("HTML4 predefined entity '&Aacute;' is not allowed in this XQuery version."));
        assertThat(holder.annotations.get(0).getTooltip(), is(nullValue()));
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
        assertThat(holder.annotations.get(0).getMessage(), is("Predefined entity '&xyz;' is not a known entity name."));
        assertThat(holder.annotations.get(0).getTooltip(), is(nullValue()));

        assertThat(holder.annotations.get(1).getStartOffset(), is(6));
        assertThat(holder.annotations.get(1).getEndOffset(), is(11));
        assertThat(holder.annotations.get(1).getSeverity(), is(HighlightSeverity.ERROR));
        assertThat(holder.annotations.get(1).getMessage(), is("Predefined entity '&ABC;' is not a known entity name."));
        assertThat(holder.annotations.get(1).getTooltip(), is(nullValue()));
    }
}
