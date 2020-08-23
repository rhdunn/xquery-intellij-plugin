/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.intellij.lexer

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import uk.co.reecedunn.intellij.plugin.xslt.intellij.resources.XsltBundle

object XsltSyntaxHighlighterColors {
    val ATTRIBUTE_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XSLT_ATTRIBUTE_VALUE", XmlHighlighterColors.XML_ATTRIBUTE_VALUE
    )

    val ESCAPED_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XSLT_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    )

    val XSLT_DIRECTIVE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XSLT_DIRECTIVE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
    )

    val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor(XsltBundle.message("xslt.settings.colors.attribute-value"), ATTRIBUTE_VALUE),
        AttributesDescriptor(XsltBundle.message("xslt.settings.colors.directive"), XSLT_DIRECTIVE),
        AttributesDescriptor(XsltBundle.message("xslt.settings.colors.escaped-character"), ESCAPED_CHARACTER)
    )
}
