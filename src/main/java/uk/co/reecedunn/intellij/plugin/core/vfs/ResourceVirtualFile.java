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
package uk.co.reecedunn.intellij.plugin.core.vfs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.compress.utils.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceVirtualFile extends VirtualFile {
    private ClassLoader mLoader;
    private String mResource;
    private String mParent;
    private String mName;
    private File mFile = null;

    public static ResourceVirtualFile create(Class klass, String resource) {
        return new ResourceVirtualFile(klass.getClassLoader(), resource);
    }

    public static PsiFile resolve(final String path, Project project) {
        if (path == null || !path.startsWith("res://")) {
            return null;
        }

        final String resource = path.replaceFirst("res://", "builtin/");
        VirtualFile file = create(ResourceVirtualFile.class, resource);
        return file.isValid() ? PsiManager.getInstance(project).findFile(file) : null;
    }

    private ResourceVirtualFile(ClassLoader loader, String resource) {
        mLoader = loader;
        mResource = resource;

        int idx = resource.lastIndexOf('/');
        if (idx == -1) {
            mParent = null;
            mName = resource;
        } else {
            mParent = resource.substring(0, idx);
            mName = resource.substring(idx + 1);
        }

        URL url = mLoader.getResource(resource);
        if (url == null) return;

        try {
            if (url.getProtocol().equals("file")) {
                mFile = new File(url.toURI());
            } else if (url.getProtocol().equals("jar")) {
                JarURLConnection connection = (JarURLConnection)url.openConnection();
                mFile = new File(connection.getJarFileURL().toURI());
            }
        } catch (URISyntaxException | IOException e) {
            //
        }
    }

    @NotNull
    @Override
    public String getName() {
        return mName;
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return ResourceVirtualFileSystem.getInstance();
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
        return mParent == null ? null : new ResourceVirtualFile(mLoader, mParent);
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
        InputStream input = getInputStream();
        if (input == null) {
            throw new UnsupportedOperationException();
        }
        return IOUtils.toByteArray(input);
    }

    @Override
    public long getTimeStamp() {
        return mFile.lastModified();
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return (mFile == null || mFile.isDirectory()) ? 0 : mFile.length();
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
