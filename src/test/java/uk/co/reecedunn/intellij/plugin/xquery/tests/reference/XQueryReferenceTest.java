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
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class).get();
        XQueryAnnotatedDecl annotatedDeclPsi = descendants(prologPsi).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        assertThat(functionDeclPsi, is(notNullValue()));

        XQueryQName qname = children(functionDeclPsi).findFirst(XQueryQName.class).get();
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
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class).get();
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class).get();
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
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class).get();
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class).get();
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
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class).get();
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class).get();
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class).get();
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
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class).get();
        XQueryForClause forClausePsi = descendants(intermediateClausePsi).findFirst(XQueryForClause.class).get();
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class).get();

        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryLetBinding letBindingPsi = children(letClausePsi).findFirst(XQueryLetBinding.class).get();
        XQueryVarName varNamePsi = children(letBindingPsi).findFirst(XQueryVarName.class).get();

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class).get();
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class).get();
        XQueryParam paramPsi = children(paramListPsi).findFirst(XQueryParam.class).get();
        XQueryEQName paramNamePsi = children(paramPsi).findFirst(XQueryEQName.class).get();

        XQueryFunctionBody functionBodyPsi = children(functionDeclPsi).findFirst(XQueryFunctionBody.class).get();
        XQueryExpr exprPsi = children(functionBodyPsi).findFirst(XQueryExpr.class).get();
        XQueryVarRef varRefPsi = descendants(exprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryQueryBody queryBodyPsi = children(mainModulePsi).findFirst(XQueryQueryBody.class).get();
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class).get();
        XQueryPositionalVar positionalVarPsi = children(forBindingPsi).findFirst(XQueryPositionalVar.class).get();
        XQueryVarName varNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class).get();

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class).get();
        XQueryVarName varNamePsi = children(slidingWindowClausePsi).findFirst(XQueryVarName.class).get();

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class).get();
        XQueryVarName varNamePsi = children(tumblingWindowClausePsi).findFirst(XQueryVarName.class).get();

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class).get();
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class).get();
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class).get();
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class).get();
        XQueryEQName varDeclNamePsi = children(varDeclPsi).findFirst(XQueryEQName.class).get();

        XQueryMainModule mainModulePsi = descendants(file).findFirst(XQueryMainModule.class).get();
        XQueryQueryBody queryBodyPsi = children(mainModulePsi).findFirst(XQueryQueryBody.class).get();
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class).get();
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class).get();

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
