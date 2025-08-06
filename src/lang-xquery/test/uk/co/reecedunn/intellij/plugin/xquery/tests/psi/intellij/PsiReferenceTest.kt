// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi.intellij

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.module.ModuleTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAtomicOrUnionType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.RelativeModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier", "ReplaceNotNullAssertionWithElvisReturn")
@DisplayName("IntelliJ Program Structure Interface (PSI) - PsiReference - XQuery")
class PsiReferenceTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule>, ModuleTestCase {
    override val pluginId: PluginId = PluginId.getId("PsiReferenceTest")
    override val language: Language = XQuery

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmVariableProvider.register(this, XQueryVariableProvider)

        registerModuleManager()

        project.registerService(XQueryProjectSettings())
        project.registerService(XpmModuleLoaderSettings(project))

        XpmModuleLoaderFactory.register(this, "relative", RelativeModuleLoader)

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)
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
                val uriLiteral = file.walkTree().filterIsInstance<XPathUriLiteral>().first()

                val ref = uriLiteral.reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiteral)))
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
                val uriLiteral = file.walkTree().filterIsInstance<XPathUriLiteral>().last()

                val ref = uriLiteral.reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiteral)))
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
                val uriLiteral = file.walkTree().filterIsInstance<XPathUriLiteral>().last()

                val ref = uriLiteral.reference!!
                assertThat(ref.element, `is`(sameInstance(uriLiteral)))
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
                val uriLiteral = file.walkTree().filterIsInstance<XPathUriLiteral>().last()
                assertThat(uriLiteral.reference, `is`(nullValue()))
            }

            @Test
            @DisplayName("incomplete uri")
            fun incompleteUri() {
                val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_Incomplete.xq")
                val uriLiteral = file.walkTree().filterIsInstance<XPathUriLiteral>().last()
                assertThat(uriLiteral.reference, `is`(nullValue()))
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
            assertThat(resolved, `is`(instanceOf(XQueryModule::class.java)))
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
            assertThat(resolved, `is`(instanceOf(XmlNCNameImpl::class.java)))
            assertThat(resolved.text, `is`("xs"))
            assertThat(resolved.parent.parent, `is`(instanceOf(XQueryNamespaceDecl::class.java)))

            val refs = eqname.references
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(eqname)))
            assertThat(refs[0].canonicalText, `is`("xs"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(2))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XmlNCNameImpl::class.java)))
            assertThat(resolved.text, `is`("xs"))
            assertThat(resolved.parent.parent, `is`(instanceOf(XQueryNamespaceDecl::class.java)))
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
            val file = parse<XQueryModule>(
                """
                declare variable ${'$'}value := 2;
                ${'$'}value
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[0]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDeclaration>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("value"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(5))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("value"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(5))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        fun param() {
            val file = parse<XQueryModule>(
                """
                declare function f(${'$'}x) { ${'$'}x };
                ${'$'}x (: ${'$'}x is not in scope here :)
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[0]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (43) IntermediateClause")
        fun intermediateClause() {
            val file = parse<XQueryModule>(
                """
                for ${'$'}x in ${'$'}y
                for ${'$'}z in ${'$'}x
                return ${'$'}z
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[2]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[1]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("z"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("z"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
        fun forBinding() {
            val file = parse<XQueryModule>(
                """
                for ${'$'}x in ${'$'}y
                return ${'$'}x
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[1]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
        fun positionalVar() {
            val file = parse<XQueryModule>(
                """
                for ${'$'}x at ${'$'}i in ${'$'}y
                return ${'$'}i
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[1]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[1]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("i"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("i"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (49) LetBinding")
        fun letBinding() {
            val file = parse<XQueryModule>(
                """
                let ${'$'}x := ${'$'}y
                return ${'$'}x
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[1]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause")
        fun tumblingWindowClause() {
            val file = parse<XQueryModule>(
                """
                for tumbling window ${'$'}x in ${'$'}y start when true()
                return ${'$'}x
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[1]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
        fun slidingWindowClause() {
            val file = parse<XQueryModule>(
                """
                for sliding window ${'$'}x in ${'$'}y start when true()
                return ${'$'}x
                """
            )[0]

            val varRef = file.walkTree().filterIsInstance<XpmVariableReference>().toList()[1]
            val varDecl = file.walkTree().filterIsInstance<XpmVariableDefinition>().toList()[0]

            val ref = varRef.variableName?.element?.reference!!
            assertThat(ref.element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(ref.canonicalText, `is`("x"))
            assertThat(ref.rangeInElement.startOffset, `is`(0))
            assertThat(ref.rangeInElement.endOffset, `is`(1))
            assertThat(ref.variants.size, `is`(0))

            var resolved: PsiElement = ref.resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))

            val refs = varRef.variableName?.element?.references!!
            assertThat(refs.size, `is`(1))

            assertThat(refs[0].element, `is`(sameInstance(varRef.variableName?.element)))
            assertThat(refs[0].canonicalText, `is`("x"))
            assertThat(refs[0].rangeInElement.startOffset, `is`(0))
            assertThat(refs[0].rangeInElement.endOffset, `is`(1))
            assertThat(refs[0].variants.size, `is`(0))

            resolved = refs[0].resolve()!!
            assertThat(resolved, `is`(instanceOf(XPathNCName::class.java)))
            assertThat(resolved, `is`(varDecl.variableName?.element))
        }
    }
}
