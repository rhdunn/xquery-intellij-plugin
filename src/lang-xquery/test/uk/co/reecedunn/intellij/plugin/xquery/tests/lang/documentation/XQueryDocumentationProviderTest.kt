// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.documentation

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSourceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.documentation.XQueryDocumentationProvider
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Documentation Provider - XQuery")
class XQueryDocumentationProviderTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryDocumentationProviderTest")
    override val language: Language = XQuery

    inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery).walkTree().filterIsInstance<T>().toList()
    }

    companion object {
        fun body(substring: String): Matcher<in String?> = StringContains.containsString("<body>$substring</body>")
    }

    private val documentationProvider = XQueryDocumentationProvider()

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XQDocDocumentationSourceProvider.register(this, DocumentationSourceProvider)

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmVariableProvider.register(this, XQueryVariableProvider)
        XpmFunctionProvider.register(this, XQueryFunctionProvider)

        project.registerService(XQueryProjectSettings())
        project.registerService(XpmModuleLoaderSettings(project))

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (4) LibraryModule")
    internal inner class LibraryModule {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val element = module.walkTree().filterIsInstance<XPathUriLiteral>().first()
            val ref = element.references[0].resolve()
            return element to ref
        }

        @Test
        @DisplayName("builtin")
        fun builtin() {
            val ref = parse("declare namespace fn = \"http://www.w3.org/2005/xpath-functions\";")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, startsWith("module namespace fn = \"http://www.w3.org/2005/xpath-functions\"\nat \""))
            assertThat(quickDoc, endsWith("/org/w3/www/2005/xpath-functions.xqy\""))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
    internal inner class ModuleImport {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XpmFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[0].resolve()
            return element to ref
        }

        @Test
        @DisplayName("custom")
        fun custom() {
            val ref = parse(
                """
                import module namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace ex = \"http://www.example.com\""))
        }

        @Test
        @DisplayName("custom without namespace uri")
        fun customWithoutUri() {
            val ref = parse(
                """
                import module namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor")
        fun quickDoc() {
            val target = parse<XsQNameValue>("import module namespace ex = \"http://www.example.com\";")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=ex namespace=http://www.example.com]</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=ex namespace=http://www.example.com]</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("module href=[prefix=ex namespace=http://www.example.com]"))
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
    internal inner class NamespaceDecl {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XpmFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[0].resolve()
            return element to ref
        }

        @Test
        @DisplayName("builtin")
        fun builtin() {
            val ref = parse("fn:true()")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace fn = \"http://www.w3.org/2005/xpath-functions\""))
        }

        @Test
        @DisplayName("custom")
        fun custom() {
            val ref = parse(
                """
                declare namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("namespace ex = \"http://www.example.com\""))
        }

        @Test
        @DisplayName("custom without namespace uri")
        fun customWithoutUri() {
            val ref = parse(
                """
                declare namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }

        @Test
        @DisplayName("module")
        fun module() {
            val ref = parse(
                """
                module namespace ex = "http://www.example.com";
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("module namespace ex = \"http://www.example.com\"\nat \"/testcase.xqy\""))
        }

        @Test
        @DisplayName("module without namespace uri")
        fun moduleWithoutUri() {
            val ref = parse(
                """
                module namespace ex;
                declare function ex:test((::)) {};
                ex:test()
                """
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`(nullValue()))
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor")
        fun quickDoc() {
            val target = parse<XsQNameValue>("declare namespace ex = \"http://www.example.com\";")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=ex namespace=http://www.example.com]</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=ex namespace=http://www.example.com]</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("module href=[prefix=ex namespace=http://www.example.com]"))
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : NCName")
        fun ncname() {
            val target = parse<XsQNameValue>("declare function true() {}; 2")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0"))
            )
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : QName")
        fun qname() {
            val target = parse<XsQNameValue>("declare function fn:true() {}; 2")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=true]#0"))
            )
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : URIQualifiedName")
        fun uriQualifiedName() {
            val target = parse<XsQNameValue>(
                "declare function Q{http://www.w3.org/2005/xpath-functions}true() {}; 2"
            )[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=true]#0"))
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XpmVariableReference
            val element = call.variableName?.element!!
            val ref = element.references[1].resolve()
            return element to ref
        }

        @Test
        @DisplayName("no type")
        fun noType() {
            val ref = parse("declare variable \$ local:test := 2; \$local:test")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare variable \$local:test"))
        }

        @Test
        @DisplayName("type")
        fun type() {
            val ref = parse("declare variable \$ local:test  as  xs:float := 2; \$local:test")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare variable \$local:test as xs:float"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
    internal inner class FunctionCall {
        fun parse(text: String): Pair<PsiElement?, PsiElement?> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XpmFunctionReference
            val element = call.functionName?.element!!
            val ref = element.references[1].resolve()
            return element to ref
        }

        @Test
        @DisplayName("empty parameters ; no return type")
        fun emptyParams_noReturnType() {
            val ref = parse("declare function local:test((::)) {}; local:test()")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test()"))
        }

        @Test
        @DisplayName("empty parameters ; return type")
        fun emptyParams_returnType() {
            val ref = parse("declare function local:test((::)) as (::) node((::)) {}; local:test()")

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test() as node()"))
        }

        @Test
        @DisplayName("non-empty parameters ; no return type")
        fun params_noReturnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n  as  xs:float *) {}; local:test(1,2)"
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*)"))
        }

        @Test
        @DisplayName("non-empty parameters ; return type")
        fun params_returnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n as xs:float *) as item((::)) + {}; local:test(1,2)"
            )

            val quickDoc = documentationProvider.getQuickNavigateInfo(ref.second, ref.first)
            assertThat(quickDoc, `is`("declare function local:test(\$x as xs:int, \$n as xs:float*) as item()+"))
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : NCName")
        fun ncname() {
            val target = parse<XsQNameValue>("concat(1,2)")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2"))
            )
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : QName")
        fun qname() {
            val target = parse<XsQNameValue>("fn:concat(1,2)")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2"))
            )
        }

        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : URIQualifiedName")
        fun uriQualifiedName() {
            val target = parse<XsQNameValue>("Q{http://www.w3.org/2005/xpath-functions}concat(1,2)")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.localName?.element),
                body("<dl><dt>Summary</dt><dd>function summary=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.localName?.element),
                `is`(listOf("function href=[prefix=(null) namespace=http://www.w3.org/2005/xpath-functions localname=concat]#2"))
            )
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (234) QName")
    internal inner class QName {
        @Test
        @DisplayName("generateDoc, generateHoverDoc, getUrlFor : prefix")
        fun prefix() {
            val target = parse<XsQNameValue>("fn:concat(1,2)")[0]

            // Only the original element is used, but element is non-null for generateHoverDoc.
            val element = target.element?.containingFile as PsiElement

            assertThat(
                documentationProvider.generateDoc(element, target.prefix?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions]</dd></dl>")
            )

            assertThat(
                documentationProvider.generateHoverDoc(element, target.prefix?.element),
                body("<dl><dt>Summary</dt><dd>module summary=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions]</dd></dl>")
            )

            assertThat(
                documentationProvider.getUrlFor(element, target.prefix?.element),
                `is`(listOf("module href=[prefix=fn namespace=http://www.w3.org/2005/xpath-functions]"))
            )
        }
    }
}
