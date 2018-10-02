/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.model

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Static Context")
private class XQueryStaticContextTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Statically known namespaces")
    internal inner class StaticallyKnownNamespaces {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("module declaration")
            fun testStaticallyKnownNamespaces_ModuleDecl() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("module namespace a='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("module namespace ='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
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

                val element = parse<XQueryFunctionDecl>("import schema namespace a='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_SchemaImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("import schema namespace a='http://www.example.com'; a:test();")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import schema namespace ='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import schema namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
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

                val element = parse<XQueryFunctionDecl>("import module namespace a='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_ModuleImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("import module namespace a='http://www.example.com'; a:test();")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import module namespace ='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
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

                val element = parse<XQueryFunctionDecl>("declare namespace a='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_NamespaceDecl_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("declare namespace a='http://www.example.com'; a:test();")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("declare namespace ='http://www.example.com'; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("declare namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
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
                assertThat(namespaces.size, `is`(6))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("b"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("namespace prefix, missing namespace uri")
            fun testStaticallyKnownNamespaces_DirAttribute_Xmlns_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a xmlns:b=>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("non-namespace prefix atribute")
            fun testStaticallyKnownNamespaces_DirAttribute() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a b='http://www.example.com'>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                // predefined XQuery 1.0 namespaces:

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }
        }

        @Nested
        @DisplayName("predefined namespaces")
        internal inner class PredefinedNamespaces {
            @Test
            @DisplayName("XQuery 1.0")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_XQuery10() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(5))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))
            }

            @Test
            @DisplayName("MarkLogic 6.0")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_MarkLogic60() {
                settings.implementationVersion = "marklogic/v6"
                settings.XQueryVersion = "1.0-ml"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(22))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("xsi"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema-instance"))

                assertThat(namespaces[1].namespacePrefix!!.data, `is`("xs"))
                assertThat(namespaces[1].namespaceUri!!.data, `is`("http://www.w3.org/2001/XMLSchema"))

                assertThat(namespaces[2].namespacePrefix!!.data, `is`("xqterr"))
                assertThat(namespaces[2].namespaceUri!!.data, `is`("http://www.w3.org/2005/xqt-error"))

                assertThat(namespaces[3].namespacePrefix!!.data, `is`("xqe"))
                assertThat(namespaces[3].namespaceUri!!.data, `is`("http://marklogic.com/xqe"))

                assertThat(namespaces[4].namespacePrefix!!.data, `is`("xml"))
                assertThat(namespaces[4].namespaceUri!!.data, `is`("http://www.w3.org/XML/1998/namespace"))

                assertThat(namespaces[5].namespacePrefix!!.data, `is`("xdmp"))
                assertThat(namespaces[5].namespaceUri!!.data, `is`("http://marklogic.com/xdmp"))

                assertThat(namespaces[6].namespacePrefix!!.data, `is`("spell"))
                assertThat(namespaces[6].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/spell"))

                assertThat(namespaces[7].namespacePrefix!!.data, `is`("sec"))
                assertThat(namespaces[7].namespaceUri!!.data, `is`("http://marklogic.com/security"))

                assertThat(namespaces[8].namespacePrefix!!.data, `is`("prop"))
                assertThat(namespaces[8].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/property"))

                assertThat(namespaces[9].namespacePrefix!!.data, `is`("prof"))
                assertThat(namespaces[9].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/profile"))

                assertThat(namespaces[10].namespacePrefix!!.data, `is`("math"))
                assertThat(namespaces[10].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/math"))

                assertThat(namespaces[11].namespacePrefix!!.data, `is`("map"))
                assertThat(namespaces[11].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/map"))

                assertThat(namespaces[12].namespacePrefix!!.data, `is`("lock"))
                assertThat(namespaces[12].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/lock"))

                assertThat(namespaces[13].namespacePrefix!!.data, `is`("local"))
                assertThat(namespaces[13].namespaceUri!!.data, `is`("http://www.w3.org/2005/xquery-local-functions"))

                assertThat(namespaces[14].namespacePrefix!!.data, `is`("json"))
                assertThat(namespaces[14].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/json"))

                assertThat(namespaces[15].namespacePrefix!!.data, `is`("fn"))
                assertThat(namespaces[15].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))

                assertThat(namespaces[16].namespacePrefix!!.data, `is`("error"))
                assertThat(namespaces[16].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/error"))

                assertThat(namespaces[17].namespacePrefix!!.data, `is`("err"))
                assertThat(namespaces[17].namespaceUri!!.data, `is`("http://www.w3.org/2005/xqt-error"))

                assertThat(namespaces[18].namespacePrefix!!.data, `is`("dir"))
                assertThat(namespaces[18].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/directory"))

                assertThat(namespaces[19].namespacePrefix!!.data, `is`("dbg"))
                assertThat(namespaces[19].namespaceUri!!.data, `is`("http://marklogic.com/xdmp/dbg"))

                assertThat(namespaces[20].namespacePrefix!!.data, `is`("dav"))
                assertThat(namespaces[20].namespaceUri!!.data, `is`("DAV:"))

                assertThat(namespaces[21].namespacePrefix!!.data, `is`("cts"))
                assertThat(namespaces[21].namespaceUri!!.data, `is`("http://marklogic.com/cts"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default element/type namespace")
    internal inner class DefaultElementTypeNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
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

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>("declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>("declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions/math"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
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

                    assertThat(decls[0].arity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/resolve/namespaces/ModuleDecl.xq";
                        func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
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

                    assertThat(decls[0].arity, `is`(0))
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
                        import module namespace a = "http://basex.org/modules/admin" at "res://basex.org/modules/admin.xqy";
                        a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
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

                    assertThat(decls[0].arity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/resolve/namespaces/ModuleDecl.xq";
                        func#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
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

                    assertThat(decls[0].arity, `is`(0))
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
                        import module namespace a = "http://basex.org/modules/admin" at "res://basex.org/modules/admin.xqy";
                        a:sessions#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
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

                    assertThat(decls[0].arity, `is`(1))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/resolve/namespaces/ModuleDecl.xq";
                        () => func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
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

                    assertThat(decls[0].arity, `is`(0))
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
                        import module namespace a = "http://basex.org/modules/admin" at "res://basex.org/modules/admin.xqy";
                        () => a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(2))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(1))
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

                    assertThat(decls[0].arity, `is`(0))
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

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }
        }
    }

    // region In-Scope Variables (current file only)
    // region BlockDecls -> BlockVarDecl -> BlockVarDeclEntry [XQuery Scripting Extension]

    @Test
    fun testBlockVarDeclEntry_NoDeclarations() {
        val element = parse<XPathFunctionCall>("block { test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testBlockVarDeclEntry_SingleVarDecl_ValueExpr() {
        val element = parse<XPathFunctionCall>("block { declare \$x := test(); 1 }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testBlockVarDeclEntry_SingleVarDecl_BlockExpr() {
        val element = parse<XPathFunctionCall>("block { declare \$x := 1; test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDeclEntries_ValueExpr_FirstDecl() {
        val element = parse<XPathFunctionCall>("block { declare \$x := test(), \$y := 1; 2 }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDeclEntries_ValueExpr_LastDecl() {
        val element = parse<XPathFunctionCall>("block { declare \$x := 1, \$y := test(); 2 }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDeclEntries_BlockExpr() {
        val element = parse<XPathFunctionCall>("block { declare \$x := 1, \$y := 2; test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDecl_ValueExpr_FirstDecl() {
        val element = parse<XPathFunctionCall>("block { declare \$x := test(); declare \$y := 1; 2 }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDecl_ValueExpr_LastDecl() {
        val element = parse<XPathFunctionCall>("block { declare \$x := 1; declare \$y := test(); 2 }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testBlockVarDeclEntry_MultipleVarDecl_BlockExpr() {
        val element = parse<XPathFunctionCall>("block { declare \$x := 1; declare \$y := 2; test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> ForClause -> ForBinding

    @Test
    fun testInScopeVariables_ForBinding_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_ForBinding_InExpr_PreviousBindingInScope() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 2, \$y in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBinding_NestedFLWORExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in for \$y in test() return 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_ForBinding_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBinding_NestedFLWORExpr_InnerReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in for \$y in 2 return test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBinding_NestedFLWORExpr_OuterReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in for \$y in 2 return 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBinding_Multiple_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1, \$y in 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> ForClause -> ForBinding (PositionalVar)

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_InExpr_PreviousBindingInScope() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in 2, \$y at \$ b in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("a"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_NestedFLWORExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in for \$y at \$b in test() return 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("a"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_NestedFLWORExpr_InnerReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in for \$y at \$b in 2 return test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("b"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_NestedFLWORExpr_OuterReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in for \$y at \$b in 2 return 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("a"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_ForBindingPositionalVar_Multiple_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x at \$a in 1, \$y at \$b in 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("a"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("y"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("b"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> LetClause -> LetBinding

    @Test
    fun testInScopeVariables_LetBinding_ValueExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_LetBinding_InExpr_PreviousBindingInScope() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 2, \$y := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_LetBinding_NestedFLWORExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := let \$y := test() return 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_LetBinding_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_LetBinding_NestedFLWORExpr_InnerReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := let \$y := 2 return test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_LetBinding_NestedFLWORExpr_OuterReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := let \$y := 2 return 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_LetBinding_Multiple_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1, \$y := 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> SlidingWindowClause

    @Test
    fun testInScopeVariables_SlidingWindowClause_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> SlidingWindowClause (CurrentItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() start \$y when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() end \$y when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> SlidingWindowClause (NextItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() start \$y next \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y next \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y next \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() end \$y next \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y next \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y next \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> SlidingWindowClause (PositionalVar)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() start \$y at \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y at \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y at \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() end \$y at \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y at \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y at \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> SlidingWindowClause (PreviousItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() start \$y previous \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y previous \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowStartCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 start \$y previous \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in test() end \$y previous \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y previous \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_SlidingWindowClause_WindowEndCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for sliding window \$x in 1 end \$y previous \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> TumblingWindowClause

    @Test
    fun testInScopeVariables_TumblingWindowClause_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> InitialClause -> TumblingWindowClause (CurrentItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() start \$y when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() end \$y when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> TumblingWindowClause (NextItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() start \$y next \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y next \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y next \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() end \$y next \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y next \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y next \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> TumblingWindowClause (PositionalVar)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() start \$y at \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y at \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y at \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() end \$y at \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y at \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y at \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> InitialClause -> TumblingWindowClause (PreviousItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() start \$y previous \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y previous \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowStartCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 start \$y previous \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in test() end \$y previous \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y previous \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_TumblingWindowClause_WindowEndCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for tumbling window \$x in 1 end \$y previous \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> CountClause

    @Test
    fun testInScopeVariables_CountClause() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 count \$y return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> ForClause -> ForBinding

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_InExpr_PreviousBindingInScope() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 2 for \$y in 3, \$z in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_NestedFLWORExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in for \$z in test() return 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_NestedFLWORExpr_InnerReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in for \$z in 2 return test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_NestedFLWORExpr_OuterReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in for \$z in 2 return 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_ForBinding_Multiple_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for \$y in 2, \$z in 3 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> GroupByClause

    @Test
    fun testInScopeVariables_GroupByClause() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 group by \$y return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_GroupByClause_Multiple() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 group by \$y, \$z return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_GroupByClause_ValueExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 group by \$y := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_GroupByClause_ValueExpr_PreviousSpecInScope() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 group by \$y := 2, \$z := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> LetClause -> LetBinding

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_ValueExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_ValueExpr_PreviousBindingInScope() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := 2, \$z := test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_NestedFLWORExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := let \$z := test() return 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_NestedFLWORExpr_InnerReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := let \$z := 2 return test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_NestedFLWORExpr_OuterReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := let \$z := 2 return 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_LetBinding_Multiple_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "let \$x := 1 let \$y := 2, \$z := 3 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> SlidingWindowClause

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> SlidingWindowClause (CurrentItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() start \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() end \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> SlidingWindowClause (NextItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() start \$z next \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() end \$z next \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> SlidingWindowClause (PositionalVar)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() start \$z at \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() end \$z at \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> SlidingWindowClause (PreviousItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() start \$z previous \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowStartCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in test() end \$z previous \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_SlidingWindowClause_WindowEndCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> TumblingWindowClause

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() return 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region FLWORExpr -> IntermediateClause -> TumblingWindowClause (CurrentItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() start \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_CurrentItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() end \$z when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_CurrentItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_CurrentItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> TumblingWindowClause (NextItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() start \$z next \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z next \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z next \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_NextItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() end \$z next \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_NextItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z next \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_NextItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z next \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> TumblingWindowClause (PositionalVar)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() start \$z at \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z at \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z at \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PositionalVar_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() end \$z at \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PositionalVar_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z at \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PositionalVar_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z at \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FLWORExpr -> IntermediateClause -> TumblingWindowClause (PreviousItem)
    // region WindowStartCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() start \$z previous \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z previous \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowStartCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 start \$z previous \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region WindowEndCondition

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PreviousItem_InExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in test() end \$z previous \$w when 1 return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PreviousItem_WhenExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z previous \$w when test() return 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(3))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_IntermediateClause_TumblingWindowClause_WindowEndCondition_PreviousItem_ReturnExpr() {
        val element = parse<XPathFunctionCall>(
                "for \$x in 1 for tumbling window \$y in 1 end \$z previous \$w when 2 return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(4))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region FunctionDecl -> ParamList -> Param

    @Test
    fun testFunctionDecl_FunctionBody_NoParameters() {
        val element = parse<XPathFunctionCall>("declare function f() { test() }; 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testFunctionDecl_FunctionBody_SingleParameter() {
        val element = parse<XPathFunctionCall>("declare function f(\$x) { test() }; 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testFunctionDecl_FunctionBody_MultipleParameters() {
        val element = parse<XPathFunctionCall>("declare function f(\$x, \$y) { test() }; 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testFunctionDecl_OutsideFunctionBody() {
        val element = parse<XPathFunctionCall>("declare function f(\$x) {}; test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    // endregion
    // region Prolog -> VarDecl

    @Test
    fun testProlog_NoVarDecls_InProlog() {
        val element = parse<XPathFunctionCall>("declare function f() { test() }; 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testProlog_NoVarDecls_QueryBodyExpr() {
        val element = parse<XPathFunctionCall>("declare function f() {}; test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testProlog_SingleVarDecl_InProlog() {
        val element = parse<XPathFunctionCall>("declare function f() { test() }; declare variable \$x := 1; 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testProlog_SingleVarDecl_QueryBodyExpr() {
        val element = parse<XPathFunctionCall>("declare function f() {}; declare variable \$x := 1; test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testProlog_MultipleVarDecls_InProlog() {
        val element = parse<XPathFunctionCall>("declare function f() { test() }; declare variable \$x := 1; declare variable \$y := 2; 3")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testProlog_MultipleVarDecls_QueryBodyExpr() {
        val element = parse<XPathFunctionCall>("declare function f() {}; declare variable \$x := 1; declare variable \$y := 2; test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // region TypeswitchExpr -> CaseClause + DefaultCaseClause

    @Test
    fun testInScopeVariables_CaseClause() {
        val element = parse<XPathFunctionCall>(
            "typeswitch (2) case \$x as xs:string return test() case \$y as xs:int return 2 default \$z return 3")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_CaseClause_NotFirst() {
        val element = parse<XPathFunctionCall>(
                "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return test() default \$z return 3")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        // Only variable `y` is in scope.
        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInScopeVariables_DefaultCaseClause() {
        val element = parse<XPathFunctionCall>(
                "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return 2 default \$z return test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
}
