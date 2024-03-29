/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle

object XPathSyntaxHighlighterColors {
    // region Syntax Highlighting (Lexical Tokens)

    val BAD_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER
    )

    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT
    )

    val ESCAPED_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    )

    val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER
    )

    val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
    )

    val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_NUMBER", DefaultLanguageHighlighterColors.NUMBER
    )

    val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_STRING", DefaultLanguageHighlighterColors.STRING
    )

    // endregion
    // region Semantic Highlighting (Usage and Reference Types)

    val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_ATTRIBUTE", XmlHighlighterColors.XML_ATTRIBUTE_NAME
    )

    val ELEMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_ELEMENT", XmlHighlighterColors.XML_TAG_NAME
    )

    val FUNCTION_CALL: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL
    )

    val MAP_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_MAP_KEY", IDENTIFIER
    )

    val NS_PREFIX: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_NS_PREFIX", DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )

    val PARAMETER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER
    )

    val PRAGMA: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_PRAGMA", IDENTIFIER
    )

    val PROCESSING_INSTRUCTION: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_PROCESSING_INSTRUCTION", ATTRIBUTE
    )

    val TYPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME
    )

    val VARIABLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XPATH_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    )

    // endregion
    // region Descriptors

    @Suppress("Reformat")
    val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.attribute"), ATTRIBUTE),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.bad.character"), BAD_CHARACTER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.comment"), COMMENT),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.element"), ELEMENT),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.escaped.character"), ESCAPED_CHARACTER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.function-call"), FUNCTION_CALL),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.identifier"), IDENTIFIER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.keyword"), KEYWORD),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.map-key"), MAP_KEY),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.ns-prefix"), NS_PREFIX),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.number"), NUMBER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.parameter"), PARAMETER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.pragma"), PRAGMA),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.processing-instruction"), PROCESSING_INSTRUCTION),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.string"), STRING),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.type"), TYPE),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.variable"), VARIABLE)
    )

    val ADDITIONAL_DESCRIPTORS: Map<String, TextAttributesKey> = mapOf(
        "attribute" to ATTRIBUTE,
        "element" to ELEMENT,
        "function-call" to FUNCTION_CALL,
        "map-key" to MAP_KEY,
        "nsprefix" to NS_PREFIX,
        "parameter" to PARAMETER,
        "pragma" to PRAGMA,
        "processing-instruction" to PROCESSING_INSTRUCTION,
        "type" to TYPE,
        "variable" to VARIABLE
    )

    // endregion
}
