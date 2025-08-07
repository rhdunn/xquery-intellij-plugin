// Copyright (C) 2017-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.optree

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.module.ModuleTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.defaultNamespace
import uk.co.reecedunn.intellij.plugin.xpm.inScopeVariables
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.JspModuleSourceRootLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Static Context")
class XQueryStaticContextTest : IdeaPlatformTestCase(), LanguageTestCase, ModuleTestCase {
    override val pluginId: PluginId = PluginId.getId("XQueryStaticContextTest")
    override val language: Language = XQuery

    private val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(project)

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmVariableProvider.register(this, XQueryVariableProvider)
        XpmFunctionProvider.register(this, XQueryFunctionProvider)

        registerModuleManager(ResourceVirtualFile.create(this::class.java.classLoader, "tests/module-xquery"))

        project.registerService(XQueryProjectSettings())
        project.registerService(XpmModuleLoaderSettings(project))

        XpmModuleLoaderFactory.register(this, "module", JspModuleSourceRootLoader, "")

        XpmModulePathFactory.register(this, XpmModuleLocationPath, "")
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions)
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.basex.model.BuiltInFunctions)
        ImportPathResolver.register(this, uk.co.reecedunn.intellij.plugin.marklogic.model.BuiltInFunctions)
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Statically known namespaces")
    internal inner class StaticallyKnownNamespaces {
        private fun namespace(namespaces: List<XpmNamespaceDeclaration>, prefix: String): String {
            return namespaces.asIterable().first { ns -> ns.namespacePrefix!!.data == prefix }.namespaceUri!!.data
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("module declaration")
            fun testStaticallyKnownNamespaces_ModuleDecl() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "module namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "module namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_SchemaImport_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import schema namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_SchemaImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>(
                    "import schema namespace a='http://www.example.com'; a:test();"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import schema namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import schema namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_ModuleImport_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import module namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_ModuleImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>(
                    "import module namespace a='http://www.example.com'; a:test();"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import module namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        internal inner class NamespaceDecl {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_NamespaceDecl_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "declare namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_NamespaceDecl_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("declare namespace a='http://www.example.com'; a:test();")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "declare namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("declare namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
        internal inner class WithExpr {
            @Test
            @DisplayName("namespace prefix atribute")
            fun xmlns() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("with xmlns:b=\"http://www.example.com\" {b:test()}")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("b"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("non-namespace prefix")
            fun nonNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("with b=\"http://www.example.com\" {b:test()}")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("namespace prefix atribute")
            fun testStaticallyKnownNamespaces_DirAttribute_Xmlns() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("b"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("namespace prefix, missing namespace uri")
            fun testStaticallyKnownNamespaces_DirAttribute_Xmlns_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a xmlns:b=>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("non-namespace prefix atribute")
            fun testStaticallyKnownNamespaces_DirAttribute() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a b='http://www.example.com'>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("predefined namespaces")
        internal inner class PredefinedNamespaces {
            private fun namespace(namespaces: List<XpmNamespaceDeclaration>, prefix: String): String {
                return namespaces.asIterable().first { ns -> ns.namespacePrefix!!.data == prefix }.namespaceUri!!.data
            }

            @Test
            @DisplayName("XQuery 1.0")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_XQuery10() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()

                assertThat(namespaces.size, `is`(8))
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("MarkLogic")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_MarkLogic() {
                settings.implementationVersion = "marklogic/v6"
                settings.XQueryVersion = "1.0-ml"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()

                assertThat(namespaces.size, `is`(31)) // Includes built-in namespaces for all MarkLogic versions.
                assertThat(namespace(namespaces, "cntk"), `is`("http://marklogic.com/cntk"))
                assertThat(namespace(namespaces, "cts"), `is`("http://marklogic.com/cts"))
                assertThat(namespace(namespaces, "dav"), `is`("DAV:"))
                assertThat(namespace(namespaces, "dbg"), `is`("http://marklogic.com/xdmp/dbg"))
                assertThat(namespace(namespaces, "dir"), `is`("http://marklogic.com/xdmp/directory"))
                assertThat(namespace(namespaces, "err"), `is`("http://www.w3.org/2005/xqt-errors"))
                assertThat(namespace(namespaces, "error"), `is`("http://marklogic.com/xdmp/error"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "geo"), `is`("http://marklogic.com/geospatial"))
                assertThat(namespace(namespaces, "json"), `is`("http://marklogic.com/xdmp/json"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "lock"), `is`("http://marklogic.com/xdmp/lock"))
                assertThat(namespace(namespaces, "map"), `is`("http://marklogic.com/xdmp/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://marklogic.com/xdmp/math"))
                assertThat(namespace(namespaces, "ort"), `is`("http://marklogic.com/onnxruntime"))
                assertThat(namespace(namespaces, "prof"), `is`("http://marklogic.com/xdmp/profile"))
                assertThat(namespace(namespaces, "prop"), `is`("http://marklogic.com/xdmp/property"))
                assertThat(namespace(namespaces, "rdf"), `is`("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
                assertThat(namespace(namespaces, "sc"), `is`("http://marklogic.com/xdmp/schema-components"))
                assertThat(namespace(namespaces, "sec"), `is`("http://marklogic.com/security"))
                assertThat(namespace(namespaces, "sem"), `is`("http://marklogic.com/xdmp/semantics"))
                assertThat(namespace(namespaces, "spell"), `is`("http://marklogic.com/xdmp/spell"))
                assertThat(namespace(namespaces, "sql"), `is`("http://marklogic.com/xdmp/sql"))
                assertThat(namespace(namespaces, "tde"), `is`("http://marklogic.com/xdmp/tde"))
                assertThat(namespace(namespaces, "temporal"), `is`("http://marklogic.com/xdmp/temporal"))
                assertThat(namespace(namespaces, "xdmp"), `is`("http://marklogic.com/xdmp"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xqe"), `is`("http://marklogic.com/xqe"))
                assertThat(namespace(namespaces, "xqterr"), `is`("http://www.w3.org/2005/xqt-errors"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default element/type namespace ; XQuery 4.0 (2.1.1) Default element namespace")
    internal inner class DefaultElementNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>(
                    "import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
        internal inner class WithExpr {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("with xmlns:b=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.example.com"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.example.com"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultElement).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default element/type namespace ; XQuery 4.0 (2.1.1) Default type namespace")
    internal inner class DefaultTypeNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>(
                    "import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
        internal inner class WithExpr {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("with xmlns:b=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.example.com"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultType).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`(""))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default function namespace")
    internal inner class DefaultFunctionNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>(
                    "import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()"
                )[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions/math"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))

                // predefined static context
                assertThat(element[1].namespacePrefix?.data, `is`(""))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[1].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[1].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[1].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
        internal inner class WithExpr {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("with xmlns:b=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"http://www.example.com\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("with xmlns=\"\" {test()}")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultNamespace(XdmNamespaceType.DefaultFunctionRef).toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespacePrefix?.data, `is`(""))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(element[0].accepts(XdmNamespaceType.DefaultElement), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(element[0].accepts(XdmNamespaceType.DefaultType), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.None), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Prefixed), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.Undefined), `is`(false))
                assertThat(element[0].accepts(XdmNamespaceType.XQuery), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) In-scope variables")
    internal inner class InScopeVariables {
        fun variables(query: String, expected: List<String>) {
            val ctx = parse<XPathFunctionCall>(query)[0]
            val variables = ctx.inScopeVariables().mapNotNull { variable ->
                variable.variableName?.let { name -> qname_presentation(name) }
            }.toList()
            assertThat(variables, `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.2) For Clause")
        internal inner class ForClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (45) ForBinding ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class ForBinding_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for \$x in test() return 1", listOf())
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for \$x in 2, \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("for \$x in for \$y in test() return 1 return 2", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for \$x in 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("for \$x in for \$y in 2 return test() return 2", listOf("y"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("for \$x in for \$y in 2 return 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("for \$x in 1, \$y in 2 return test()", listOf("x", "y"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (45) ForBinding ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class ForBinding_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for \$x in 1 for \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for \$x in 2 for \$y in 3, \$z in test() return 1", listOf("y", "x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("for \$x in 1 for \$y in for \$z in test() return 1 return 2", listOf("x"))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for \$x in 1 for \$y in 2 return test()", listOf("y", "x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("for \$x in 1 for \$y in for \$z in 2 return test() return 2", listOf("z", "x"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("for \$x in 1 for \$y in for \$z in 2 return 1 return test()", listOf("y", "x"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("for \$x in 1 for \$y in 2, \$z in 3 return test()", listOf("y", "z", "x"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
            internal inner class PositionalVar {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for \$x at \$a in test() return 1", listOf())
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for \$x at \$a in 2, \$y at \$b in test() return 1", listOf("x", "a"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("for \$x at \$a in for \$y at \$b in test() return 1 return 2", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for \$x at \$a in 1 return test()", listOf("x", "a"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("for \$x at \$a in for \$y at \$b in 2 return test() return 2", listOf("y", "b"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("for \$x at \$a in for \$y at \$b in 2 return 1 return test()", listOf("x", "a"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("for \$x at \$a in 1, \$y at \$b in 2 return test()", listOf("x", "a", "y", "b"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.3) Let Clause")
        internal inner class LetClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (49) LetBinding ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class LetBinding_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("let \$x := test() return 1", listOf())
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("let \$x := 2, \$y := test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("let \$x := let \$y := test() return 1 return 2", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("let \$x := 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("let \$x := let \$y := 2 return test() return 2", listOf("y"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("let \$x := let \$y := 2 return 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("let \$x := 1, \$y := 2 return test()", listOf("x", "y"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (49) LetBinding ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class LetBinding_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("let \$x := 1 let \$y := test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("let \$x := 1 let \$y := 2, \$z := test() return 1", listOf("y", "x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("let \$x := 1 let \$y := let \$z := test() return 1 return 2", listOf("x"))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("let \$x := 1 let \$y := 2 return test()", listOf("y", "x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("let \$x := 1 let \$y := let \$z := 2 return test() return 2", listOf("z", "x"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("let \$x := 1 let \$y := let \$z := 2 return 1 return test()", listOf("y", "x"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("let \$x := 1 let \$y := 2, \$z := 3 return test()", listOf("y", "z", "x"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4) Window Clause")
        internal inner class WindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (53) WindowStartCondition ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class WindowStartCondition_InitialClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() start \$y at \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y at \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y at \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() start \$y when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y when test() return 2",
                            listOf("y")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y when 2 return test()",
                            listOf("x", "y")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() start \$y previous \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y previous \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y previous \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() start \$y next \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y next \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 start \$y next \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (53) WindowStartCondition ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class WindowStartCondition_IntermediateClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() start \$z at \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() start \$z when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z when test() return 2",
                            listOf("z", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z when 2 return test()",
                            listOf("y", "z", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() start \$z previous \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() start \$z next \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (54) WindowEndCondition ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class WindowEndCondition_InitialClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() end \$y at \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y at \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y at \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() end \$y when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y when test() return 2",
                            listOf("y")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y when 2 return test()",
                            listOf("x", "y")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() end \$y previous \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y previous \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y previous \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for sliding window \$x in test() end \$y next \$z when 1 return 2",
                            listOf()
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y next \$z when test() return 2",
                            listOf("y", "z")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for sliding window \$x in 1 end \$y next \$z when 2 return test()",
                            listOf("x", "y", "z")
                        )
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (54) WindowEndCondition ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class WindowEndCondition_IntermediateClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() end \$z at \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() end \$z when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z when test() return 2",
                            listOf("z", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z when 2 return test()",
                            listOf("y", "z", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() end \$z previous \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in test() end \$z next \$w when 1 return 2",
                            listOf("x")
                        )
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when test() return 2",
                            listOf("z", "w", "x")
                        )
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        variables(
                            "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when 2 return test()",
                            listOf("y", "z", "w", "x")
                        )
                    }
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4.1) Tumbling Window Clause")
        internal inner class TumblingWindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class TumblingWindowClause_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for tumbling window \$x in test() return 1", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for tumbling window \$x in 1 return test()", listOf("x"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause ; XQuery 3.1 EBNF (43) IntermeiateClause")
            internal inner class TumblingWindowClause_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for \$x in 1 for tumbling window \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for \$x in 1 for tumbling window \$y in 1 return test()", listOf("y", "x"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4.2) Sliding Window Clause")
        internal inner class SlidingWindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class SlidingWindowClause_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for sliding window \$x in test() return 1", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for sliding window \$x in 1 return test()", listOf("x"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class SlidingWindowClause_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for \$x in 1 for sliding window \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for \$x in 1 for sliding window \$y in 1 return test()", listOf("y", "x"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.6) Count Clause")
        internal inner class CountClause {
            @Test
            @DisplayName("XQuery 3.1 EBNF (59) CountClause")
            fun countClause() {
                variables("for \$x in 1 count \$y return test()", listOf("y", "x"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.7) Group By Clause")
        internal inner class GroupByClause {
            @Test
            @DisplayName("from return expression")
            fun returnExpr() {
                variables("for \$x in 1 group by \$y return test()", listOf("y", "x"))
            }

            @Test
            @DisplayName("from return expression; multiple group by variables")
            fun multiple() {
                variables("for \$x in 1 group by \$y, \$z return test()", listOf("y", "z", "x"))
            }

            @Test
            @DisplayName("from group specification expression")
            fun valueExpr() {
                variables("for \$x in 1 group by \$y := test() return 1", listOf("x"))
            }

            @Test
            @DisplayName("from group by expression; previous group specification in scope")
            fun valueExpr_PreviousSpecInScope() {
                variables("for \$x in 1 group by \$y := 2, \$z := test() return 1", listOf("y", "x"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.16) Quantified Expressions")
        internal inner class QuantifiedExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr")
            internal inner class QuantifiedExpr {
                @Test
                @DisplayName("single binding; 'in' Expr")
                fun testQuantifiedExpr_SingleBinding_InExpr() {
                    variables("some \$x in test() satisfies 1", listOf())
                }

                @Test
                @DisplayName("single binding; 'satisfies' Expr")
                fun testQuantifiedExpr_SingleBinding_SatisfiesExpr() {
                    variables("some \$x in 1 satisfies test()", listOf("x"))
                }

                @Test
                @DisplayName("multiple bindings; first 'in' Expr")
                fun testQuantifiedExpr_MultipleBindings_FirstInExpr() {
                    variables("some \$x in test(), \$y in 1 satisfies 2", listOf())
                }

                @Test
                @DisplayName("multiple bindings; last 'in' Expr")
                fun testQuantifiedExpr_MultipleBindings_LastInExpr() {
                    variables("some \$x in 1, \$y in test() satisfies 2", listOf("x"))
                }

                @Test
                @DisplayName("multiple bindings; 'satisfies' Expr")
                fun testQuantifiedExpr_MultipleBindings_SatisfiesExpr() {
                    variables("some \$x in 1, \$y in 2 satisfies test()", listOf("y", "x"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
        internal inner class Typeswitch {
            @Test
            @DisplayName("case clause; first clause")
            fun caseClause() {
                variables(
                    "typeswitch (2) case \$x as xs:string return test() case \$y as xs:int return 2 default \$z return 3",
                    listOf("x")
                )
            }

            @Test
            @DisplayName("case clause; non-first clause")
            fun caseClause_NotFirst() {
                variables(
                    "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return test() default \$z return 3",
                    listOf("y")
                )
            }

            @Test
            @DisplayName("default case clause")
            fun defaultCaseClause() {
                variables(
                    "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return 2 default \$z return test()",
                    listOf("z")
                )
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (4.16) Variable Declaration")
        internal inner class VariableDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
            internal inner class VarDecl {
                @Test
                @DisplayName("no variable declarations; in function body (prolog)")
                fun noVarDecls_InProlog() {
                    variables("declare function f() { test() }; 1", listOf())
                }

                @Test
                @DisplayName("no variable declarations; in query body")
                fun noVarDecls_QueryBodyExpr() {
                    variables("declare function f() {}; test()", listOf())
                }

                @Test
                @DisplayName("single variable declaration; in function body (prolog)")
                fun singleVarDecl_InProlog() {
                    variables("declare function f() { test() }; declare variable \$x := 1; 2", listOf("x"))
                }

                @Test
                @DisplayName("single variable declaration; in query body")
                fun singleVarDecl_QueryBodyExpr() {
                    variables("declare function f() {}; declare variable \$x := 1; test()", listOf("x"))
                }

                @Test
                @DisplayName("multiple variable declarations; in function body (prolog)")
                fun multipleVarDecls_InProlog() {
                    variables(
                        "declare function f() { test() }; declare variable \$x := 1; declare variable \$y := 2; 3",
                        listOf("y", "x")
                    )
                }

                @Test
                @DisplayName("multiple variable declarations; in query body")
                fun multipleVarDecls_QueryBodyExpr() {
                    variables(
                        "declare function f() {}; declare variable \$x := 1; declare variable \$y := 2; test()",
                        listOf("y", "x")
                    )
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (4.18) Function Declaration")
        internal inner class FunctionDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (33) ParamList")
            internal inner class ParamList {
                @Test
                @DisplayName("no parameters")
                fun noParameters() {
                    variables("declare function f() { test() }; 1", listOf())
                }

                @Test
                @DisplayName("single parameter")
                fun singleParameter() {
                    variables("declare function f(\$x) { test() }; 1", listOf("x"))
                }

                @Test
                @DisplayName("multiple parameters")
                fun multipleParameters() {
                    variables("declare function f(\$x, \$y) { test() }; 1", listOf("x", "y"))
                }

                @Test
                @DisplayName("outside function body")
                fun outsideFunctionBody() {
                    variables("declare function f(\$x) {}; test()", listOf())
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (34) Param")
            internal inner class Param {
                @Test
                @DisplayName("in FunctionBody; no parameters")
                fun testInlineFunctionExpr_FunctionBody_NoParameters() {
                    variables("function () { test() }", listOf())
                }

                @Test
                @DisplayName("in FunctionBody; single parameter")
                fun testInlineFunctionExpr_FunctionBody_SingleParameter() {
                    variables("function (\$x) { test() }", listOf("x"))
                }

                @Test
                @DisplayName("in FunctionBody; multiple parameters")
                fun testInlineFunctionExpr_FunctionBody_MultipleParameters() {
                    variables("function (\$x, \$y) { test() }", listOf("x", "y"))
                }

                @Test
                @DisplayName("outside FunctionBody")
                fun testInlineFunctionExpr_OutsideFunctionBody() {
                    variables("function (\$x) {}(test())", listOf())
                }
            }
        }

        @Nested
        @DisplayName("XQuery Scripting Extension 1.0 (5.2) Block Expressions")
        internal inner class BlockExpressions {
            @Nested
            @DisplayName("XQuery Scripting Extension 1.0 EBNF (156) BlockVarDecl")
            internal inner class BlockVarDecl {
                @Test
                @DisplayName("no declarations")
                fun noDeclarations() {
                    variables("block { test() }", listOf())
                }

                @Test
                @DisplayName("single declaration; from value expression")
                fun singleVarDecl_ValueExpr() {
                    variables("block { declare \$x := test(); 1 }", listOf())
                }

                @Test
                @DisplayName("single declaration; from block expression")
                fun singleVarDecl_BlockExpr() {
                    variables("block { declare \$x := 1; test() }", listOf("x"))
                }

                @Test
                @DisplayName("multiple entries in a declaration; from value expression of first variable")
                fun multipleVarDeclEntries_ValueExpr_FirstDecl() {
                    variables("block { declare \$x := test(), \$y := 1; 2 }", listOf())
                }

                @Test
                @DisplayName("multiple entries in a declaration; from value expression of last variable")
                fun multipleVarDeclEntries_ValueExpr_LastDecl() {
                    variables("block { declare \$x := 1, \$y := test(); 2 }", listOf("x"))
                }

                @Test
                @DisplayName("multiple entries in a declaration; from block expression")
                fun multipleVarDeclEntries_BlockExpr() {
                    variables("block { declare \$x := 1, \$y := 2; test() }", listOf("x", "y"))
                }

                @Test
                @DisplayName("multiple declarations; from value expression of first variable")
                fun multipleVarDecl_ValueExpr_FirstDecl() {
                    variables("block { declare \$x := test(); declare \$y := 1; 2 }", listOf())
                }

                @Test
                @DisplayName("multiple declarations; from value expression of last variable")
                fun multipleVarDecl_ValueExpr_LastDecl() {
                    variables("block { declare \$x := 1; declare \$y := test(); 2 }", listOf("x"))
                }

                @Test
                @DisplayName("multiple declarations; from block expression")
                fun multipleVarDecl_BlockExpr() {
                    variables("block { declare \$x := 1; declare \$y := 2; test() }", listOf("x", "y"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin (3.17.1) For Member Clause")
        internal inner class ForMemberClause {
            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (53) ForBinding ; XQuery 4.0 ED EBNF (47) InitialClause")
            internal inner class ForBinding_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for member \$x in test() return 1", listOf())
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for member \$x in 2, \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("for member \$x in for member \$y in test() return 1 return 2", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for member \$x in 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables("for member \$x in for member \$y in 2 return test() return 2", listOf("y"))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables("for member \$x in for member \$y in 2 return 1 return test()", listOf("x"))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables("for member \$x in 1, \$y in 2 return test()", listOf("x", "y"))
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (53) ForBinding ; XQuery 4.0 EBNF (48) IntermediateClause")
            internal inner class ForBinding_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for member \$x in 1 for member \$y in test() return 1", listOf("x"))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for member \$x in 2 for member \$y in 3, \$z in test() return 1", listOf("y", "x"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables(
                        "for member \$x in 1 for member \$y in for member \$z in test() return 1 return 2",
                        listOf("x")
                    )
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for member \$x in 1 for member \$y in 2 return test()", listOf("y", "x"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables(
                        "for member \$x in 1 for member \$y in for member \$z in 2 return test() return 2",
                        listOf("z", "x")
                    )
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables(
                        "for member \$x in 1 for member \$y in for member \$z in 2 return 1 return test()",
                        listOf("y", "x")
                    )
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables(
                        "for member \$x in 1 for member \$y in 2, \$z in 3 return test()",
                        listOf("y", "z", "x")
                    )
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (54) PositionalVar")
            internal inner class PositionalVar {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    variables("for member \$x at \$a in test() return 1", listOf())
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    variables("for member \$x at \$a in 2, \$y at \$b in test() return 1", listOf("x", "a"))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    variables("for member \$x at \$a in for member \$y at \$b in test() return 1 return 2", listOf())
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    variables("for member \$x at \$a in 1 return test()", listOf("x", "a"))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    variables(
                        "for member \$x at \$a in for member \$y at \$b in 2 return test() return 2",
                        listOf("y", "b")
                    )
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    variables(
                        "for member \$x at \$a in for member \$y at \$b in 2 return 1 return test()",
                        listOf("x", "a")
                    )
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    variables(
                        "for member \$x at \$a in 1, \$y at \$b in 2 return test()",
                        listOf("x", "a", "y", "b")
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Statically known function signatures")
    internal inner class StaticallyKnownFunctionSignatures {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        string()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        transform-to-json(), (: imported function :)
                        array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        test()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { test:g() };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        json:transform-to-json(), (: imported function :)
                        json:array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        e:test()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("fn:concat()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].declaredArity, `is`(3))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].declaredArity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].declaredArity, `is`(0))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        string#0
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        func#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        transform-to-json#0, (: imported function :)
                        array#0 (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        test#0
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { test:g#0 };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        a:sessions#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        json:transform-to-json#0, (: imported function :)
                        json:array#0 (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        a:sessions#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        e:test#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("fn:concat#3")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].declaredArity, `is`(3))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].declaredArity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].declaredArity, `is`(0))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        () => string()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        () => func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        () => transform-to-json(), (: imported function :)
                        () => array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        () => test()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("() => fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { () => test:g() };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        () => a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        () => json:transform-to-json(), (: imported function :)
                        () => json:array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].declaredArity, `is`(2))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        () => a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        () => e:test()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("() => fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].declaredArity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("() => fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].declaredArity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].declaredArity, `is`(0))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("() => fn:concat()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].declaredArity, `is`(3))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].declaredArity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].declaredArity, `is`(0))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }
    }
}
