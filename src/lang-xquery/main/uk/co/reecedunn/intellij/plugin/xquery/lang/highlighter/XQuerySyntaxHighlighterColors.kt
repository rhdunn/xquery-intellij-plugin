/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

object XQuerySyntaxHighlighterColors {
    // region Syntax Highlighting (Lexical Tokens)

    val BAD_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_BAD_CHARACTER", XPathSyntaxHighlighterColors.BAD_CHARACTER
    )

    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_COMMENT", XPathSyntaxHighlighterColors.COMMENT
    )

    val ENTITY_REFERENCE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_ENTITY_REFERENCE", XmlHighlighterColors.XML_ENTITY_REFERENCE
    )

    val ESCAPED_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_ESCAPED_CHARACTER", XPathSyntaxHighlighterColors.ESCAPED_CHARACTER
    )

    val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_IDENTIFIER", XPathSyntaxHighlighterColors.IDENTIFIER
    )

    val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_KEYWORD", XPathSyntaxHighlighterColors.KEYWORD
    )

    val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_NUMBER", XPathSyntaxHighlighterColors.NUMBER
    )

    val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_STRING", XPathSyntaxHighlighterColors.STRING
    )

    val XQDOC_MARKUP: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XQDOC_MARKUP", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP
    )

    val XQDOC_TAG: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XQDOC_TAG", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG
    )

    val XQDOC_TAG_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XQDOC_TAG_VALUE", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE
    )

    val XML_PI_TAG: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_PI_TAG", XmlHighlighterColors.XML_PROLOGUE
    )

    val XML_ATTRIBUTE_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_ATTRIBUTE_NAME", XmlHighlighterColors.XML_ATTRIBUTE_NAME
    )

    val XML_ATTRIBUTE_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_ATTRIBUTE_VALUE", XmlHighlighterColors.XML_ATTRIBUTE_VALUE
    )

    val XML_ENTITY_REFERENCE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_ENTITY_REFERENCE", DefaultLanguageHighlighterColors.MARKUP_ENTITY
    )

    val XML_ESCAPED_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    )

    val XML_TAG: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_TAG", XmlHighlighterColors.XML_TAG
    )

    val XML_TAG_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_XML_TAG_NAME", XmlHighlighterColors.XML_TAG_NAME
    )

    // endregion
    // region Semantic Highlighting (Usage and Reference Types)

    val ANNOTATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_ANNOTATION", DefaultLanguageHighlighterColors.METADATA
    )

    val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_ATTRIBUTE", XPathSyntaxHighlighterColors.ATTRIBUTE
    )

    val DECIMAL_FORMAT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_DECIMAL_FORMAT", IDENTIFIER
    )

    val ELEMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_ELEMENT", XPathSyntaxHighlighterColors.ELEMENT
    )

    val FUNCTION_CALL: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_FUNCTION_CALL", XPathSyntaxHighlighterColors.FUNCTION_CALL
    )

    val FUNCTION_DECL: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_FUNCTION_DECL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    )

    val MAP_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_MAP_KEY", XPathSyntaxHighlighterColors.MAP_KEY
    )

    val NS_PREFIX: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_NS_PREFIX", XPathSyntaxHighlighterColors.NS_PREFIX
    )

    val OPTION: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_OPTION", IDENTIFIER
    )

    val PARAMETER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_PARAMETER", XPathSyntaxHighlighterColors.PARAMETER
    )

    val PRAGMA: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_PRAGMA", XPathSyntaxHighlighterColors.PRAGMA
    )

    val PROCESSING_INSTRUCTION: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_PROCESSING_INSTRUCTION", XPathSyntaxHighlighterColors.PROCESSING_INSTRUCTION
    )

    val TYPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_TYPE", XPathSyntaxHighlighterColors.TYPE
    )

    val VARIABLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "XQUERY_VARIABLE", XPathSyntaxHighlighterColors.VARIABLE
    )

    // endregion
    // region Descriptors

    @Suppress("Reformat")
    val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.annotation"), ANNOTATION),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.attribute"), ATTRIBUTE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.bad.character"), BAD_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.comment"), COMMENT),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.decimal-format"), DECIMAL_FORMAT),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.element"), ELEMENT),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.entity.reference"), ENTITY_REFERENCE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.escaped.character"), ESCAPED_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.function-call"), FUNCTION_CALL),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.function-declaration"), FUNCTION_DECL),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.identifier"), IDENTIFIER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.keyword"), KEYWORD),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.map-key"), MAP_KEY),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.ns-prefix"), NS_PREFIX),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.number"), NUMBER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.option"), OPTION),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.parameter"), PARAMETER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.pragma"), PRAGMA),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.processing-instruction"), PROCESSING_INSTRUCTION),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.string"), STRING),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.type"), TYPE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.variable"), VARIABLE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.name"), XML_ATTRIBUTE_NAME),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.attribute.value"), XML_ATTRIBUTE_VALUE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.entity.reference"), XML_ENTITY_REFERENCE),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.escaped.character"), XML_ESCAPED_CHARACTER),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.pi.tag"), XML_PI_TAG),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag"), XML_TAG),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xml.tag.name"), XML_TAG_NAME),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.markup"), XQDOC_MARKUP),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag"), XQDOC_TAG),
        AttributesDescriptor(XQueryBundle.message("xquery.settings.colors.xqdoc.tag-value"), XQDOC_TAG_VALUE)
    )

    val ADDITIONAL_DESCRIPTORS: Map<String, TextAttributesKey> = mapOf(
        "attribute" to ATTRIBUTE,
        "decimal-format" to DECIMAL_FORMAT,
        "element" to ELEMENT,
        "function-call" to FUNCTION_CALL,
        "function-decl" to FUNCTION_DECL,
        "map-key" to MAP_KEY,
        "nsprefix" to NS_PREFIX,
        "option" to OPTION,
        "parameter" to PARAMETER,
        "pragma" to PRAGMA,
        "processing-instruction" to PROCESSING_INSTRUCTION,
        "type" to TYPE,
        "variable" to VARIABLE
    )

    // endregion
}
