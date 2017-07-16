/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.ui;

import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class TextFieldFileAccessor implements TextComponentAccessor<JTextField> {
    @Override
    public String getText(JTextField component) {
        final String text = component.getText();
        final String url = VfsUtil.pathToUrl(text.replace(File.separatorChar, '/'));
        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null && !file.isDirectory()) {
            return file.getParent().getPresentableUrl();
        }
        return text;
    }

    @Override
    public void setText(JTextField component, @NotNull String text) {
        component.setText(text);
    }
}
