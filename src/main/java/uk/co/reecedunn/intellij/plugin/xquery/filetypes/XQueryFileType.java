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
package uk.co.reecedunn.intellij.plugin.xquery.filetypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.XQuery;

import javax.swing.*;

public class XQueryFileType extends LanguageFileType {
    private static final Icon FILETYPE_ICON = IconLoader.getIcon("/icons/xquery.png");

    public static final String EXTENSIONS = "xq;xqy;xquery";

    public static final XQueryFileType INSTANCE = new XQueryFileType();

    private XQueryFileType() {
        super(XQuery.INSTANCE);
    }

    @Override
    @NotNull
    public String getName() {
        return "XQuery";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "XQuery Language Support";
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return "xqy";
    }

    @Override
    public Icon getIcon() {
        return FILETYPE_ICON;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull final byte[] content) {
        return "utf-8";
    }
}
