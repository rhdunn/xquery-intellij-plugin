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

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ResourceVirtualFileSystem extends VirtualFileSystem {
    private static ResourceVirtualFileSystem INSTANCE = new ResourceVirtualFileSystem();
    private static String PROTOCOL = "res";

    public static ResourceVirtualFileSystem getInstance() {
        return INSTANCE;
    }

    private ResourceVirtualFileSystem() {
    }

    @NotNull
    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Nullable
    @Override
    public VirtualFile findFileByPath(@NotNull String path) {
        return ResourceVirtualFile.Companion.create(ResourceVirtualFileSystem.class, path);
    }

    @Override
    public void refresh(boolean asynchronous) {
    }

    @Nullable
    @Override
    public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return findFileByPath(path);
    }

    @Override
    public void addVirtualFileListener(@NotNull VirtualFileListener listener) {
    }

    @Override
    public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {
    }

    @Override
    protected void deleteFile(Object requestor, @NotNull VirtualFile vFile) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void moveFile(Object requestor, @NotNull VirtualFile vFile, @NotNull VirtualFile newParent) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void renameFile(Object requestor, @NotNull VirtualFile vFile, @NotNull String newName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    protected VirtualFile createChildFile(Object requestor, @NotNull VirtualFile vDir, @NotNull String fileName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    protected VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile vDir, @NotNull String dirName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    protected VirtualFile copyFile(Object requestor, @NotNull VirtualFile virtualFile, @NotNull VirtualFile newParent, @NotNull String copyName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
