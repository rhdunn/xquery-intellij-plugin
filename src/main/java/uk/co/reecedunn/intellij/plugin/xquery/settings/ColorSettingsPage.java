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
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.bad.character"), SyntaxHighlighter.Companion.getBAD_CHARACTER()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.comment"), SyntaxHighlighter.Companion.getCOMMENT()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.entity.reference"), SyntaxHighlighter.Companion.getENTITY_REFERENCE()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.escaped.character"), SyntaxHighlighter.Companion.getESCAPED_CHARACTER()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.identifier"), SyntaxHighlighter.Companion.getIDENTIFIER()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.keyword"), SyntaxHighlighter.Companion.getKEYWORD()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.annotation"), SyntaxHighlighter.Companion.getANNOTATION()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.number"), SyntaxHighlighter.Companion.getNUMBER()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.string"), SyntaxHighlighter.Companion.getSTRING()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag"), SyntaxHighlighter.Companion.getXML_TAG()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag.name"), SyntaxHighlighter.Companion.getXML_TAG_NAME()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.name"), SyntaxHighlighter.Companion.getXML_ATTRIBUTE_NAME()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.value"), SyntaxHighlighter.Companion.getXML_ATTRIBUTE_VALUE()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.entity.reference"), SyntaxHighlighter.Companion.getXML_ENTITY_REFERENCE()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.escaped.character"), SyntaxHighlighter.Companion.getXML_ESCAPED_CHARACTER()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag"), SyntaxHighlighter.Companion.getXQDOC_TAG()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag-value"), SyntaxHighlighter.Companion.getXQDOC_TAG_VALUE()),
        new AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.markup"), SyntaxHighlighter.Companion.getXQDOC_MARKUP())
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
            " : @param $a parameter A.\n" +
            " : @return A value.\n" +
            " :)\n" +
            "declare updating function update($a as xs:integer) external;\n" +
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
