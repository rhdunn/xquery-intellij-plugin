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
package uk.co.reecedunn.intellij.plugin.xquery.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

import javax.swing.*

private val DESCRIPTORS = arrayOf(
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.bad.character"), SyntaxHighlighter.BAD_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.comment"), SyntaxHighlighter.COMMENT),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.entity.reference"), SyntaxHighlighter.ENTITY_REFERENCE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.escaped.character"), SyntaxHighlighter.ESCAPED_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.identifier"), SyntaxHighlighter.IDENTIFIER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.keyword"), SyntaxHighlighter.KEYWORD),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.annotation"), SyntaxHighlighter.ANNOTATION),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.number"), SyntaxHighlighter.NUMBER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.string"), SyntaxHighlighter.STRING),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag"), SyntaxHighlighter.XML_TAG),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag.name"), SyntaxHighlighter.XML_TAG_NAME),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.name"), SyntaxHighlighter.XML_ATTRIBUTE_NAME),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.value"), SyntaxHighlighter.XML_ATTRIBUTE_VALUE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.entity.reference"), SyntaxHighlighter.XML_ENTITY_REFERENCE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.escaped.character"), SyntaxHighlighter.XML_ESCAPED_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag"), SyntaxHighlighter.XQDOC_TAG),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag-value"), SyntaxHighlighter.XQDOC_TAG_VALUE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.markup"), SyntaxHighlighter.XQDOC_MARKUP))

class ColorSettingsPage : com.intellij.openapi.options.colors.ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): com.intellij.openapi.fileTypes.SyntaxHighlighter {
        return SyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return "(: Comment :)\n" +
                "xquery version \"1.0\";\n" +
                "(:~ Documentation <code>Markup</code>\n" +
                " : @param \$a parameter A.\n" +
                " : @return A value.\n" +
                " :)\n" +
                "declare updating function update(\$a as xs:integer) external;\n" +
                "let \$_ := (1234, \"One \"\" Two &quot; Three\", value)\n" +
                "return <test comment=\"One \"\" Two &quot; Three\">Lorem ipsum dolor.</test>\n" +
                "~~~"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        return null
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "XQuery"
}
