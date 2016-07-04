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
package uk.co.reecedunn.intellij.plugin.xquery.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;

import javax.swing.*;
import java.util.Map;

public class ColorSettingsPage implements com.intellij.openapi.options.colors.ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.bad.character"), SyntaxHighlighter.BAD_CHARACTER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.comment"), SyntaxHighlighter.COMMENT),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.entity.reference"), SyntaxHighlighter.ENTITY_REFERENCE),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.escaped.character"), SyntaxHighlighter.ESCAPED_CHARACTER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.identifier"), SyntaxHighlighter.IDENTIFIER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.number"), SyntaxHighlighter.NUMBER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.string"), SyntaxHighlighter.STRING),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new SyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return
            "(: Comment :)\n" +
            "(1234, \"One \"\" Two &quot; Three\", value)\n" +
            "~~~";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "XQuery";
    }
}
