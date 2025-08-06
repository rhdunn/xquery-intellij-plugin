// Copyright (C) 2018, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xdm.tests.functions.op

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_equal
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("XPath and XQuery Functions and Operators 3.1")
class FunctionsAndOperatorsTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("FunctionsAndOperatorsTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
    }

    @Nested
    @DisplayName("XPath and XQuery Functions and Operators 3.1 (10.2.1) op:QName-equal")
    internal inner class OpQNameEqual {
        @Test
        @DisplayName("local-name == local-name")
        fun ncname() {
            val qnames = parse<XsQNameValue>("test test testing")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("local-name == prefix:local-name")
        fun ncnameAndQname() {
            val qnames = parse<XsQNameValue>("string xs:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("local-name == Q{namespace}local-name")
        fun ncnameAndUriQualifiedName() {
            val qnames = parse<XsQNameValue>("string Q{http://www.w3.org/2001/XMLSchema}string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == prefix:local-name")
        fun qname() {
            val qnames = parse<XsQNameValue>("xs:string xs:string xs:integer fn:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))

            // prefix differs
            assertThat(qname_equal(qnames[0], qnames[3]), `is`(false))
            assertThat(qname_equal(qnames[3], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == local-name")
        fun qnameAndNcname() {
            val qnames = parse<XsQNameValue>("xs:string string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == Q{namespace}local-name")
        fun qnameAndUriQualifiedName() {
            val qnames = parse<XsQNameValue>("xs:string Q{http://www.w3.org/2001/XMLSchema}string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == Q{namespace}local-name")
        fun uriQualifiedName() {
            val qnames = parse<XsQNameValue>(
                """
                Q{http://www.w3.org/2001/XMLSchema}string
                Q{http://www.w3.org/2001/XMLSchema}string
                Q{http://www.w3.org/2001/XMLSchema}integer
                Q{http://www.w3.org/2005/xpath-functions}string
                """
            )

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))

            // namespace differs
            assertThat(qname_equal(qnames[0], qnames[3]), `is`(false))
            assertThat(qname_equal(qnames[3], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == prefix:local-name")
        fun uriQualifiedNameAndQName() {
            val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string xs:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == local-name")
        fun uriQualifiedNameAndNcname() {
            val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Nested
        @DisplayName("expanded")
        internal inner class Expanded {
            @Test
            @DisplayName("local-name == local-name")
            fun ncname() {
                val qnames = parse<XsQNameValue>("test test testing")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("local-name == prefix:local-name")
            fun ncnameAndQname() {
                val qnames = parse<XsQNameValue>(
                    """
                    declare default element namespace "http://www.w3.org/2005/xpath-functions";
                    string
                    xs:string
                    fn:string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(false))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
            }

            @Test
            @DisplayName("local-name == Q{namespace}local-name")
            fun ncnameAndUriQualifiedName() {
                val qnames = parse<XsQNameValue>("string Q{http://www.w3.org/2001/XMLSchema}string Q{}string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(false))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
            }

            @Test
            @DisplayName("prefix:local-name == prefix:local-name")
            fun qname() {
                val qnames = parse<XsQNameValue>("xs:string xs:string xs:integer fn:string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))

                // prefix differs
                assertThat(qname_equal(expanded[0], expanded[3]), `is`(false))
                assertThat(qname_equal(expanded[3], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("prefix:local-name == local-name")
            fun qnameAndNcname() {
                val qnames = parse<XsQNameValue>(
                    """
                    declare default element namespace "http://www.w3.org/2005/xpath-functions";
                    string
                    xs:string
                    fn:string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[1], expanded[0]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(true))
            }

            @Test
            @DisplayName("prefix:local-name == Q{namespace}local-name")
            fun qnameAndUriQualifiedName() {
                val qnames = parse<XsQNameValue>("xs:string Q{http://www.w3.org/2001/XMLSchema}string Q{}string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == Q{namespace}local-name")
            fun uriQualifiedName() {
                val qnames = parse<XsQNameValue>(
                    """
                    Q{http://www.w3.org/2001/XMLSchema}string
                    Q{http://www.w3.org/2001/XMLSchema}string
                    Q{http://www.w3.org/2001/XMLSchema}integer
                    Q{http://www.w3.org/2005/xpath-functions}string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))

                // namespace differs
                assertThat(qname_equal(expanded[0], expanded[3]), `is`(false))
                assertThat(qname_equal(expanded[3], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == prefix:local-name")
            fun uriQualifiedNameAndQName() {
                val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string Q{}string xs:string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[2]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == local-name")
            fun uriQualifiedNameAndNcname() {
                val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string Q{}string string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[1], expanded[2]), `is`(true))
            }
        }
    }
}
