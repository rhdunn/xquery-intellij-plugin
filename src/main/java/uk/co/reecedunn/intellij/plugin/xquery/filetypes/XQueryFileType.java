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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;

import javax.swing.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XQueryFileType extends LanguageFileType {
    private class ByteSequence implements CharSequence {
        private final byte[] mData;
        private final int mOffset;
        private final int mLength;

        public ByteSequence(byte[] data) {
            this(data, 0, data.length);
        }

        public ByteSequence(byte[] data, int offset, int length) {
            mData = data;
            mOffset = offset;
            mLength = length;
        }

        @Override
        public int length() {
            return mLength;
        }

        @Override
        public char charAt(int index) {
            return (char)(mData[mOffset + index] & 0xFF);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new ByteSequence(mData, start, end - start);
        }

        @NotNull
        @Override
        public String toString() {
            return new String(mData, mOffset, mLength);
        }
    }

    private static final Icon FILETYPE_ICON = IconLoader.getIcon("/icons/xquery.png");
    private static final Pattern ENCODING_PATTERN = Pattern.compile("^[ \r\n\t]*xquery[ \r\n\t]+version[ \r\n\t]+\"[^\"]*\"[ \r\n\t]+encoding[ \r\n\t]+\"([^\"]*)\"[ \r\n\t]*;");

    // xq;xqy;xquery -- standard defined extensions
    // xql           -- XQuery Language (main) file [eXist-db]
    // xqm           -- XQuery Module file [eXist-db]
    // xqws          -- XQuery Web Service [eXist-db]
    public static final String EXTENSIONS = "xq;xqy;xquery;xql;xqm;xqws";

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
        return XQueryBundle.message("xquery.files.filetype.description");
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

    private String getXQueryEncoding(CharSequence content) {
        final Matcher matcher = ENCODING_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "utf-8";
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull final byte[] content) {
        return getXQueryEncoding(new ByteSequence(content));
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile file, @NotNull CharSequence content) {
        return Charset.forName(getXQueryEncoding(content));
    }
}
