/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighterColors
import javax.swing.Icon

class XQueryColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = XQuerySyntaxHighlighter()

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

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = XQuerySyntaxHighlighterColors.DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "XQuery"
}
