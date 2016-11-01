/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.filetypes;

import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NonNls;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.FileTypeFactory;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("SameParameterValue")
public class XQueryFileTypeTest extends ParsingTestCase {
    public XQueryFileTypeTest() {
        super("", ".xqy", new XQueryParserDefinition());
    }

    private LightVirtualFile createVirtualFile(@NonNls String name, String text) {
        LightVirtualFile file = new LightVirtualFile(name, myLanguage, text);
        file.setCharset(CharsetToolkit.UTF8_CHARSET);
        return file;
    }

    public void testFactory() {
        FileTypeFactory factory = new FileTypeFactory();
        FileTypeToArrayConsumer consumer = new FileTypeToArrayConsumer();
        factory.createFileTypes(consumer);

        assertThat(consumer.fileTypes.size(), is(1));
        assertThat(consumer.fileTypes.get(0).first.getClass().getName(), is(XQueryFileType.class.getName()));
        assertThat(consumer.fileTypes.get(0).second, is("xq;xqy;xquery;xql;xqm;xqws"));
        assertThat(consumer.fileMatchers.size(), is(0));
    }

    public void testProperties() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;

        assertThat(fileType.getName(), is("XQuery"));
        assertThat(fileType.getDescription(), is("XQuery Language Support"));
        assertThat(fileType.getDefaultExtension(), is("xqy"));
    }

    public void testDefaultEncoding() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.getCharset(file, "let $_ := 123".getBytes()), is("UTF-8"));

        assertThat(fileType.getCharset(file, "xquery version \"1.0\";".getBytes()), is("UTF-8"));

        assertThat(fileType.getCharset(file, "xquery version\"1.0\"encoding\"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xqwery version \"1.0\" encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery+version \"1.0\" encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery versjon \"1.0\" encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version+\"1.0\" encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version   1.0\" encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\"+encoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" enkoding \"latin1\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding+\"latin1\"".getBytes()), is("UTF-8"));
    }

    public void testFileEncoding() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"UTF-8\"".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"latin1\"".getBytes()), is("ISO-8859-1"));

        assertThat(fileType.getCharset(file, "    xquery    version    \"1.0\"    encoding    \"latin1\"".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t".getBytes()), is("ISO-8859-1"));

        assertThat(fileType.getCharset(file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"".getBytes()), is("UTF-8"));

        assertThat(fileType.getCharset(file, "(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "(::)\nxquery version \"1.0\" encoding \"latin1\"".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\n(::)xquery version \"1.0\" encoding \"latin1\"".getBytes()), is("ISO-8859-1"));
        assertThat(fileType.getCharset(file, "\n(::)\nxquery version \"1.0\" encoding \"latin1\"".getBytes()), is("ISO-8859-1"));
    }

    public void testUnsupportedFileEncoding() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"utf\"".getBytes()), is("UTF-8"));
    }

    public void testDefaultEncodingFromContents() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"let $_ := 123").name(), is("UTF-8"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\";").name(), is("UTF-8"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version\"1.0\"encoding\"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xqwery version \"1.0\" encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery+version \"1.0\" encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery versjon \"1.0\" encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version+\"1.0\" encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version   1.0\" encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\"+encoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" enkoding \"latin1\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" encoding+\"latin1\"").name(), is("UTF-8"));
    }

    public void testFileEncodingFromContents() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" encoding \"UTF-8\"").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" encoding \"latin1\"").name(), is("ISO-8859-1"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"    xquery    version    \"1.0\"    encoding    \"latin1\"").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"latin1\"\r\r").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"latin1\"\n\n").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"latin1\"\r\n").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"latin1\"\t\t").name(), is("ISO-8859-1"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)xquery(::)version(::)\"1.0\"(::)encoding(:\"latin1\"").name(), is("UTF-8"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)xquery(::)version(::)\"1.0\"(::)encoding(::)\"latin1\"").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)\nxquery version \"1.0\" encoding \"latin1\"").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)\n(:x:)\nxquery version \"1.0\" encoding \"latin1\"").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\n(::)xquery version \"1.0\" encoding \"latin1\"").name(), is("ISO-8859-1"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\n(::)\nxquery version \"1.0\" encoding \"latin1\"").name(), is("ISO-8859-1"));
    }

    public void testUnsupportedFileEncodingFromContents() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        VirtualFile file = createVirtualFile("encoding.xqy", "");

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence) "xquery version \"1.0\" encoding \"utf\"").name(), is("UTF-8"));
    }
}
