/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariable
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - References and Resolve - XQuery")
private class XQueryReferenceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XQueryModule
    }

    @Nested
    @DisplayName("Files")
    internal inner class Files {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (217) URILiteral")
        internal inner class URILiteral {
            @Test
            @DisplayName("http uri")
            fun httpUri() {
                val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_SameDirectory.xq")

                val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
                assertThat(moduleImportPsi, `is`(notNullValue()))

                val uriLiterals = moduleImportPsi.walkTree().filterIsInstance<XPathUriLiteral>().toList()
                assertThat(uriLiterals.size, `is`(2))

                val ref = uriLiterals.first().reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiterals.first())))
                assertThat(ref.canonicalText, `is`("http://example.com/test"))
                assertThat(ref.rangeInElement.startOffset, `is`(1))
                assertThat(ref.rangeInElement.endOffset, `is`(24))
                assertThat(ref.variants.size, `is`(0))

                val resolved = ref.resolve()
                assertThat(resolved, `is`(nullValue()))
            }

            @Test
            @DisplayName("file uri; same directory")
            fun sameDirectory() {
                val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_SameDirectory.xq")

                val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
                assertThat(moduleImportPsi, `is`(notNullValue()))

                val uriLiterals = moduleImportPsi.walkTree().filterIsInstance<XPathUriLiteral>().toList()
                assertThat(uriLiterals.size, `is`(2))

                val ref = uriLiterals.last().reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiterals.last())))
                assertThat(ref.canonicalText, `is`("test.xq"))
                assertThat(ref.rangeInElement.startOffset, `is`(1))
                assertThat(ref.rangeInElement.endOffset, `is`(8))
                assertThat(ref.variants.size, `is`(0))

                val resolved = ref.resolve()
                assertThat(resolved, `is`(notNullValue()))
                assertThat(resolved, instanceOf<PsiElement>(XQueryModule::class.java))
                assertThat(resolved!!.containingFile.name, `is`("test.xq"))
            }

            @Test
            @DisplayName("file uri; parent directory")
            fun parentDirectory() {
                val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_ParentDirectory.xq")

                val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
                assertThat(moduleImportPsi, `is`(notNullValue()))

                val uriLiterals = moduleImportPsi.walkTree().filterIsInstance<XPathUriLiteral>().toList()
                assertThat(uriLiterals.size, `is`(2))

                val ref = uriLiterals.last().reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiterals.last())))
                assertThat(ref.canonicalText, `is`("namespaces/ModuleDecl.xq"))
                assertThat(ref.rangeInElement.startOffset, `is`(1))
                assertThat(ref.rangeInElement.endOffset, `is`(25))
                assertThat(ref.variants.size, `is`(0))

                val resolved = ref.resolve()
                assertThat(resolved, `is`(nullValue()))
            }

            @Test
            @DisplayName("empty uri")
            fun emptyUri() {
                val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_Empty.xq")

                val moduleImportPsi = file.descendants().filterIsInstance<XQueryModuleImport>().first()
                assertThat(moduleImportPsi, `is`(notNullValue()))

                val uriLiterals = moduleImportPsi.walkTree().filterIsInstance<XPathUriLiteral>().toList()
                assertThat(uriLiterals.size, `is`(2))

                val ref = uriLiterals.last().reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiterals.last())))
                assertThat(ref.canonicalText, `is`(""))
                assertThat(ref.rangeInElement.startOffset, `is`(1))
                assertThat(ref.rangeInElement.endOffset, `is`(1))
                assertThat(ref.variants.size, `is`(0))

                val resolved = ref.resolve()
                assertThat(resolved, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("Namespaces")
    internal inner class Namespaces {
        @Test
        @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
        fun uriQualifiedName() {
            val eqname = parse<XPathAtomicOrUnionType>(
                """
                module  namespace fn = "http://www.w3.org/2005/xpath-functions";
                declare function fn:true() as Q{http://www.w3.org/2001/XMLSchema}boolean { "true" };
                """
            )[0].firstChild

            val ref = eqname.reference
            assertThat(ref, `is`(nullValue()))

            val refs = eqname.references
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(eqname)))
            assertThat(refs[0].canonicalText, `is`("http://www.w3.org/2001/XMLSchema"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(2))
            assertThat(refs[0].rangeInElement.endOffset, `is`(34))
            assertThat(refs[0].variants.size, `is`(0))

            val resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XQueryModule::class.java)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (234) QName")
        fun qname() {
            val eqname = parse<XPathAtomicOrUnionType>(
                """
                module  namespace fn = "http://www.w3.org/2005/xpath-functions";
                declare namespace xs = "http://www.w3.org/2001/XMLSchema";
                declare function fn:true() as xs:boolean { "true" };
                """
            )[0].firstChild

            val ref = eqname.reference!!
            assertThat(ref.element, `is`(sameInstance(eqname)))
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

            assertThat(refs[0].element, `is`(sameInstance(eqname)))
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
        @DisplayName("XQuery 3.1 EBNF (235) NCName")
        fun ncname() {
            val eqname = parse<XPathAtomicOrUnionType>(
                """
                module  namespace fn = "http://www.w3.org/2005/xpath-functions";
                declare function fn:true() as boolean { "true" };
                """
            )[0].firstChild

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
        @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
        fun varDecl() {
            val vars = parse<XpmVariable>(
                """
                declare variable ${'$'}value := 2;
                ${'$'}value
                """
            )

            val varRef = vars[1]
            val varDecl = vars[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("value"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(5))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("value"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(5))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        fun param() {
            val vars = parse<XpmVariable>(
                """
                declare function f(${'$'}x) { ${'$'}x };
                ${'$'}x (: ${'$'}x is not in scope here :)
                """
            )

            val ref = vars[1].variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(vars[1].variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(vars[0].variableName?.element))

            val refs = vars[1].variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(vars[1].variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(vars[0].variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (43) IntermediateClause")
        fun intermediateClause() {
            val vars = parse<XpmVariable>(
                """
                for ${'$'}x in ${'$'}y
                for ${'$'}z in ${'$'}x
                return ${'$'}z
                """
            )

            val varRef = vars[4]
            val varDecl = vars[2]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("z"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("z"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
        fun forBinding() {
            val vars = parse<XpmVariable>(
                """
                for ${'$'}x in ${'$'}y
                return ${'$'}x
                """
            )

            val varRef = vars[2]
            val varDecl = vars[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
        fun positionalVar() {
            val vars = parse<XpmVariable>(
                """
                for ${'$'}x at ${'$'}i in ${'$'}y
                return ${'$'}i
                """
            )

            val varRef = vars[3]
            val varDecl = vars[1]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("i"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("i"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (49) LetBinding")
        fun letBinding() {
            val vars = parse<XpmVariable>(
                """
                let ${'$'}x := ${'$'}y
                return ${'$'}x
                """
            )

            val varRef = vars[2]
            val varDecl = vars[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause")
        fun tumblingWindowClause() {
            val vars = parse<XpmVariable>(
                """
                for tumbling window ${'$'}x in ${'$'}y start when true()
                return ${'$'}x
                """
            )

            val varRef = vars[2]
            val varDecl = vars[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
        fun slidingWindowClause() {
            val vars = parse<XpmVariable>(
                """
                for sliding window ${'$'}x in ${'$'}y start when true()
                return ${'$'}x
                """
            )

            val varRef = vars[2]
            val varDecl = vars[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf<PsiElement>(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }
    }
}
