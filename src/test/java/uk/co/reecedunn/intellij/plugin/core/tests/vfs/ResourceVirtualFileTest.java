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
package uk.co.reecedunn.intellij.plugin.core.tests.vfs;

import com.intellij.openapi.vfs.VirtualFile;
import junit.framework.TestCase;
import org.apache.xmlbeans.impl.common.IOUtil;
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile;
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ResourceVirtualFileTest extends TestCase {
    public String streamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        return writer.toString();
    }

    // region File System

    public void testFileSystem_CreatingFile() throws IOException {
        VirtualFile file = ResourceVirtualFile.create(ResourceVirtualFileTest.class, "tests/vfs/test.xq");
        assertThat(file.getName(), is("test.xq"));
        assertThat(file.getPath(), anyOf(endsWith("/tests/vfs/test.xq"), endsWith("\\tests\\vfs\\test.xq")));
        assertThat(file.isWritable(), is(false));
        assertThat(file.isDirectory(), is(false));
        assertThat(file.isValid(), is(true));
        assertThat(file.getLength(), is(28L));
        assertThat(file.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(streamToString(file.getInputStream()), is("xquery version \"3.0\"; true()"));
        assertThat(file.contentsToByteArray(), is("xquery version \"3.0\"; true()".getBytes()));
        assertThat(file.getModificationStamp(), is(0L));

        VirtualFile parent = file.getParent();
        assertThat(parent, is(notNullValue()));
        assertThat(parent.getName(), is("vfs"));
        assertThat(parent.getPath(), anyOf(endsWith("/tests/vfs"), endsWith("\\tests\\vfs")));
        assertThat(parent.isDirectory(), is(true));
        assertThat(parent.isValid(), is(true));
        assertThat(parent.getLength(), is(0L));
        assertThat(parent.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(parent.getModificationStamp(), is(0L));

        assertThat(parent.getParent(), is(notNullValue()));

        VirtualFile[] children = file.getChildren();
        assertThat(children, is(nullValue()));
    }

    public void testFileSystem_InvalidFilePath() throws IOException {
        VirtualFile file = ResourceVirtualFile.create(ResourceVirtualFileTest.class, "tests/vfs/test.xqy");
        assertThat(file.getName(), is("test.xqy"));
        assertThat(file.getPath(), is(""));
        assertThat(file.isWritable(), is(false));
        assertThat(file.isDirectory(), is(false));
        assertThat(file.isValid(), is(false));
        assertThat(file.getLength(), is(0L));
        assertThat(file.getInputStream(), is(nullValue()));
        assertThat(file.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(file.getModificationStamp(), is(0L));

        VirtualFile parent = file.getParent();
        assertThat(parent, is(notNullValue()));
        assertThat(parent.getName(), is("vfs"));
        assertThat(parent.getPath(), anyOf(endsWith("/tests/vfs"), endsWith("\\tests\\vfs")));
        assertThat(parent.isDirectory(), is(true));
        assertThat(parent.isValid(), is(true));
        assertThat(parent.getLength(), is(0L));
        assertThat(parent.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(parent.getModificationStamp(), is(0L));

        assertThat(parent.getParent(), is(notNullValue()));

        VirtualFile[] children = file.getChildren();
        assertThat(children, is(nullValue()));
    }

    public void testFileSystem_Directory() throws IOException {
        VirtualFile file = ResourceVirtualFile.create(ResourceVirtualFileTest.class, "tests/vfs");
        assertThat(file.getName(), is("vfs"));
        assertThat(file.getPath(), anyOf(endsWith("/tests/vfs"), endsWith("\\tests\\vfs")));
        assertThat(file.isWritable(), is(false));
        assertThat(file.isDirectory(), is(true));
        assertThat(file.isValid(), is(true));
        assertThat(file.getLength(), is(0L));
        assertThat(file.getInputStream(), is(nullValue()));
        assertThat(file.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(file.getModificationStamp(), is(0L));

        VirtualFile parent = file.getParent();
        assertThat(parent, is(notNullValue()));
        assertThat(parent.getName(), is("tests"));
        assertThat(parent.isDirectory(), is(true));
        assertThat(parent.isValid(), is(true));
        assertThat(parent.getLength(), is(0L));
        assertThat(parent.getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
        assertThat(parent.getModificationStamp(), is(0L));

        assertThat(parent.getParent(), is(nullValue()));

        VirtualFile[] children = file.getChildren();
        assertThat(children, is(notNullValue()));
        assertThat(children.length, is(1));
        assertThat(children[0].getName(), is("test.xq"));
        assertThat(children[0].getPath(), anyOf(endsWith("/tests/vfs/test.xq"), endsWith("\\tests\\vfs\\test.xq")));
        assertThat(children[0].getLength(), is(28L));
        assertThat(children[0].getFileSystem(), instanceOf(ResourceVirtualFileSystem.class));
    }

    // endregion
}
