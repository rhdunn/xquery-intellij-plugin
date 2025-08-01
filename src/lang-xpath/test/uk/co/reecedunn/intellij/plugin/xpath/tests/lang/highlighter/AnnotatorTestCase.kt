// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.highlighter

import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath as XPathNode
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition

abstract class AnnotatorTestCase : ParsingTestCase<XPathNode>("xqy", XPathParserDefinition()) {
    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPath)
    }
}
