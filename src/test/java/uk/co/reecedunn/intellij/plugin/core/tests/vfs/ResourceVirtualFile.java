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
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceVirtualFile extends VirtualFile {
    private ClassLoader mLoader;
    private String mResource;
    private File mFile = null;

    public ResourceVirtualFile(ClassLoader loader, String resource) {
        mLoader = loader;
        mResource = resource;
        URL url = mLoader.getResource(mResource);
        if (url != null && url.getProtocol().equals("file")) {
            try {
                mFile = new File(url.toURI());
            } catch (URISyntaxException e) {
                //
            }
        }
    }

    public ResourceVirtualFile(String resource) {
        this(ResourceVirtualFile.class.getClassLoader(), resource);
    }

    @NotNull
    @Override
    public String getName() {
        return mResource;
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getPath() {
        return mFile != null ? mFile.getPath() : "";
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return mFile != null && mFile.isDirectory();
    }

    @Override
    public boolean isValid() {
        return mFile != null;
    }

    @Override
    public VirtualFile getParent() {
        int idx = mResource.lastIndexOf('/');
        if (idx == -1) {
            return null;
        }
        return new ResourceVirtualFile(mLoader, mResource.substring(0, idx));
    }

    @Override
    public VirtualFile[] getChildren() {
        if (mFile == null) {
            return null;
        }

        String[] children = mFile.list();
        if (children == null) {
            return null;
        }

        VirtualFile[] files = new VirtualFile[children.length];
        for (int i = 0; i != children.length; ++i) {
            files[i] = new ResourceVirtualFile(mLoader, mResource + "/" + children[i]);
        }
        return files;
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTimeStamp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return isDirectory() ? null : mLoader.getResourceAsStream(mResource);
    }
}
