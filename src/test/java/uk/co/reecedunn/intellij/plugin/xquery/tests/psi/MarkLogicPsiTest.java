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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi;

import com.intellij.lang.ASTNode;
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.MarkLogicBinaryExpr;
import uk.co.reecedunn.intellij.plugin.xquery.ast.marklogic.MarkLogicBinaryKindTest;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryLanguageType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class MarkLogicPsiTest extends ParserTestCase {
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_Namespace() {
        final ASTNode node = parseResource("tests/parser/marklogic/ForwardAxis_Namespace.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(XQueryVersion.VERSION_6_0));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(notNullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION).getNode().getElementType(), is(XQueryTokenType.K_NAMESPACE));
    }

    public void testForwardAxis_Property() {
        final ASTNode node = parseResource("tests/parser/marklogic/ForwardAxis_Property.xq");

        XQueryForwardAxis forwardAxisPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryForwardAxis.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)forwardAxisPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(XQueryVersion.VERSION_6_0));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(notNullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION).getNode().getElementType(), is(XQueryTokenType.K_PROPERTY));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryExpr

    public void testBinaryExpr() {
        final ASTNode node = parseResource("tests/parser/marklogic/BinaryExpr.xq");

        MarkLogicBinaryExpr binaryKindTestPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), MarkLogicBinaryExpr.class);
        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)binaryKindTestPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(XQueryVersion.VERSION_6_0));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(notNullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION).getNode().getElementType(), is(XQueryTokenType.K_BINARY));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryKindTest

    public void testBinaryKindTest() {
        final ASTNode node = parseResource("tests/parser/marklogic/BinaryKindTest.xq");

        XQueryAnnotatedDecl annotationDeclPsi = PsiNavigation.findFirstChildByClass(node.getPsi(), XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildrenByClass(annotationDeclPsi, XQueryVarDecl.class).get(0);
        XQueryTypeDeclaration typeDeclarationPsi = PsiNavigation.findChildrenByClass(varDeclPsi, XQueryTypeDeclaration.class).get(0);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildrenByClass(typeDeclarationPsi, XQuerySequenceType.class).get(0);
        MarkLogicBinaryKindTest binaryKindTestPsi = PsiNavigation.findFirstChildByClass(sequenceTypePsi, MarkLogicBinaryKindTest.class);

        XQueryVersionedConstruct versioned = (XQueryVersionedConstruct)binaryKindTestPsi;

        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeVersion(XQueryLanguageType.MARKLOGIC_EXTENSION), is(XQueryVersion.VERSION_6_0));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.XQUERY), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.UPDATE_FACILITY_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.FULL_TEXT_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.SCRIPTING_EXTENSION), is(nullValue()));
        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION), is(notNullValue()));

        assertThat(versioned.getLanguageTypeElement(XQueryLanguageType.MARKLOGIC_EXTENSION).getNode().getElementType(), is(XQueryTokenType.K_BINARY));
    }

    // endregion
}
