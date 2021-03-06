/*
 * Copyright (C) 2016, 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryQueries
import javax.swing.Icon

class XQueryColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = XQuerySyntaxHighlighter

    override fun getDemoText(): String = demo

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> =
        XQuerySyntaxHighlighterColors.ADDITIONAL_DESCRIPTORS

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = XQuerySyntaxHighlighterColors.DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "XQuery"

    companion object {
        @Suppress("Reformat")
        private val demo: String by lazy {
            var ret = XQueryQueries.ColorSettingsDemo
            ret = ret.replace("\r\n", "\n")
            ret = ret.replace(" json =", " <nsprefix>json</nsprefix> =")
            ret = ret.replace(" zip =", " <nsprefix>zip</nsprefix> =")
            ret = ret.replace(" fmt ", " <decimal-format>fmt</decimal-format> ")
            ret = ret.replace(" opt ", " <option>opt</option> ")
            ret = ret.replace(" \$test ", " \$<variable>test</variable> ")
            ret = ret.replace(" update(", " <function-decl>update</function-decl>(")
            ret = ret.replace("(\$a ", "(\$<parameter>a</parameter> ")
            ret = ret.replace(" xs:integer)", " <nsprefix>xs</nsprefix>:<type>integer</type>)")
            ret = ret.replace("{ \$a ", "{ \$<parameter>a</parameter> ")
            ret = ret.replace(" \$items ", " \$<variable>items</variable> ")
            ret = ret.replace("::one,", "::<element>one</element>,")
            ret = ret.replace("::two,", "::<attribute>two</attribute>,")
            ret = ret.replace("::three,", "::<nsprefix>three</nsprefix>,")
            ret = ret.replace(" data/", " <element>data</element>/")
            ret = ret.replace("@value", "@<attribute>value</attribute>")
            ret = ret.replace(" fn:true(", " <nsprefix>fn</nsprefix>:<function-call>true</function-call>(")
            ret = ret.replace("?key-name", "?<map-key>key-name</map-key>")
            ret = ret.replace("processing-instruction(test)", "processing-instruction(<processing-instruction>test</processing-instruction>)")
            ret = ret.replace(" ext ", " <pragma>ext</pragma> ")
            ret
        }
    }
}
