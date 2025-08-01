// Copyright (C) 2019-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.expath.tests.pkg

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.xml.XmlFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackage
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackageDtd
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackageImport
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackageResource
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import java.util.zip.ZipEntry

@Suppress("RedundantVisibilityModifier")
@DisplayName("EXPath Packaging System 9 May 2012")
class EXPathPackageDescriptorTest : ParsingTestCase<XmlFile>(XMLLanguage.INSTANCE) {
    override val pluginId: PluginId = PluginId.getId("EXPathPackageDescriptorTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XMLParserDefinition().registerExtension(project)
        XmlFileType.INSTANCE.registerFileType()
    }

    private fun pkg(xml: String, files: Sequence<Pair<ZipEntry, ByteArray>> = sequenceOf()): EXPathPackage {
        val zip = sequenceOf(
            sequenceOf(ZipEntry("expath-pkg.xml") to xml.toByteArray()),
            files
        ).flatten().toZipByteArray()
        return EXPathPackage.create(zip)
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XSLT))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.xsl"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XSLT))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xsl"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XQuery))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xqy"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XQuery))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xqy"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XProc))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.xpl"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XProc))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xpl"))
        }
    }

    @Nested
    @DisplayName("XMLSchema components")
    internal inner class XmlSchemaComponents {
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XMLSchema))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xsd"))
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

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.XMLSchema))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.xsd"))
        }
    }

    @Nested
    @DisplayName("RelaxNG components")
    internal inner class RngComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <rng><namespace>http://www.example.com/import</namespace><file>test.rng</file></rng>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.RelaxNG))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.rng"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <rng><import-uri>http://www.example.com/import</import-uri><file>test.rng</file></rng>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.RelaxNG))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.rng"))
        }
    }

    @Nested
    @DisplayName("RelaxNG compact syntax components")
    internal inner class RncComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <rnc><namespace>http://www.example.com/import</namespace><file>test.rnc</file></rnc>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.RelaxNGCompact))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.rnc"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <rnc><import-uri>http://www.example.com/import</import-uri><file>test.rnc</file></rnc>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.RelaxNGCompact))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.rnc"))
        }
    }

    @Nested
    @DisplayName("Schematron components")
    internal inner class SchematronComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <schematron><namespace>http://www.example.com/import</namespace><file>test.sch</file></schematron>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.Schematron))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.sch"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <schematron><import-uri>http://www.example.com/import</import-uri><file>test.sch</file></schematron>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.Schematron))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.sch"))
        }
    }

    @Nested
    @DisplayName("NVDL components")
    internal inner class NvdlComponents {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <nvdl><namespace>http://www.example.com/import</namespace><file>test.nvdl</file></nvdl>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.NVDL))
            assertThat(c.importUri?.data, `is`(nullValue()))
            assertThat(c.file, `is`("test.nvdl"))
        }

        @Test
        @DisplayName("import-uri")
        fun importUri() {
            @Language("XML")
            val pkg = pkg(
                """
                <package xmlns="http://expath.org/ns/pkg">
                    <nvdl><import-uri>http://www.example.com/import</import-uri><file>test.nvdl</file></nvdl>
                </package>
                """.trimIndent()
            )

            assertThat(pkg.components.size, `is`(1))

            val c = pkg.components[0] as EXPathPackageImport
            assertThat(c.moduleType, `is`(XdmModuleType.NVDL))
            assertThat(c.importUri?.data, `is`("http://www.example.com/import"))
            assertThat(c.file, `is`("test.nvdl"))
        }
    }

    @Test
    @DisplayName("DTD components")
    fun dtd() {
        @Language("XML")
        val pkg = pkg(
            """
                <package xmlns="http://expath.org/ns/pkg">
                    <dtd><public-id>lorem</public-id><system-id>ipsum</system-id><file>test.dtd</file></dtd>
                </package>
                """.trimIndent()
        )

        assertThat(pkg.components.size, `is`(1))

        val c = pkg.components[0] as EXPathPackageDtd
        assertThat(c.moduleType, `is`(XdmModuleType.DTD))
        assertThat(c.publicId, `is`("lorem"))
        assertThat(c.systemId, `is`("ipsum"))
        assertThat(c.file, `is`("test.dtd"))
    }

    @Test
    @DisplayName("resource components")
    fun resource() {
        @Language("XML")
        val pkg = pkg(
            """
                <package xmlns="http://expath.org/ns/pkg">
                    <resource><public-uri>/lorem/ipsum.txt</public-uri><file>test.txt</file></resource>
                </package>
                """.trimIndent()
        )

        assertThat(pkg.components.size, `is`(1))

        val c = pkg.components[0] as EXPathPackageResource
        assertThat(c.moduleType, `is`(XdmModuleType.Resource))
        assertThat(c.publicUri?.data, `is`("/lorem/ipsum.txt"))
        assertThat(c.file, `is`("test.txt"))
    }

    @Test
    @DisplayName("load")
    fun load() {
        @Language("XML")
        val expathPkg =
            """
            <package xmlns="http://expath.org/ns/pkg">
                <xslt><namespace>http://www.example.com/import</namespace><file>test.xsl</file></xslt>
            </package>
            """.trimIndent()
        val pkg = pkg(
            expathPkg,
            sequenceOf(
                ZipEntry("content/") to ByteArray(0),
                ZipEntry("content/test.xsl") to "lorem ipsum".toByteArray(),
                ZipEntry("content/test.xml") to "one two".toByteArray()
            )
        )

        val file = pkg.load(pkg.components[0])
        assertThat(file?.path, `is`("content/test.xsl"))
        assertThat(file?.decode(), `is`("lorem ipsum"))
    }
}
