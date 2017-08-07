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
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;
import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.descendants;

@SuppressWarnings("ConstantConditions")
public class XQueryReferenceTest extends ParserTestCase {
    // region Files
    // region URILiteral

    public void testURILiteral_HttpUri() {
        final XQueryFile file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = children(moduleImportPsi).toListOf(XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference httpUriRef = uriLiterals.get(0).getReference();
        assertThat(httpUriRef.getCanonicalText(), is("http://example.com/test"));
        assertThat(httpUriRef.getVariants().length, is(0));

        PsiElement resolved = httpUriRef.resolve();
        assertThat(resolved, is(nullValue()));
    }

    public void testURILiteral_SameDirectory() {
        final XQueryFile file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = children(moduleImportPsi).toListOf(XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference ref = uriLiterals.get(1).getReference();
        assertThat(ref.getCanonicalText(), is("test.xq"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(notNullValue()));
        assertThat(resolved, instanceOf(XQueryFile.class));
        assertThat(resolved.getContainingFile().getName(), is("test.xq"));
    }

    public void testURILiteral_BuiltinResource() {
        final XQueryFile file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = children(moduleImportPsi).toListOf(XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference ref = uriLiterals.get(1).getReference();
        assertThat(ref.getCanonicalText(), is("res://www.w3.org/2005/xpath-functions/array.xqy"));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(notNullValue()));
        assertThat(resolved, instanceOf(XQueryFile.class));
        assertThat(resolved.getContainingFile().getName(), is("array.xqy"));
    }

    public void testURILiteral_Empty() {
        final XQueryFile file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq");

        XQueryModuleImport moduleImportPsi = descendants(file).findFirst(XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = children(moduleImportPsi).toListOf(XQueryUriLiteral.class);
        assertThat(uriLiterals.size(), is(2));

        PsiReference ref = uriLiterals.get(1).getReference();
        assertThat(ref.getCanonicalText(), is(""));
        assertThat(ref.getVariants().length, is(0));

        PsiElement resolved = ref.resolve();
        assertThat(resolved, is(nullValue()));
    }

    // endregion
    // endregion
    // region Namespaces
    // region QName

    public void testQName() {
        final XQueryFile file = parseResource("tests/resolve/namespaces/ModuleDecl.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class);
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = descendants(prologPsi).findFirst(XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class);
        assertThat(functionDeclPsi, is(notNullValue()));

        XQueryQName qname = children(functionDeclPsi).findFirst(XQueryQName.class);
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
        final XQueryFile file = parseResource("tests/resolve/namespaces/FunctionDecl_WithNCNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class);
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class);
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref, is(nullValue()));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(0));
    }

    public void testEQName_QName() {
        final XQueryFile file = parseResource("tests/resolve/namespaces/FunctionDecl_WithQNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class);
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class);
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
        final XQueryFile file = parseResource("tests/resolve/namespaces/FunctionDecl_WithURIQualifiedNameReturnType.xq");

        XQueryLibraryModule modulePsi = descendants(file).findFirst(XQueryLibraryModule.class);
        XQueryProlog prologPsi = children(modulePsi).findFirst(XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = children(prologPsi).findFirst(XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = children(functionDeclPsi).findFirst(XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = descendants(sequenceTypePsi).findFirst(XQueryEQName.class);
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

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class);
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class);
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
        final XQueryFile file = parseResource("tests/resolve/variables/IntermediateClause_ReturnInnerForVariable.xq");

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryIntermediateClause intermediateClausePsi = children(flworExprPsi).findFirst(XQueryIntermediateClause.class);
        XQueryForClause forClausePsi = descendants(intermediateClausePsi).findFirst(XQueryForClause.class);
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class);
        XQueryVarName varNamePsi = children(forBindingPsi).findFirst(XQueryVarName.class);

        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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

        XQueryLetClause letClausePsi = descendants(file).findFirst(XQueryLetClause.class);
        XQueryLetBinding letBindingPsi = children(letClausePsi).findFirst(XQueryLetBinding.class);
        XQueryVarName varNamePsi = children(letBindingPsi).findFirst(XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
        final XQueryFile file = parseResource("tests/resolve/variables/FunctionDecl_ReturningSpecifiedParam.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = children(annotatedDeclPsi).findFirst(XQueryFunctionDecl.class);
        XQueryParamList paramListPsi = children(functionDeclPsi).findFirst(XQueryParamList.class);
        XQueryParam paramPsi = children(paramListPsi).findFirst(XQueryParam.class);
        XQueryEQName paramNamePsi = children(paramPsi).findFirst(XQueryEQName.class);

        XQueryFunctionBody functionBodyPsi = children(functionDeclPsi).findFirst(XQueryFunctionBody.class);
        XQueryExpr exprPsi = children(functionBodyPsi).findFirst(XQueryExpr.class);
        XQueryVarRef varRefPsi = descendants(exprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
        final XQueryFile file = parseResource("tests/resolve/variables/FunctionDecl_ReturningSpecifiedParam.xq");

        XQueryMainModule mainModulePsi = descendants(file).findFirst(XQueryMainModule.class);
        XQueryQueryBody queryBodyPsi = children(mainModulePsi).findFirst(XQueryQueryBody.class);
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
        final XQueryFile file = parseResource("tests/resolve/variables/PositionalVar_ReturnThePosition.xq");

        XQueryForClause forClausePsi = descendants(file).findFirst(XQueryForClause.class);
        XQueryForBinding forBindingPsi = children(forClausePsi).findFirst(XQueryForBinding.class);
        XQueryPositionalVar positionalVarPsi = children(forBindingPsi).findFirst(XQueryPositionalVar.class);
        XQueryVarName varNamePsi = children(positionalVarPsi).findFirst(XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class);
        XQuerySlidingWindowClause slidingWindowClausePsi = children(windowClausePsi).findFirst(XQuerySlidingWindowClause.class);
        XQueryVarName varNamePsi = children(slidingWindowClausePsi).findFirst(XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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

        XQueryWindowClause windowClausePsi = descendants(file).findFirst(XQueryWindowClause.class);
        XQueryTumblingWindowClause tumblingWindowClausePsi = children(windowClausePsi).findFirst(XQueryTumblingWindowClause.class);
        XQueryVarName varNamePsi = children(tumblingWindowClausePsi).findFirst(XQueryVarName.class);

        XQueryFLWORExpr flworExprPsi = descendants(file).findFirst(XQueryFLWORExpr.class);
        XQueryReturnClause returnClausePsi = children(flworExprPsi).findFirst(XQueryReturnClause.class);
        XQueryOrExpr orExprPsi = children(returnClausePsi).findFirst(XQueryOrExpr.class);
        XQueryVarRef varRefPsi = descendants(orExprPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
        final XQueryFile file = parseResource("tests/resolve/variables/VarDecl_VarRef_NCName.xq");

        XQueryAnnotatedDecl annotatedDeclPsi = descendants(file).findFirst(XQueryAnnotatedDecl.class);
        XQueryVarDecl varDeclPsi = children(annotatedDeclPsi).findFirst(XQueryVarDecl.class);
        XQueryEQName varDeclNamePsi = children(varDeclPsi).findFirst(XQueryEQName.class);

        XQueryMainModule mainModulePsi = descendants(file).findFirst(XQueryMainModule.class);
        XQueryQueryBody queryBodyPsi = children(mainModulePsi).findFirst(XQueryQueryBody.class);
        XQueryVarRef varRefPsi = descendants(queryBodyPsi).findFirst(XQueryVarRef.class);
        XQueryEQName varRefNamePsi = children(varRefPsi).findFirst(XQueryEQName.class);

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
