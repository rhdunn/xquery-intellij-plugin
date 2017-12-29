/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryReferenceTest : ParserTestCase() {
    // region Files
    // region URILiteral

    fun testURILiteral_HttpUri() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val httpUriRef = uriLiterals.first().reference
        assertThat(httpUriRef!!.canonicalText, `is`("http://example.com/test"))
        assertThat(httpUriRef.variants.size, `is`(0))

        val resolved = httpUriRef.resolve()
        assertThat<PsiElement>(resolved, `is`(nullValue()))
    }

    fun testURILiteral_SameDirectory() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_SameDirectory.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val ref = uriLiterals.last().reference
        assertThat(ref!!.canonicalText, `is`("test.xq"))
        assertThat(ref.variants.size, `is`(0))

        val resolved = ref.resolve()
        assertThat<PsiElement>(resolved, `is`(notNullValue()))
        assertThat<PsiElement>(resolved, instanceOf<PsiElement>(XQueryModule::class.java))
        assertThat(resolved!!.containingFile.name, `is`("test.xq"))
    }

    fun testURILiteral_ParentDirectory() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ParentDirectory.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val ref = uriLiterals.last().reference
        assertThat(ref!!.canonicalText, `is`("namespaces/ModuleDecl.xq"))
        assertThat(ref.variants.size, `is`(0))

        val resolved = ref.resolve()
        assertThat<PsiElement>(resolved, `is`(notNullValue()))
        assertThat<PsiElement>(resolved, instanceOf<PsiElement>(XQueryModule::class.java))
        assertThat(resolved!!.containingFile.name, `is`("ModuleDecl.xq"))
    }

    fun testURILiteral_BuiltinResource() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFile.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val ref = uriLiterals.last().reference
        assertThat(ref!!.canonicalText, `is`("res://www.w3.org/2005/xpath-functions/array.xqy"))
        assertThat(ref.variants.size, `is`(0))

        val resolved = ref.resolve()
        assertThat<PsiElement>(resolved, `is`(notNullValue()))
        assertThat<PsiElement>(resolved, instanceOf<PsiElement>(XQueryModule::class.java))
        assertThat(resolved!!.containingFile.name, `is`("array.xqy"))
    }

    fun testURILiteral_BuiltinResource_NotFound() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_ResourceFileNotFound.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val ref = uriLiterals.last().reference
        assertThat(ref!!.canonicalText, `is`("res://this-resource-does-not-exist.xqy"))
        assertThat(ref.variants.size, `is`(0))

        val resolved = ref.resolve()
        assertThat<PsiElement>(resolved, `is`(nullValue()))
    }

    fun testURILiteral_Empty() {
        val file = parseResource("tests/resolve/files/ModuleImport_URILiteral_Empty.xq")!!

        val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
        assertThat(moduleImportPsi, `is`(notNullValue()))

        val uriLiterals = moduleImportPsi.children().filterIsInstance<XQueryUriLiteral>().toList()
        assertThat(uriLiterals.count(), `is`(2))

        val ref = uriLiterals.last().reference
        assertThat(ref!!.canonicalText, `is`(""))
        assertThat(ref.variants.size, `is`(0))

        val resolved = ref.resolve()
        assertThat<PsiElement>(resolved, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region Namespaces
    // region QName

    fun testQName() {
        val file = parseResource("tests/resolve/namespaces/ModuleDecl.xq")!!

        val modulePsi = file.descendants().filterIsInstance<XQueryLibraryModule>().first()
        val prologPsi = modulePsi.children().filterIsInstance<XQueryProlog>().first()
        val annotatedDeclPsi = prologPsi.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        assertThat(functionDeclPsi, `is`(notNullValue()))

        val qname = functionDeclPsi.children().filterIsInstance<XPathQName>().first()
        assertThat(qname, `is`(notNullValue()))

        val ref: PsiReference = qname.reference!!
        assertThat(ref.canonicalText, `is`("test"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(LeafPsiElement::class.java)))
        assertThat(resolved.text, `is`("test"))
        assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryModuleDecl::class.java)))

        val refs = qname.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("test"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(LeafPsiElement::class.java)))
        assertThat(resolved.text, `is`("test"))
        assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryModuleDecl::class.java)))
    }

    // endregion
    // region EQName

    fun testEQName_NCName() {
        val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithNCNameReturnType.xq")!!

        val modulePsi = file.descendants().filterIsInstance<XQueryLibraryModule>().first()
        val prologPsi = modulePsi.children().filterIsInstance<XQueryProlog>().first()
        val annotatedDeclPsi = prologPsi.children().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val sequenceTypePsi = functionDeclPsi.children().filterIsInstance<XQuerySequenceType>().first()
        assertThat(sequenceTypePsi, `is`(notNullValue()))

        val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()
        assertThat(eqname, `is`(notNullValue()))

        val ref = eqname.reference
        assertThat(ref, `is`(nullValue()))

        val refs = eqname.references
        assertThat(refs.size, `is`(0))
    }

    fun testEQName_QName() {
        val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithQNameReturnType.xq")!!

        val modulePsi = file.descendants().filterIsInstance<XQueryLibraryModule>().first()
        val prologPsi = modulePsi.children().filterIsInstance<XQueryProlog>().first()
        val annotatedDeclPsi = prologPsi.children().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val sequenceTypePsi = functionDeclPsi.children().filterIsInstance<XQuerySequenceType>().first()
        assertThat(sequenceTypePsi, `is`(notNullValue()))

        val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()
        assertThat(eqname, `is`(notNullValue()))

        val ref = eqname.reference!!
        assertThat(ref.canonicalText, `is`("xs"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(LeafPsiElement::class.java)))
        assertThat(resolved.text, `is`("xs"))
        assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))

        val refs = eqname.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("xs"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(LeafPsiElement::class.java)))
        assertThat(resolved.text, `is`("xs"))
        assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))
    }

    fun testEQName_URIQualifiedName() {
        val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithURIQualifiedNameReturnType.xq")!!

        val modulePsi = file.descendants().filterIsInstance<XQueryLibraryModule>().first()
        val prologPsi = modulePsi.children().filterIsInstance<XQueryProlog>().first()
        val annotatedDeclPsi = prologPsi.children().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val sequenceTypePsi = functionDeclPsi.children().filterIsInstance<XQuerySequenceType>().first()
        assertThat(sequenceTypePsi, `is`(notNullValue()))

        val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()
        assertThat(eqname, `is`(notNullValue()))

        val ref = eqname.reference
        assertThat(ref, `is`(nullValue()))

        val refs = eqname.references
        assertThat(refs.size, `is`(0))
    }

    // endregion
    // endregion
    // region Variables
    // region ForBinding

    fun testForBinding() {
        val file = parseResource("tests/parser/xquery-1.0/ForClause.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference
        assertThat(ref!!.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region IntermediateClause

    fun testIntermediateClause() {
        val file = parseResource("tests/resolve/variables/IntermediateClause_ReturnInnerForVariable.xq")!!

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val intermediateClausePsi = flworExprPsi.children().filterIsInstance<XQueryIntermediateClause>().first()
        val forClausePsi = intermediateClausePsi.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val varNamePsi = forBindingPsi.children().filterIsInstance<XPathVarName>().first()

        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("z"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("z"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region LetBinding

    fun testLetBinding() {
        val file = parseResource("tests/parser/xquery-1.0/LetClause.xq")!!

        val letClausePsi = file.descendants().filterIsInstance<XQueryLetClause>().first()
        val letBindingPsi = letClausePsi.children().filterIsInstance<XQueryLetBinding>().first()
        val varNamePsi = letBindingPsi.children().filterIsInstance<XPathVarName>().first()

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region Param

    fun testParam() {
        val file = parseResource("tests/resolve/variables/FunctionDecl_ReturningSpecifiedParam.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val paramListPsi = functionDeclPsi.children().filterIsInstance<XQueryParamList>().first()
        val paramPsi = paramListPsi.children().filterIsInstance<XQueryParam>().first()
        val paramNamePsi = paramPsi.children().filterIsInstance<XPathEQName>().first()

        val functionBodyPsi = functionDeclPsi.children().filterIsInstance<XPathFunctionBody>().first()
        val exprPsi = functionBodyPsi.children().filterIsInstance<XPathExpr>().first()
        val varRefPsi = exprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
        assertThat(resolved, `is`<PsiElement>(paramNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
        assertThat(resolved, `is`<PsiElement>(paramNamePsi))
    }

    fun testParam_ReferencedFromOutsideTheFunction() {
        val file = parseResource("tests/resolve/variables/FunctionDecl_ReturningSpecifiedParam.xq")!!

        val mainModulePsi = file.descendants().filterIsInstance<XQueryMainModule>().first()
        val queryBodyPsi = mainModulePsi.children().filterIsInstance<XQueryQueryBody>().first()
        val varRefPsi = queryBodyPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement? = ref.resolve()
        assertThat(resolved, `is`(nullValue()))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()
        assertThat(resolved, `is`(nullValue()))
    }

    // endregion
    // region PositionalVar

    fun testPositionalVar() {
        val file = parseResource("tests/resolve/variables/PositionalVar_ReturnThePosition.xq")!!

        val forClausePsi = file.descendants().filterIsInstance<XQueryForClause>().first()
        val forBindingPsi = forClausePsi.children().filterIsInstance<XQueryForBinding>().first()
        val positionalVarPsi = forBindingPsi.children().filterIsInstance<XQueryPositionalVar>().first()
        val varNamePsi = positionalVarPsi.children().filterIsInstance<XPathVarName>().first()

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("i"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("i"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region SlidingWindowClause

    fun testSlidingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val slidingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQuerySlidingWindowClause>().first()
        val varNamePsi = slidingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region TumblingWindowClause

    fun testTumblingWindowClause() {
        val file = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")!!

        val windowClausePsi = file.descendants().filterIsInstance<XQueryWindowClause>().first()
        val tumblingWindowClausePsi = windowClausePsi.children().filterIsInstance<XQueryTumblingWindowClause>().first()
        val varNamePsi = tumblingWindowClausePsi.children().filterIsInstance<XPathVarName>().first()

        val flworExprPsi = file.descendants().filterIsInstance<XQueryFLWORExpr>().first()
        val returnClausePsi = flworExprPsi.children().filterIsInstance<XQueryReturnClause>().first()
        val orExprPsi = returnClausePsi.children().filterIsInstance<XQueryOrExpr>().first()
        val varRefPsi = orExprPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("x"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("x"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varNamePsi))
    }

    // endregion
    // region VarDecl

    fun testVarDecl() {
        val file = parseResource("tests/resolve/variables/VarDecl_VarRef_NCName.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val varDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryVarDecl>().first()
        val varDeclNamePsi = varDeclPsi.children().filterIsInstance<XPathEQName>().first()

        val mainModulePsi = file.descendants().filterIsInstance<XQueryMainModule>().first()
        val queryBodyPsi = mainModulePsi.children().filterIsInstance<XQueryQueryBody>().first()
        val varRefPsi = queryBodyPsi.descendants().filterIsInstance<XPathVarRef>().first()
        val varRefNamePsi = varRefPsi.children().filterIsInstance<XPathEQName>().first()

        val ref = varRefNamePsi.reference!!
        assertThat(ref.canonicalText, `is`("value"))
        assertThat(ref.variants.size, `is`(0))

        var resolved: PsiElement = ref.resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varDeclNamePsi))

        val refs = varRefNamePsi.references
        assertThat(refs.size, `is`(1))

        assertThat(refs[0].canonicalText, `is`("value"))
        assertThat(refs[0].variants.size, `is`(0))

        resolved = refs[0].resolve()!!
        assertThat(resolved, `is`(instanceOf<PsiElement>(XPathVarName::class.java)))
        assertThat(resolved, `is`<PsiElement>(varDeclNamePsi))
    }

    // endregion
    // endregion
}
