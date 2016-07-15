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

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryStringLiteralPsiImpl;

import java.util.HashSet;
import java.util.Set;

public class XQueryStringLiteralAnnotator implements Annotator {
    private static final TokenSet PREDEFINED_ENTITY_REFS = TokenSet.create(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);

    private static final Set<CharSequence> XML_ENTITIES = new HashSet<>();
    private static final Set<CharSequence> HTML4_ENTITIES = new HashSet<>();

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XQueryStringLiteralPsiImpl)) return;

        final ASTNode node = element.getNode();
        for (ASTNode child : node.getChildren(PREDEFINED_ENTITY_REFS)) {
            checkPredefinedEntity(child.getChars(), child, holder);
        }
    }

    private void checkPredefinedEntity(@NotNull CharSequence entity, @NotNull ASTNode node, @NotNull AnnotationHolder holder) {
        CharSequence name = entity.subSequence(1, entity.length() - 1);
        if (XML_ENTITIES.contains(name)) return;

        if (HTML4_ENTITIES.contains(name)) {
            holder.createAnnotation(HighlightSeverity.ERROR, node.getTextRange(), XQueryBundle.message("annotator.string-literal.html4-entity", entity.toString()));
        } else {
            holder.createAnnotation(HighlightSeverity.ERROR, node.getTextRange(), XQueryBundle.message("annotator.string-literal.unknown-xml-entity", entity.toString()));
        }
    }

    static {
        HTML4_ENTITIES.add("Aacute");
        HTML4_ENTITIES.add("aacute");
        HTML4_ENTITIES.add("Acirc");
        HTML4_ENTITIES.add("acirc");
        HTML4_ENTITIES.add("acute");
        HTML4_ENTITIES.add("AElig");
        HTML4_ENTITIES.add("aelig");
        HTML4_ENTITIES.add("Agrave");
        HTML4_ENTITIES.add("agrave");
        HTML4_ENTITIES.add("alefsym");
        HTML4_ENTITIES.add("Alpha");
        HTML4_ENTITIES.add("alpha");
        XML_ENTITIES.add("amp");
        HTML4_ENTITIES.add("and");
        HTML4_ENTITIES.add("ang");
        XML_ENTITIES.add("apos");
        HTML4_ENTITIES.add("Aring");
        HTML4_ENTITIES.add("aring");
        HTML4_ENTITIES.add("asymp");
        HTML4_ENTITIES.add("Atilde");
        HTML4_ENTITIES.add("atilde");
        HTML4_ENTITIES.add("Auml");
        HTML4_ENTITIES.add("auml");
        HTML4_ENTITIES.add("bdquo");
        HTML4_ENTITIES.add("Beta");
        HTML4_ENTITIES.add("beta");
        HTML4_ENTITIES.add("brvbar");
        HTML4_ENTITIES.add("bull");
        HTML4_ENTITIES.add("cap");
        HTML4_ENTITIES.add("Ccedil");
        HTML4_ENTITIES.add("ccedil");
        HTML4_ENTITIES.add("cedil");
        HTML4_ENTITIES.add("cent");
        HTML4_ENTITIES.add("Chi");
        HTML4_ENTITIES.add("chi");
        HTML4_ENTITIES.add("circ");
        HTML4_ENTITIES.add("clubs");
        HTML4_ENTITIES.add("cong");
        HTML4_ENTITIES.add("copy");
        HTML4_ENTITIES.add("crarr");
        HTML4_ENTITIES.add("cup");
        HTML4_ENTITIES.add("curren");
        HTML4_ENTITIES.add("dagger");
        HTML4_ENTITIES.add("Dagger");
        HTML4_ENTITIES.add("darr");
        HTML4_ENTITIES.add("dArr");
        HTML4_ENTITIES.add("deg");
        HTML4_ENTITIES.add("Delta");
        HTML4_ENTITIES.add("delta");
        HTML4_ENTITIES.add("diams");
        HTML4_ENTITIES.add("divide");
        HTML4_ENTITIES.add("Eacute");
        HTML4_ENTITIES.add("eacute");
        HTML4_ENTITIES.add("Ecirc");
        HTML4_ENTITIES.add("ecirc");
        HTML4_ENTITIES.add("Egrave");
        HTML4_ENTITIES.add("egrave");
        HTML4_ENTITIES.add("empty");
        HTML4_ENTITIES.add("emsp");
        HTML4_ENTITIES.add("ensp");
        HTML4_ENTITIES.add("Epsilon");
        HTML4_ENTITIES.add("epsilon");
        HTML4_ENTITIES.add("equiv");
        HTML4_ENTITIES.add("Eta");
        HTML4_ENTITIES.add("eta");
        HTML4_ENTITIES.add("ETH");
        HTML4_ENTITIES.add("eth");
        HTML4_ENTITIES.add("Euml");
        HTML4_ENTITIES.add("euml");
        HTML4_ENTITIES.add("euro");
        HTML4_ENTITIES.add("exist");
        HTML4_ENTITIES.add("fnof");
        HTML4_ENTITIES.add("forall");
        HTML4_ENTITIES.add("frac12");
        HTML4_ENTITIES.add("frac14");
        HTML4_ENTITIES.add("frac34");
        HTML4_ENTITIES.add("frasl");
        HTML4_ENTITIES.add("Gamma");
        HTML4_ENTITIES.add("gamma");
        HTML4_ENTITIES.add("ge");
        XML_ENTITIES.add("gt");
        HTML4_ENTITIES.add("harr");
        HTML4_ENTITIES.add("hArr");
        HTML4_ENTITIES.add("hearts");
        HTML4_ENTITIES.add("hellip");
        HTML4_ENTITIES.add("Iacute");
        HTML4_ENTITIES.add("iacute");
        HTML4_ENTITIES.add("Icirc");
        HTML4_ENTITIES.add("icirc");
        HTML4_ENTITIES.add("iexcl");
        HTML4_ENTITIES.add("Igrave");
        HTML4_ENTITIES.add("igrave");
        HTML4_ENTITIES.add("image");
        HTML4_ENTITIES.add("infin");
        HTML4_ENTITIES.add("int");
        HTML4_ENTITIES.add("Iota");
        HTML4_ENTITIES.add("iota");
        HTML4_ENTITIES.add("iquest");
        HTML4_ENTITIES.add("isin");
        HTML4_ENTITIES.add("Iuml");
        HTML4_ENTITIES.add("iuml");
        HTML4_ENTITIES.add("Kappa");
        HTML4_ENTITIES.add("kappa");
        HTML4_ENTITIES.add("Lambda");
        HTML4_ENTITIES.add("lambda");
        HTML4_ENTITIES.add("lang");
        HTML4_ENTITIES.add("laquo");
        HTML4_ENTITIES.add("larr");
        HTML4_ENTITIES.add("lArr");
        HTML4_ENTITIES.add("lceil");
        HTML4_ENTITIES.add("ldquo");
        HTML4_ENTITIES.add("le");
        HTML4_ENTITIES.add("lfloor");
        HTML4_ENTITIES.add("lowast");
        HTML4_ENTITIES.add("loz");
        HTML4_ENTITIES.add("lrm");
        HTML4_ENTITIES.add("lsaquo");
        HTML4_ENTITIES.add("lsquo");
        XML_ENTITIES.add("lt");
        HTML4_ENTITIES.add("macr");
        HTML4_ENTITIES.add("mdash");
        HTML4_ENTITIES.add("micro");
        HTML4_ENTITIES.add("middot");
        HTML4_ENTITIES.add("minus");
        HTML4_ENTITIES.add("Mu");
        HTML4_ENTITIES.add("mu");
        HTML4_ENTITIES.add("nabla");
        HTML4_ENTITIES.add("nbsp");
        HTML4_ENTITIES.add("ndash");
        HTML4_ENTITIES.add("ne");
        HTML4_ENTITIES.add("ni");
        HTML4_ENTITIES.add("not");
        HTML4_ENTITIES.add("notin");
        HTML4_ENTITIES.add("nsub");
        HTML4_ENTITIES.add("Ntilde");
        HTML4_ENTITIES.add("ntilde");
        HTML4_ENTITIES.add("Nu");
        HTML4_ENTITIES.add("nu");
        HTML4_ENTITIES.add("Oacute");
        HTML4_ENTITIES.add("oacute");
        HTML4_ENTITIES.add("Ocirc");
        HTML4_ENTITIES.add("ocirc");
        HTML4_ENTITIES.add("OElig");
        HTML4_ENTITIES.add("oelig");
        HTML4_ENTITIES.add("Ograve");
        HTML4_ENTITIES.add("ograve");
        HTML4_ENTITIES.add("oline");
        HTML4_ENTITIES.add("Omega");
        HTML4_ENTITIES.add("omega");
        HTML4_ENTITIES.add("Omicron");
        HTML4_ENTITIES.add("omicron");
        HTML4_ENTITIES.add("oplus");
        HTML4_ENTITIES.add("or");
        HTML4_ENTITIES.add("ordf");
        HTML4_ENTITIES.add("ordm");
        HTML4_ENTITIES.add("Oslash");
        HTML4_ENTITIES.add("oslash");
        HTML4_ENTITIES.add("Otilde");
        HTML4_ENTITIES.add("otilde");
        HTML4_ENTITIES.add("otimes");
        HTML4_ENTITIES.add("Ouml");
        HTML4_ENTITIES.add("ouml");
        HTML4_ENTITIES.add("para");
        HTML4_ENTITIES.add("part");
        HTML4_ENTITIES.add("permil");
        HTML4_ENTITIES.add("perp");
        HTML4_ENTITIES.add("Phi");
        HTML4_ENTITIES.add("phi");
        HTML4_ENTITIES.add("Pi");
        HTML4_ENTITIES.add("pi");
        HTML4_ENTITIES.add("piv");
        HTML4_ENTITIES.add("plusmn");
        HTML4_ENTITIES.add("pound");
        HTML4_ENTITIES.add("prime");
        HTML4_ENTITIES.add("Prime");
        HTML4_ENTITIES.add("prod");
        HTML4_ENTITIES.add("prop");
        HTML4_ENTITIES.add("Psi");
        HTML4_ENTITIES.add("psi");
        XML_ENTITIES.add("quot");
        HTML4_ENTITIES.add("radic");
        HTML4_ENTITIES.add("rang");
        HTML4_ENTITIES.add("raquo");
        HTML4_ENTITIES.add("rarr");
        HTML4_ENTITIES.add("rArr");
        HTML4_ENTITIES.add("rceil");
        HTML4_ENTITIES.add("rdquo");
        HTML4_ENTITIES.add("real");
        HTML4_ENTITIES.add("reg");
        HTML4_ENTITIES.add("rfloor");
        HTML4_ENTITIES.add("Rho");
        HTML4_ENTITIES.add("rho");
        HTML4_ENTITIES.add("rlm");
        HTML4_ENTITIES.add("rsaquo");
        HTML4_ENTITIES.add("rsquo");
        HTML4_ENTITIES.add("sbquo");
        HTML4_ENTITIES.add("Scaron");
        HTML4_ENTITIES.add("scaron");
        HTML4_ENTITIES.add("sdot");
        HTML4_ENTITIES.add("sect");
        HTML4_ENTITIES.add("shy");
        HTML4_ENTITIES.add("Sigma");
        HTML4_ENTITIES.add("sigma");
        HTML4_ENTITIES.add("sigmaf");
        HTML4_ENTITIES.add("sim");
        HTML4_ENTITIES.add("spades");
        HTML4_ENTITIES.add("sub");
        HTML4_ENTITIES.add("sube");
        HTML4_ENTITIES.add("sum");
        HTML4_ENTITIES.add("sup");
        HTML4_ENTITIES.add("sup1");
        HTML4_ENTITIES.add("sup2");
        HTML4_ENTITIES.add("sup3");
        HTML4_ENTITIES.add("supe");
        HTML4_ENTITIES.add("szlig");
        HTML4_ENTITIES.add("Tau");
        HTML4_ENTITIES.add("tau");
        HTML4_ENTITIES.add("there4");
        HTML4_ENTITIES.add("Theta");
        HTML4_ENTITIES.add("theta");
        HTML4_ENTITIES.add("thetasym");
        HTML4_ENTITIES.add("thinsp");
        HTML4_ENTITIES.add("THORN");
        HTML4_ENTITIES.add("thorn");
        HTML4_ENTITIES.add("tilde");
        HTML4_ENTITIES.add("times");
        HTML4_ENTITIES.add("trade");
        HTML4_ENTITIES.add("Uacute");
        HTML4_ENTITIES.add("uacute");
        HTML4_ENTITIES.add("uarr");
        HTML4_ENTITIES.add("uArr");
        HTML4_ENTITIES.add("Ucirc");
        HTML4_ENTITIES.add("ucirc");
        HTML4_ENTITIES.add("Ugrave");
        HTML4_ENTITIES.add("ugrave");
        HTML4_ENTITIES.add("uml");
        HTML4_ENTITIES.add("upsih");
        HTML4_ENTITIES.add("Upsilon");
        HTML4_ENTITIES.add("upsilon");
        HTML4_ENTITIES.add("Uuml");
        HTML4_ENTITIES.add("uuml");
        HTML4_ENTITIES.add("weierp");
        HTML4_ENTITIES.add("Xi");
        HTML4_ENTITIES.add("xi");
        HTML4_ENTITIES.add("Yacute");
        HTML4_ENTITIES.add("yacute");
        HTML4_ENTITIES.add("yen");
        HTML4_ENTITIES.add("yuml");
        HTML4_ENTITIES.add("Yuml");
        HTML4_ENTITIES.add("Zeta");
        HTML4_ENTITIES.add("zeta");
        HTML4_ENTITIES.add("zwj");
        HTML4_ENTITIES.add("zwnj");
    }
}
