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
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter;
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

import javax.swing.*;
import java.util.Map;

public class ColorSettingsPage implements com.intellij.openapi.options.colors.ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.bad.character"), SyntaxHighlighter.BAD_CHARACTER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.comment"), SyntaxHighlighter.COMMENT),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.entity.reference"), SyntaxHighlighter.ENTITY_REFERENCE),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.escaped.character"), SyntaxHighlighter.ESCAPED_CHARACTER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.identifier"), SyntaxHighlighter.IDENTIFIER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.keyword"), SyntaxHighlighter.KEYWORD),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.annotation"), SyntaxHighlighter.ANNOTATION),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.number"), SyntaxHighlighter.NUMBER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.string"), SyntaxHighlighter.STRING),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag"), SyntaxHighlighter.XML_TAG),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag.name"), SyntaxHighlighter.XML_TAG_NAME),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.name"), SyntaxHighlighter.XML_ATTRIBUTE_NAME),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.value"), SyntaxHighlighter.XML_ATTRIBUTE_VALUE),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.entity.reference"), SyntaxHighlighter.XML_ENTITY_REFERENCE),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.escaped.character"), SyntaxHighlighter.XML_ESCAPED_CHARACTER),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag"), SyntaxHighlighter.XQDOC_TAG),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.markup"), SyntaxHighlighter.XQDOC_MARKUP)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new SyntaxHighlighter();
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String getDemoText() {
        return
            "(: Comment :)\n" +
            "xquery version \"1.0\";\n" +
            "(:~ Documentation <code>Markup</code>\n" +
            " : @return A value.\n" +
            " :)\n" +
            "declare updating function update() external;\n" +
            "let $_ := (1234, \"One \"\" Two &quot; Three\", value)\n" +
            "return <test comment=\"One \"\" Two &quot; Three\">Lorem ipsum dolor.</test>\n" +
            "~~~";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public String getDisplayName() {
        return "XQuery";
    }
}
