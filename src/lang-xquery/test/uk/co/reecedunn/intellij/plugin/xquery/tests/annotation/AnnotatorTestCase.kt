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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotation

import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.extensions.PluginId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider
import uk.co.reecedunn.intellij.plugin.core.tests.parser.AnnotatorTestCase
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProviderBean
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProviderBean
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase :
    AnnotatorTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()),
    PluginDescriptorProvider {

    override val pluginDescriptor: PluginDescriptor
        get() = DefaultPluginDescriptor(pluginId, this::class.java.classLoader)

    @BeforeAll
    override fun setUp() {
        super.setUp()
        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        registerExtensionPoint(XpmVariableProvider.EP_NAME, XpmVariableProviderBean::class.java)
        registerVariableProvider(XQueryVariableProvider, "INSTANCE")

        registerExtensionPoint(XpmNamespaceProvider.EP_NAME, XpmNamespaceProviderBean::class.java)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Suppress("UsePropertyAccessSyntax")
    protected fun registerVariableProvider(provider: XpmVariableProvider, fieldName: String) {
        val bean = XpmVariableProviderBean()
        bean.implementationClass = provider.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(pluginDescriptor)
        registerExtension(XpmVariableProvider.EP_NAME, bean)
    }
}
