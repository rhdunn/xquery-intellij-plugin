/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - References and Resolve - XPath")
private class XPathReferenceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(XPathReferenceTest::class.java, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("Namespaces")
    internal inner class Namespaces {
        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun testEQName_NCName() {
            val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithNCNameReturnType.xq")

            val sequenceTypePsi = file.walkTree().filterIsInstance<XPathAtomicOrUnionType>().first()
            val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()

            val ref = eqname.reference
            assertThat(ref, `is`(nullValue()))

            val refs = eqname.references
            assertThat(refs.size, `is`(0))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun testEQName_QName() {
            val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithQNameReturnType.xq")

            val sequenceTypePsi = file.walkTree().filterIsInstance<XPathAtomicOrUnionType>().first()
            val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()

            val ref = eqname.reference!!
            assertThat(ref.canonicalText, `is`("xs"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(2))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XmlNCNameImpl::class.java)))
            assertThat(resolved.text, `is`("xs"))
            assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))

            val refs = eqname.references
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].canonicalText, `is`("xs"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(2))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XmlNCNameImpl::class.java)))
            assertThat(resolved.text, `is`("xs"))
            assertThat(resolved.parent.parent, `is`(instanceOf<PsiElement>(XQueryNamespaceDecl::class.java)))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
        fun testEQName_URIQualifiedName() {
            val file = parseResource("tests/resolve/namespaces/FunctionDecl_WithURIQualifiedNameReturnType.xq")

            val sequenceTypePsi = file.walkTree().filterIsInstance<XPathAtomicOrUnionType>().first()
            val eqname = sequenceTypePsi.descendants().filterIsInstance<XPathEQName>().first()

            val ref = eqname.reference
            assertThat(ref, `is`(nullValue()))

            val refs = eqname.references
            assertThat(refs.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("Variables")
    internal inner class Variables {
        @Test
        @DisplayName("XPath 3.1 EBNF (3) Param")
        fun testParam() {
            val file = parseResource("tests/resolve/variables/FunctionDecl_ReturningSpecifiedParam.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
            val paramListPsi = functionDeclPsi.children().filterIsInstance<XPathParamList>().first()
            val paramPsi = paramListPsi.children().filterIsInstance<XPathParam>().first()
            val paramNamePsi = paramPsi.children().filterIsInstance<XPathEQName>().first()

            val functionBodyPsi = functionDeclPsi.children().filterIsInstance<XPathFunctionBody>().first()
            val varRefNamePsi = functionBodyPsi.walkTree().filterIsInstance<XPathEQName>().first()

            val ref = varRefNamePsi.reference!!
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(paramNamePsi))

            val refs = varRefNamePsi.references
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(paramNamePsi))
        }
    }
}
