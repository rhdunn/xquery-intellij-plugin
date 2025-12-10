// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.xml

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.xml.BackendXmlElementFactory
import com.intellij.lang.xml.BasicXmlElementFactory
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.xml.XmlExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresXmlParser() {
    XmlASTFactory().registerExtension(project, XMLLanguage.INSTANCE)
    XMLParserDefinition().registerExtension(project)
    XmlFileType.INSTANCE.registerFileType()

    app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)

    app.registerService(BasicXmlElementFactory::class.java, BackendXmlElementFactory())
}
