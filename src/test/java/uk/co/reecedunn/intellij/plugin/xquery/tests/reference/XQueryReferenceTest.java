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
package uk.co.reecedunn.intellij.plugin.xquery.tests.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.reecedunn.intellij.plugin.xquery.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.xquery.functional.PsiTreeWalker.descendants;

@SuppressWarnings("ConstantConditions")
public class XQueryReferenceTest extends ParserTestCase {
    // region Files
    // region URILiteral

    public void testURILiteral_HttpUri() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/ModuleImport_URILiteral_SameDirectory.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class).get();
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = children(moduleImportPsi).toListOf(XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference httpUriRef = uriLiterals.get(0).getReference();
        assertThat(httpUriRef.getCanonicalText(), is("http://example.com/test"));
        assertThat(httpUriRef.getVariants().length, is(0));

        PsiElement resolved = httpUriRef.resolve();
        assertThat(resolved, is(nullValue()));
    }

    // endregion
    // endregion
    // region Namespaces
    // region QName

    public void testQName() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/ModuleDecl.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class).get();
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = descendants(prologPsi).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        assertThat(functionDeclPsi, is(notNullValue()));

        XQueryQName qname = PsiNavigation.findChildByClass(functionDeclPsi, XQueryQName.class);
        assertThat(qname, is(notNullValue()));

        PsiReference ref = qname.getReference();
        assertThat(ref.getCanonicalText(), is("test"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(LeafPsiElement.class)));
        assertThat(resolved.getText(), is("test"));
        assertThat(resolved.getParent().getParent(), is(instanceOf(XQueryModuleDecl.class)));

        PsiReference[] refs = qname.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("test"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(LeafPsiElement.class)));
        assertThat(resolved.getText(), is("test"));
        assertThat(resolved.getParent().getParent(), is(instanceOf(XQueryModuleDecl.class)));
    }

    // endregion
    // region EQName

    public void testEQName_NCName() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithNCNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class).get();
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class).get();
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref, is(nullValue()));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(0));
    }

    public void testEQName_QName() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithQNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class).get();
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class).get();
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref.getCanonicalText(), is("xs"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(LeafPsiElement.class)));
        assertThat(resolved.getText(), is("xs"));
        assertThat(resolved.getParent().getParent(), is(instanceOf(XQueryNamespaceDecl.class)));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("xs"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(LeafPsiElement.class)));
        assertThat(resolved.getText(), is("xs"));
        assertThat(resolved.getParent().getParent(), is(instanceOf(XQueryNamespaceDecl.class)));
    }

    public void testEQName_URIQualifiedName() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithURIQualifiedNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class).get();
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class).get();
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref, is(nullValue()));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(0));
    }

    // endregion
    // endregion
    // region Variables
    // region ForBinding

    public void testForBinding() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/ForClause.xq");

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region IntermediateClause

    public void testIntermediateClause() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/IntermediateClause_ReturnInnerForVariable.xq");

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryIntermediateClause intermediateClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryIntermediateClause.class);
        XQueryForClause forClausePsi = descendants(intermediateClausePsi).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryVarName.class);

        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("z"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("z"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region LetBinding

    public void testLetBinding() {
        final XQueryFile file = parseResource("tests/parser/xquery-1.0/LetClause.xq");

        XQueryLetClause letClausePsi = descendants(file).findFirst(XQueryLetClause.class).get();
        XQueryLetBinding letBindingPsi = PsiNavigation.findChildByClass(letClausePsi, XQueryLetBinding.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(letBindingPsi, XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region Param

    public void testParam() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/FunctionDecl_ReturningSpecifiedParam.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryParamList.class);
        XQueryParam paramPsi = PsiNavigation.findChildByClass(paramListPsi, XQueryParam.class);
        XQueryEQName paramNamePsi = PsiNavigation.findChildByClass(paramPsi, XQueryEQName.class);

        XQueryFunctionBody functionBodyPsi = PsiNavigation.findChildByClass(functionDeclPsi, XQueryFunctionBody.class);
        XQueryExpr exprPsi = PsiNavigation.findChildByClass(functionBodyPsi, XQueryExpr.class);
        XQueryVarRef varRefPsi = descendants(exprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryNCName.class)));
        assertThat(resolved, is(paramNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryNCName.class)));
        assertThat(resolved, is(paramNamePsi));
    }

    public void testParam_ReferencedFromOutsideTheFunction() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/FunctionDecl_ReturningSpecifiedParam.xq");

        XQueryMainModule mainModulePsi = descendants(file).findFirst(XQueryMainModule.class).get();
        XQueryQueryBody queryBodyPsi = PsiNavigation.findChildByClass(mainModulePsi, XQueryQueryBody.class);
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(nullValue()));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(nullValue()));
    }

    // endregion
    // region PositionalVar

    public void testPositionalVar() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/PositionalVar_ReturnThePosition.xq");

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = PsiNavigation.findChildByClass(forClausePsi, XQueryForBinding.class);
        XQueryPositionalVar positionalVarPsi = PsiNavigation.findChildByClass(forBindingPsi, XQueryPositionalVar.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(positionalVarPsi, XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("i"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("i"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region SlidingWindowClause

    public void testSlidingWindowClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq");

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQuerySlidingWindowClause slidingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQuerySlidingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(slidingWindowClausePsi, XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region TumblingWindowClause

    public void testTumblingWindowClause() {
        final XQueryFile file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq");

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class).get();
        XQueryTumblingWindowClause tumblingWindowClausePsi = PsiNavigation.findChildByClass(windowClausePsi, XQueryTumblingWindowClause.class);
        XQueryVarName varNamePsi = PsiNavigation.findChildByClass(tumblingWindowClausePsi, XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = PsiNavigation.findChildByClass(flworExprPsi, XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = PsiNavigation.findChildByClass(returnClausePsi, XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("x"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("x"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varNamePsi));
    }

    // endregion
    // region VarDecl

    public void testVarDecl() {
        final XQueryFile file = parseResource("tests/resolve/xquery-1.0/VarDecl_WithCorrespondingVarRef.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryVarDecl varDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryVarDecl.class);
        XQueryEQName varDeclNamePsi = PsiNavigation.findChildByClass(varDeclPsi, XQueryEQName.class);

        XQueryMainModule mainModulePsi = descendants(file).findFirst(XQueryMainModule.class).get();
        XQueryQueryBody queryBodyPsi = PsiNavigation.findChildByClass(mainModulePsi, XQueryQueryBody.class);
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = PsiNavigation.findChildByClass(varRefPsi, XQueryEQName.class);

        PsiReference ref = varRefNamePsi.getReference();
        assertThat(ref.getCanonicalText(), is("value"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varDeclNamePsi));

        PsiReference[] refs = varRefNamePsi.getReferences();
        assertThat(refs.length, is(1));

        assertThat(refs[0].getCanonicalText(), is("value"));
        assertThat(refs[0].getVariants().length, is(0));

        resolved = refs[0].resolve();
        assertThat(resolved, is(instanceOf(XQueryVarName.class)));
        assertThat(resolved, is(varDeclNamePsi));
    }

    // endregion
    // endregion
}
