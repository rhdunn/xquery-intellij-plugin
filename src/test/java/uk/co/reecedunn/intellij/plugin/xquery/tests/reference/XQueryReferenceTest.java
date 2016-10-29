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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*;
import uk.co.reecedunn.intellij.plugin.xquery.psi.PsiNavigation;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class XQueryReferenceTest extends ParserTestCase {
    // region Files
    // region URILiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-ModuleImport")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-URILiteral")
    public void testURILiteral_HttpUri() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/ModuleImport_URILiteral_SameDirectory.xq");

        XQueryModuleImport moduleImportPsi = PsiNavigation.findDirectDescendantByClass(node.getPsi(), XQueryModuleImport.class);
        assertThat(moduleImportPsi, is(notNullValue()));

        List<XQueryUriLiteral> uriLiterals = PsiNavigation.findChildrenByClass(moduleImportPsi, XQueryUriLiteral.class);
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

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    public void testQName() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/ModuleDecl.xq");

        XQueryLibraryModule modulePsi = PsiNavigation.findDirectDescendantByClass(node.getPsi(), XQueryLibraryModule.class);
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findDirectDescendantByClass(prologPsi, XQueryAnnotatedDecl.class);
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

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-EQName")
    public void testEQName_NCName() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithNCNameReturnType.xq");

        XQueryLibraryModule modulePsi = PsiNavigation.findDirectDescendantByClass(node.getPsi(), XQueryLibraryModule.class);
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryEQName.class);
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref, is(nullValue()));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(0));
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-EQName")
    public void testEQName_QName() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithQNameReturnType.xq");

        XQueryLibraryModule modulePsi = PsiNavigation.findDirectDescendantByClass(node.getPsi(), XQueryLibraryModule.class);
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryEQName.class);
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

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-EQName")
    public void testEQName_URIQualifiedName() {
        final ASTNode node = parseResource("tests/resolve/xquery-1.0/FunctionDecl_WithURIQualifiedNameReturnType.xq");

        XQueryLibraryModule modulePsi = PsiNavigation.findDirectDescendantByClass(node.getPsi(), XQueryLibraryModule.class);
        XQueryProlog prologPsi = PsiNavigation.findChildByClass(modulePsi, XQueryProlog.class);
        XQueryAnnotatedDecl annotatedDeclPsi = PsiNavigation.findChildByClass(prologPsi, XQueryAnnotatedDecl.class);
        XQueryFunctionDecl functionDeclPsi = PsiNavigation.findChildByClass(annotatedDeclPsi, XQueryFunctionDecl.class);
        XQuerySequenceType sequenceTypePsi = PsiNavigation.findChildByClass(functionDeclPsi, XQuerySequenceType.class);
        assertThat(sequenceTypePsi, is(notNullValue()));

        XQueryEQName eqname = PsiNavigation.findDirectDescendantByClass(sequenceTypePsi, XQueryEQName.class);
        assertThat(eqname, is(notNullValue()));

        PsiReference ref = eqname.getReference();
        assertThat(ref, is(nullValue()));

        PsiReference[] refs = eqname.getReferences();
        assertThat(refs.length, is(0));
    }

    // endregion
    // endregion
}
