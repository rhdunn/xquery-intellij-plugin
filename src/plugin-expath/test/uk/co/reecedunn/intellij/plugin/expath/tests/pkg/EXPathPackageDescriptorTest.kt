/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.expath.tests.pkg

import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.psi.xml.XmlFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackageDescriptor
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("EXPath Packaging System 9 May 2012")
private class EXPathPackageDescriptorTest : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun pkg(xml: String): EXPathPackageDescriptor {
        return EXPathPackageDescriptor(createVirtualFile("package.xml", xml))
    }

    @Test
    @DisplayName("missing fields")
    fun missingFields() {
        @Language("XML")
        val pkg = pkg("<package xmlns=\"http://expath.org/ns/pkg\"/>")

        assertThat(pkg.name, `is`(nullValue()))
        assertThat(pkg.abbrev, `is`(nullValue()))
        assertThat(pkg.version, `is`(nullValue()))
        assertThat(pkg.spec, `is`(nullValue()))
        assertThat(pkg.title, `is`(nullValue()))
        assertThat(pkg.home, `is`(nullValue()))
        assertThat(pkg.dependencies.size, `is`(0))
        assertThat(pkg.components.size, `is`(0))
    }

    @Test
    @DisplayName("properties")
    fun properties() {
        @Language("XML")
        val pkg = pkg(
            """
            <package xmlns="http://expath.org/ns/pkg"
                     name="http://www.example.com"
                     abbrev="test"
                     version="2.4"
                     spec="1.0">
                <title>Test Package</title>
                <home>http://www.example.com/home</home>
            </package>
            """.trimIndent()
        )

        assertThat(pkg.name?.data, `is`("http://www.example.com"))
        assertThat(pkg.abbrev, `is`("test"))
        assertThat(pkg.version, `is`("2.4"))
        assertThat(pkg.spec, `is`("1.0"))
        assertThat(pkg.title, `is`("Test Package"))
        assertThat(pkg.home?.data, `is`("http://www.example.com/home"))
        assertThat(pkg.dependencies.size, `is`(0))
        assertThat(pkg.components.size, `is`(0))
    }

    @Nested
    @DisplayName("package dependencies")
    internal inner class PackageDependencies {
        @Test
        @DisplayName("versions")
        fun versions() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency package="http://www.example.com/pkg1" versions="1.1 1.2"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg?.data, `is`("http://www.example.com/pkg1"))
            assertThat(pkg.dependencies[0].processor, `is`(nullValue()))
            assertThat(pkg.dependencies[0].versions, `is`(listOf("1.1", "1.2")))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver")
        fun semver() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency package="http://www.example.com/pkg1" semver="1.1"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg?.data, `is`("http://www.example.com/pkg1"))
            assertThat(pkg.dependencies[0].processor, `is`(nullValue()))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`("1.1"))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver-min")
        fun semverMin() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency package="http://www.example.com/pkg1" semver-min="1.1"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg?.data, `is`("http://www.example.com/pkg1"))
            assertThat(pkg.dependencies[0].processor, `is`(nullValue()))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`("1.1"))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver-max")
        fun semverMax() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency package="http://www.example.com/pkg1" semver-max="1.1"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg?.data, `is`("http://www.example.com/pkg1"))
            assertThat(pkg.dependencies[0].processor, `is`(nullValue()))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`("1.1"))
        }

        @Test
        @DisplayName("semver-min and semver-max")
        fun semverMinMax() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency package="http://www.example.com/pkg1" semver-min="1.1" semver-max="1.2"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg?.data, `is`("http://www.example.com/pkg1"))
            assertThat(pkg.dependencies[0].processor, `is`(nullValue()))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`("1.1"))
            assertThat(pkg.dependencies[0].semverMax, `is`("1.2"))
        }
    }

    @Nested
    @DisplayName("processor dependencies")
    internal inner class ProcessorDependencies {
        @Test
        @DisplayName("versions")
        fun versions() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency processor="http://saxon.sf.net/he" versions="9.8.11 9.8.12"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg, `is`(nullValue()))
            assertThat(pkg.dependencies[0].processor, `is`("http://saxon.sf.net/he"))
            assertThat(pkg.dependencies[0].versions, `is`(listOf("9.8.11", "9.8.12")))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver")
        fun semver() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency processor="http://saxon.sf.net/he" semver="9.8"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg, `is`(nullValue()))
            assertThat(pkg.dependencies[0].processor, `is`("http://saxon.sf.net/he"))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`("9.8"))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver-min")
        fun semverMin() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency processor="http://saxon.sf.net/he" semver-min="9.8"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg, `is`(nullValue()))
            assertThat(pkg.dependencies[0].processor, `is`("http://saxon.sf.net/he"))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`("9.8"))
            assertThat(pkg.dependencies[0].semverMax, `is`(nullValue()))
        }

        @Test
        @DisplayName("semver-max")
        fun semverMax() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency processor="http://saxon.sf.net/he" semver-max="9.8"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg, `is`(nullValue()))
            assertThat(pkg.dependencies[0].processor, `is`("http://saxon.sf.net/he"))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMax, `is`("9.8"))
        }

        @Test
        @DisplayName("semver-min and semver-max")
        fun semverMinMax() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <dependency processor="http://saxon.sf.net/he" semver-min="9.8" semver-max="9.9"/>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.dependencies.size, `is`(1))

            assertThat(pkg.dependencies[0].pkg, `is`(nullValue()))
            assertThat(pkg.dependencies[0].processor, `is`("http://saxon.sf.net/he"))
            assertThat(pkg.dependencies[0].versions, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semver, `is`(nullValue()))
            assertThat(pkg.dependencies[0].semverMin, `is`("9.8"))
            assertThat(pkg.dependencies[0].semverMax, `is`("9.9"))
        }
    }

    @Nested
    @DisplayName("XSLT components")
    internal inner class XsltComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xslt><namespace>http://www.example.com/import</namespace><file>test.xsl</file></xslt>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XSLT))
            assertThat(pkg.components[0].importUri?.data, `is`(nullValue()))
            assertThat(pkg.components[0].file, `is`("test.xsl"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xslt><import-uri>http://www.example.com/import</import-uri><file>test.xsl</file></xslt>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XSLT))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xsl"))
        }
    }

    @Nested
    @DisplayName("XQuery components")
    internal inner class XQueryComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xquery><namespace>http://www.example.com/import</namespace><file>test.xqy</file></xquery>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XQuery))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xqy"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xquery><import-uri>http://www.example.com/import</import-uri><file>test.xqy</file></xquery>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XQuery))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xqy"))
        }
    }

    @Nested
    @DisplayName("XProc components")
    internal inner class XProcComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xproc><namespace>http://www.example.com/import</namespace><file>test.xpl</file></xproc>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XProc))
            assertThat(pkg.components[0].importUri?.data, `is`(nullValue()))
            assertThat(pkg.components[0].file, `is`("test.xpl"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xproc><import-uri>http://www.example.com/import</import-uri><file>test.xpl</file></xproc>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XProc))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xpl"))
        }
    }

    @Nested
    @DisplayName("XMLSchema components")
    internal inner class XMLSchemaComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xsd><namespace>http://www.example.com/import</namespace><file>test.xsd</file></xsd>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XMLSchema))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xsd"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <xsd><import-uri>http://www.example.com/import</import-uri><file>test.xsd</file></xsd>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            assertThat(pkg.components[0].moduleType, `is`(XdmModuleType.XMLSchema))
            assertThat(pkg.components[0].importUri?.data, `is`("http://www.example.com/import"))
            assertThat(pkg.components[0].file, `is`("test.xsd"))
        }
    }
}
