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

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import javax.swing.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class XQueryFileType extends LanguageFileType {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final Icon FILETYPE_ICON = IconLoader.getIcon("/icons/xquery.png");
    private static final Icon FILETYPE_ICON_163 = IconLoader.getIcon("/icons/xquery-163.png");

    private static final XQueryLexer sEncodingLexer = new XQueryLexer();

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
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String getName() {
        return "XQuery";
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String getDescription() {
        return XQueryBundle.message("xquery.files.filetype.description");
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String getDefaultExtension() {
        return "xqy";
    }

    @Override
    public Icon getIcon() {
        if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 163) {
            return FILETYPE_ICON_163;
        }

        return FILETYPE_ICON;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean matchToken(IElementType type) {
        boolean match = sEncodingLexer.getTokenType() == type;
        sEncodingLexer.advance();
        return match;
    }

    private boolean matchWhiteSpaceOrComment(boolean required) {
        boolean matched = false;
        while (true) {
            if (sEncodingLexer.getTokenType() == XQueryTokenType.WHITE_SPACE) {
                sEncodingLexer.advance();
                matched = true;
            } else if (sEncodingLexer.getTokenType() == XQueryTokenType.COMMENT_START_TAG) {
                sEncodingLexer.advance();
                if (sEncodingLexer.getTokenType() == XQueryTokenType.COMMENT) {
                    sEncodingLexer.advance();
                }
                if (sEncodingLexer.getTokenType() == XQueryTokenType.COMMENT_END_TAG) {
                    sEncodingLexer.advance();
                }
                matched = true;
            } else {
                return matched && required;
            }
        }
    }

    private String matchString(String defaultValue) {
        if (sEncodingLexer.getTokenType() != XQueryTokenType.STRING_LITERAL_START)
            return defaultValue;
        sEncodingLexer.advance();
        if (sEncodingLexer.getTokenType() != XQueryTokenType.STRING_LITERAL_CONTENTS)
            return defaultValue;
        String match = sEncodingLexer.getTokenText();
        sEncodingLexer.advance();
        if (sEncodingLexer.getTokenType() != XQueryTokenType.STRING_LITERAL_END)
            return defaultValue;
        sEncodingLexer.advance();
        return match;
    }

    private Charset getXQueryEncoding(CharSequence content) {
        sEncodingLexer.start(content);
        matchWhiteSpaceOrComment(false);
        if (!matchToken(XQueryTokenType.K_XQUERY)) return UTF_8;
        if (!matchWhiteSpaceOrComment(true)) return UTF_8;
        if (!matchToken(XQueryTokenType.K_VERSION)) return UTF_8;
        if (!matchWhiteSpaceOrComment(true)) return UTF_8;
        if (matchString(null) == null) return UTF_8;
        if (!matchWhiteSpaceOrComment(true)) return UTF_8;
        if (!matchToken(XQueryTokenType.K_ENCODING)) return UTF_8;
        if (!matchWhiteSpaceOrComment(true)) return UTF_8;
        try {
            return Charset.forName(matchString("utf-8"));
        } catch (UnsupportedCharsetException e) {
            return UTF_8;
        }
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull final byte[] content) {
        return getXQueryEncoding(new ByteSequence(content)).name();
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile file, @NotNull CharSequence content) {
        return getXQueryEncoding(content);
    }
}
