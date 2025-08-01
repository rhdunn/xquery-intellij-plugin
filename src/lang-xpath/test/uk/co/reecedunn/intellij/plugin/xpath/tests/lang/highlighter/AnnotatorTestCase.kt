// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.highlighter

import com.intellij.lang.LanguageASTFactory
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition

abstract class AnnotatorTestCase : ParsingTestCase<XPath>("xqy", XPathParserDefinition()) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        addExplicitExtension(
            LanguageASTFactory.INSTANCE, uk.co.reecedunn.intellij.plugin.xpath.lang.XPath, XPathASTFactory()
        )
    }
}
