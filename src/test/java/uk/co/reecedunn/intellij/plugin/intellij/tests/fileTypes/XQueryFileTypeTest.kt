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
package uk.co.reecedunn.intellij.plugin.intellij.tests.fileTypes

import com.intellij.lang.LanguageASTFactory
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.FileTypeFactory
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.ServerSideJavaScriptFileType
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Custom Language Support - Registering a File Type")
private class XQueryFileTypeTest : ParsingTestCase<XQueryModule>(".xqy", XQueryParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        registerApplicationService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("FileTypeFactory")
    fun testFactory() {
        val factory = FileTypeFactory()
        val consumer = FileTypeToArrayConsumer()
        factory.createFileTypes(consumer)

        assertThat(consumer.fileTypes.size, `is`(2))
        assertThat(consumer.fileMatchers.size, `is`(0))

        assertThat(consumer.fileTypes[0].first.javaClass.name, `is`(XQueryFileType::class.java.name))
        assertThat(consumer.fileTypes[0].second, `is`("xq;xqy;xquery;xql;xqm;xqu;xqws"))

        assertThat(consumer.fileTypes[1].first.javaClass.name, `is`(ServerSideJavaScriptFileType::class.java.name))
        assertThat(consumer.fileTypes[1].second, `is`("sjs"))
    }

    @Test
    @DisplayName("XQuery properties")
    fun testProperties() {
        assertThat(XQueryFileType.name, `is`("XQuery"))
        assertThat(XQueryFileType.description, `is`("XML Query Language"))
        assertThat(XQueryFileType.defaultExtension, `is`("xqy"))
    }

    @Nested
    @DisplayName("XQuery charset")
    internal inner class XQueryCharset {
        @Test
        @DisplayName("default encoding")
        fun testDefaultEncoding() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(XQueryFileType.getCharset(file, "let \$_ := 123".toByteArray()), `is`("UTF-8"))

            assertThat(XQueryFileType.getCharset(file, "xquery version \"1.0\";".toByteArray()), `is`("UTF-8"))

            assertThat(
                XQueryFileType.getCharset(file, "xquery version\"1.0\"encoding\"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xqwery version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery+version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery versjon \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery version+\"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery version   1.0\" encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(XQueryFileType.getCharset(file, "xquery version \"".toByteArray()), `is`("UTF-8"))
            assertThat(XQueryFileType.getCharset(file, "xquery version \"1.0".toByteArray()), `is`("UTF-8"))
            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\"+encoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\" enkoding \"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\" encoding+\"latin1\"".toByteArray()),
                `is`("UTF-8")
            )
        }

        @Test
        @DisplayName("from VersionDecl")
        fun testFileEncoding() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\" encoding \"UTF-8\"".toByteArray()),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("ISO-8859-1")
            )

            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "    xquery    version    \"1.0\"    encoding    \"latin1\"".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t".toByteArray()
                ), `is`("ISO-8859-1")
            )

            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"".toByteArray()
                ), `is`("UTF-8")
            )

            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(file, "(::)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(
                    file,
                    "(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()
                ), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(file, "\n(::)xquery version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.getCharset(file, "\n(::)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()),
                `is`("ISO-8859-1")
            )
        }

        @Test
        @DisplayName("unsupported/invalid VersionDecl encoding")
        fun testUnsupportedFileEncoding() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(
                XQueryFileType.getCharset(file, "xquery version \"1.0\" encoding \"utf\"".toByteArray()),
                `is`("UTF-8")
            )
        }
    }

    @Nested
    @DisplayName("XQuery charset from contents")
    internal inner class XQuery {
        @Test
        @DisplayName("default encoding")
        fun testDefaultEncodingFromContents() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(null, file, "let \$_ := 123" as CharSequence).name(),
                `is`("UTF-8")
            )

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\";" as CharSequence
                ).name(), `is`("UTF-8")
            )

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version\"1.0\"encoding\"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xqwery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery+version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery versjon \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version+\"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version   1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(null, file, "xquery version \"" as CharSequence).name(),
                `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\"+encoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\" enkoding \"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\" encoding+\"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
        }

        @Test
        @DisplayName("from VersionDecl")
        fun testFileEncodingFromContents() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\" encoding \"UTF-8\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "    xquery    version    \"1.0\"    encoding    \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"" as CharSequence
                ).name(), `is`("UTF-8")
            )

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "(::)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\n(::)xquery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "\n(::)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence
                ).name(), `is`("ISO-8859-1")
            )
        }

        @Test
        @DisplayName("unsupported/invalid VersionDecl encoding")
        fun testUnsupportedFileEncodingFromContents() {
            val file = createVirtualFile("encoding.xqy", "")

            assertThat(
                XQueryFileType.extractCharsetFromFileContent(
                    null,
                    file,
                    "xquery version \"1.0\" encoding \"utf\"" as CharSequence
                ).name(), `is`("UTF-8")
            )
        }
    }
}
