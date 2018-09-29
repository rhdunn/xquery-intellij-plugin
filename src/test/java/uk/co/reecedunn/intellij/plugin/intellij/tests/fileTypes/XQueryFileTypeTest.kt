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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.FileTypeFactory
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class XQueryFileTypeTest : ParsingTestCase<XQueryModule>(".xqy", XQueryParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        registerApplicationService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, language!!, XQueryASTFactory())
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testFactory() {
        val factory = FileTypeFactory()
        val consumer = FileTypeToArrayConsumer()
        factory.createFileTypes(consumer)

        assertThat(consumer.fileTypes.size, `is`(1))
        assertThat(consumer.fileTypes[0].first.javaClass.name, `is`(XQueryFileType::class.java.name))
        assertThat(consumer.fileTypes[0].second, `is`("xq;xqy;xquery;xql;xqm;xqu;xqws"))
        assertThat(consumer.fileMatchers.size, `is`(0))
    }

    @Test
    fun testProperties() {
        val fileType = XQueryFileType.INSTANCE

        assertThat(fileType.name, `is`("XQuery"))
        assertThat(fileType.description, `is`("XML Query Language"))
        assertThat(fileType.defaultExtension, `is`("xqy"))
    }

    @Test
    fun testDefaultEncoding() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.getCharset(file, "let \$_ := 123".toByteArray()), `is`("UTF-8"))

        assertThat(fileType.getCharset(file, "xquery version \"1.0\";".toByteArray()), `is`("UTF-8"))

        assertThat(fileType.getCharset(file, "xquery version\"1.0\"encoding\"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xqwery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery+version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery versjon \"1.0\" encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version+\"1.0\" encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version   1.0\" encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"1.0".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"1.0\"+encoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" enkoding \"latin1\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding+\"latin1\"".toByteArray()), `is`("UTF-8"))
    }

    @Test
    fun testFileEncoding() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"UTF-8\"".toByteArray()), `is`("UTF-8"))
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("ISO-8859-1"))

        assertThat(fileType.getCharset(file, "    xquery    version    \"1.0\"    encoding    \"latin1\"".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t".toByteArray()), `is`("ISO-8859-1"))

        assertThat(fileType.getCharset(file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"".toByteArray()), `is`("UTF-8"))

        assertThat(fileType.getCharset(file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "(::)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\n(::)xquery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("ISO-8859-1"))
        assertThat(fileType.getCharset(file, "\n(::)\nxquery version \"1.0\" encoding \"latin1\"".toByteArray()), `is`("ISO-8859-1"))
    }

    @Test
    fun testUnsupportedFileEncoding() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"utf\"".toByteArray()), `is`("UTF-8"))
    }

    @Test
    fun testDefaultEncodingFromContents() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.extractCharsetFromFileContent(null, file, "let \$_ := 123" as CharSequence).name(), `is`("UTF-8"))

        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\";" as CharSequence).name(), `is`("UTF-8"))

        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version\"1.0\"encoding\"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xqwery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery+version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery versjon \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version+\"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version   1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\"+encoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\" enkoding \"latin1\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\" encoding+\"latin1\"" as CharSequence).name(), `is`("UTF-8"))
    }

    @Test
    fun testFileEncodingFromContents() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\" encoding \"UTF-8\"" as CharSequence).name(), `is`("UTF-8"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))

        assertThat(fileType.extractCharsetFromFileContent(null, file, "    xquery    version    \"1.0\"    encoding    \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t" as CharSequence).name(), `is`("ISO-8859-1"))

        assertThat(fileType.extractCharsetFromFileContent(null, file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"" as CharSequence).name(), `is`("UTF-8"))

        assertThat(fileType.extractCharsetFromFileContent(null, file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "(::)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\n(::)xquery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
        assertThat(fileType.extractCharsetFromFileContent(null, file, "\n(::)\nxquery version \"1.0\" encoding \"latin1\"" as CharSequence).name(), `is`("ISO-8859-1"))
    }

    @Test
    fun testUnsupportedFileEncodingFromContents() {
        val fileType = XQueryFileType.INSTANCE
        val file = createVirtualFile("encoding.xqy", "")

        assertThat(fileType.extractCharsetFromFileContent(null, file, "xquery version \"1.0\" encoding \"utf\"" as CharSequence).name(), `is`("UTF-8"))
    }
}
