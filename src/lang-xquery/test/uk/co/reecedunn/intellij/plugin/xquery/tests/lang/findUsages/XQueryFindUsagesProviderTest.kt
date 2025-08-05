// Copyright (C) 2016-2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.findUsages

import com.intellij.lang.HelpID
import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.lang.findUsages.XQueryFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Find Usages - FindUsagesProvider")
class XQueryFindUsagesProviderTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryFindUsagesProviderTest")
    override val language: Language = XQuery

    val provider = XQueryFindUsagesProvider()

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmVariableProvider.register(this, XQueryVariableProvider)
        XpmFunctionProvider.register(this, XQueryFunctionProvider)

        project.registerService(XQueryProjectSettings())
        project.registerService(XpmModuleLoaderSettings(project))

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        fun parse(text: String): Pair<PsiElement, PsiElement> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XpmVariableReference
            val element = call.variableName?.element!!
            val references = element.references
            return when (references.size) {
                1 -> element to references[0].resolve()!! // NCName
                2 -> element to references[1].resolve()!! // QName
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        @Test
        @DisplayName("NCName")
        fun ncname() {
            val ref = parse("declare variable \$ x := 2; \$x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("variable"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val ref = parse("declare variable \$ local:x := 2; \$local:x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("variable"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }

        @Test
        @DisplayName("with type")
        fun withType() {
            val ref = parse("declare variable \$ x as xs:long := 2; \$x")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("variable"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("x"))
            assertThat(provider.getNodeText(ref.second, true), `is`("x"))
            assertThat(provider.getNodeText(ref.second, false), `is`("x"))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
    internal inner class FunctionCall {
        fun parse(text: String): Pair<PsiElement, PsiElement> {
            val module = parseText(text)
            val call = module.walkTree().filterIsInstance<XPathFunctionCall>().first() as XpmFunctionReference
            val element = call.functionName?.element!!
            val references = element.references
            return when (references.size) {
                1 -> element to references[0].resolve()!! // NCName
                2 -> element to references[1].resolve()!! // QName
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        @Test
        @DisplayName("empty parameters ; no return type ; ncname")
        fun emptyParams_noReturnType_ncname() {
            val ref = parse("declare function test((::)) {}; test()")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("function"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("test"))
            assertThat(provider.getNodeText(ref.second, true), `is`("test"))
            assertThat(provider.getNodeText(ref.second, false), `is`("test"))
        }

        @Test
        @DisplayName("empty parameters ; no return type ; qname")
        fun emptyParams_noReturnType_qname() {
            val ref = parse("declare function local:test((::)) {}; local:test()")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("function"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("test"))
            assertThat(provider.getNodeText(ref.second, true), `is`("test"))
            assertThat(provider.getNodeText(ref.second, false), `is`("test"))
        }

        @Test
        @DisplayName("empty parameters ; return type")
        fun emptyParams_returnType() {
            val ref = parse("declare function local:test((::)) as (::) node((::)) {}; local:test()")

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("function"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("test"))
            assertThat(provider.getNodeText(ref.second, true), `is`("test"))
            assertThat(provider.getNodeText(ref.second, false), `is`("test"))
        }

        @Test
        @DisplayName("non-empty parameters ; no return type")
        fun params_noReturnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n  as  xs:float *) {}; local:test(1,2)"
            )

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("function"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("test"))
            assertThat(provider.getNodeText(ref.second, true), `is`("test"))
            assertThat(provider.getNodeText(ref.second, false), `is`("test"))
        }

        @Test
        @DisplayName("non-empty parameters ; return type")
        fun params_returnType() {
            val ref = parse(
                "declare function local:test( \$x as (::) xs:int , \$n as xs:float *) as item((::)) + {}; local:test(1,2)"
            )

            assertThat(provider.canFindUsagesFor(ref.second), `is`(true))
            assertThat(provider.getHelpId(ref.second), `is`(HelpID.FIND_OTHER_USAGES))
            assertThat(provider.getType(ref.second), `is`("function"))
            assertThat(provider.getDescriptiveName(ref.second), `is`("test"))
            assertThat(provider.getNodeText(ref.second, true), `is`("test"))
            assertThat(provider.getNodeText(ref.second, false), `is`("test"))
        }
    }
}
