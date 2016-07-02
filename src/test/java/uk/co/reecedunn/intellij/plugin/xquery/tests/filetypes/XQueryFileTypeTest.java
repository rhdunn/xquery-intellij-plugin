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

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryFileTypeTest extends TestCase {
    public class MockVirtualFile extends VirtualFile {
        @NotNull
        @Override
        public String getName() {
            return null;
        }

        @NotNull
        @Override
        public VirtualFileSystem getFileSystem() {
            return null;
        }

        @NotNull
        @Override
        public String getPath() {
            return null;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public VirtualFile getParent() {
            return null;
        }

        @Override
        public VirtualFile[] getChildren() {
            return new VirtualFile[0];
        }

        @NotNull
        @Override
        public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
            return null;
        }

        @NotNull
        @Override
        public byte[] contentsToByteArray() throws IOException {
            return new byte[0];
        }

        @Override
        public long getTimeStamp() {
            return 0;
        }

        @Override
        public long getLength() {
            return 0;
        }

        @Override
        public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }
    };

    public void testDefaultEncoding() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        MockVirtualFile file = new MockVirtualFile();

        assertThat(fileType.getCharset(file, "let $_ := 123".getBytes()), is("utf-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\";".getBytes()), is("utf-8"));
        assertThat(fileType.getCharset(file, "(::)xquery version \"1.0\" encoding \"latin1\";".getBytes()), is("utf-8"));
        assertThat(fileType.getCharset(file, "(::)\nxquery version \"1.0\" encoding \"latin1\";".getBytes()), is("utf-8"));
    }

    public void testFileEncoding() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        MockVirtualFile file = new MockVirtualFile();

        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"UTF-8\";".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "xquery version \"1.0\" encoding \"latin1\";".getBytes()), is("latin1"));

        assertThat(fileType.getCharset(file, "    xquery    version    \"1.0\"    encoding    \"UTF-8\"    ;".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"UTF-8\"\r\r;".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"UTF-8\"\n\n;".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"UTF-8\"\r\n;".getBytes()), is("UTF-8"));
        assertThat(fileType.getCharset(file, "\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"UTF-8\"\t\t;".getBytes()), is("UTF-8"));
    }

    public void testDefaultEncodingFromContents() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        MockVirtualFile file = new MockVirtualFile();

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"let $_ := 123").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\";").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)xquery version \"1.0\" encoding \"latin1\";").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"(::)\nxquery version \"1.0\" encoding \"latin1\";").name(), is("UTF-8"));
    }

    public void testFileEncodingFromContents() {
        XQueryFileType fileType = XQueryFileType.INSTANCE;
        MockVirtualFile file = new MockVirtualFile();

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" encoding \"UTF-8\";").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"xquery version \"1.0\" encoding \"latin1\";").name(), is("ISO-8859-1"));

        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"    xquery    version    \"1.0\"    encoding    \"UTF-8\"    ;").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\r\rxquery\r\rversion\r\r\"1.0\"\r\rencoding\r\r\"UTF-8\"\r\r;").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\n\nxquery\n\nversion\n\n\"1.0\"\n\nencoding\n\n\"UTF-8\"\n\n;").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\r\nxquery\r\nversion\r\n\"1.0\"\r\nencoding\r\n\"UTF-8\"\r\n;").name(), is("UTF-8"));
        assertThat(fileType.extractCharsetFromFileContent(null, file, (CharSequence)"\t\txquery\t\tversion\t\t\"1.0\"\t\tencoding\t\t\"UTF-8\"\t\t;").name(), is("UTF-8"));
    }
}
