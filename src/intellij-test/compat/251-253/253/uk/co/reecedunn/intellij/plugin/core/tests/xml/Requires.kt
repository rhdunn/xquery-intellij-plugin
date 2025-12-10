// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.xml

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.lang.xml.XmlSyntaxDefinitionExtension
import com.intellij.platform.syntax.psi.ElementTypeConverter
import com.intellij.platform.syntax.psi.ElementTypeConverterFactory
import com.intellij.platform.syntax.psi.ElementTypeConverters
import com.intellij.platform.syntax.psi.LanguageSyntaxDefinitions
import com.intellij.psi.xml.xmlElementTypeConverter
import com.intellij.xml.XmlExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.syntax.requiresPsiSyntaxBuilderFactory
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresXmlParser() {
    XmlASTFactory().registerExtension(project, XMLLanguage.INSTANCE)
    XMLParserDefinition().registerExtension(project)
    XmlFileType.INSTANCE.registerFileType()

    app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)

    requiresPsiSyntaxBuilderFactory()

    ElementTypeConverters.instance.addExplicitExtension(XMLLanguage.INSTANCE, object : ElementTypeConverterFactory {
        override fun getElementTypeConverter(): ElementTypeConverter = xmlElementTypeConverter
    })

    LanguageSyntaxDefinitions.INSTANCE.addExplicitExtension(XMLLanguage.INSTANCE, XmlSyntaxDefinitionExtension())
}
