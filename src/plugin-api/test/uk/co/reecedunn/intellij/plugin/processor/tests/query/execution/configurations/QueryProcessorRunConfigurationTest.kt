// Copyright (C) 2019-2022, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.tests.query.execution.configurations

import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.impl.serializeConfigurationInto
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.JDOMUtil
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.jdom.Element
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.QueryProcessorDataSourceType
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.QueryProcessorRunConfiguration
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.QueryProcessorRunConfigurationData
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.rdf.Turtle
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.lang.configuration.XPathConfigurationType

@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Processor Settings")
class QueryProcessorRunConfigurationTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("QueryProcessorRunConfigurationTest")

    @Test
    @DisplayName("default values")
    @Suppress("UsePropertyAccessSyntax")
    fun defaultValues() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        assertThat(settings.processorId, `is`(nullValue()))
        assertThat(settings.rdfOutputFormat, `is`(nullValue()))
        assertThat(settings.reformatResults, `is`(false))
        assertThat(settings.updating, `is`(false))
        assertThat(settings.xpathSubset, `is`(XPathSubset.XPath))
        assertThat(settings.server, `is`(nullValue()))
        assertThat(settings.database, `is`(nullValue()))
        assertThat(settings.modulePath, `is`(nullValue()))
        assertThat(settings.scriptSource, `is`(QueryProcessorDataSourceType.LocalFile))
        assertThat(settings.scriptFilePath, `is`(nullValue()))
        assertThat(settings.contextItemSource, `is`(nullValue()))
        assertThat(settings.contextItemValue, `is`(nullValue()))

        val state = settings.getState()!!
        assertThat(state.processorId, `is`(-1))
        assertThat(state.rdfOutputFormat, `is`(nullValue()))
        assertThat(state.reformatResults, `is`(false))
        assertThat(state.updating, `is`(false))
        assertThat(state.xpathSubset, `is`(XPathSubset.XPath))
        assertThat(state.server, `is`(nullValue()))
        assertThat(state.database, `is`(nullValue()))
        assertThat(state.modulePath, `is`(nullValue()))
        assertThat(state.scriptSource, `is`(QueryProcessorDataSourceType.LocalFile))
        assertThat(state.scriptFile, `is`(nullValue()))
        assertThat(state.contextItemSource, `is`(nullValue()))
        assertThat(state.contextItem, `is`(nullValue()))
    }

    @Test
    @DisplayName("setting: processor ID")
    fun processorId() {
        val serialized = """
            <configuration>
              <option name="processorId" value="1" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.processorId = 1
        assertThat(settings.processorId, `is`(1))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: RDF output format")
    fun rdfOutputFormat() {
        val serialized = """
            <configuration>
              <option name="rdfOutputFormat" value="text/turtle" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.rdfOutputFormat = Turtle
        assertThat(settings.rdfOutputFormat, `is`(Turtle))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: reformat results")
    fun reformatResults() {
        val serialized = """
            <configuration>
              <option name="reformatResults" value="true" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.reformatResults = true
        assertThat(settings.reformatResults, `is`(true))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: updating")
    fun updating() {
        val serialized = """
            <configuration>
              <option name="updating" value="true" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.updating = true
        assertThat(settings.updating, `is`(true))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: XPath subset")
    fun xpathSubset() {
        val serialized = """
            <configuration>
              <option name="xpathSubset" value="XsltPattern" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.xpathSubset = XPathSubset.XsltPattern
        assertThat(settings.xpathSubset, `is`(XPathSubset.XsltPattern))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: server")
    fun server() {
        val serialized = """
            <configuration>
              <option name="server" value="test-server" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.server = "test-server"
        assertThat(settings.server, `is`("test-server"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: database")
    fun database() {
        val serialized = """
            <configuration>
              <option name="database" value="test-database" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.database = "test-database"
        assertThat(settings.database, `is`("test-database"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: module path")
    fun modulePath() {
        val serialized = """
            <configuration>
              <option name="modulePath" value="/test/path" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.modulePath = "/test/path"
        assertThat(settings.modulePath, `is`("/test/path"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: script file source")
    fun scriptFileSource() {
        val serialized = """
            <configuration>
              <option name="scriptSource" value="ActiveEditorFile" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.scriptSource = QueryProcessorDataSourceType.ActiveEditorFile
        assertThat(settings.scriptSource, `is`(QueryProcessorDataSourceType.ActiveEditorFile))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: script file path")
    fun scriptFilePath() {
        val serialized = """
            <configuration>
              <option name="scriptFile" value="/test/script.xqy" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.scriptFilePath = "/test/script.xqy"
        assertThat(settings.scriptFilePath, `is`("/test/script.xqy"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: context item source")
    fun contextItemSource() {
        val serialized = """
            <configuration>
              <option name="contextItemSource" value="ActiveEditorFile" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.contextItemSource = QueryProcessorDataSourceType.ActiveEditorFile
        assertThat(settings.contextItemSource, `is`(QueryProcessorDataSourceType.ActiveEditorFile))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: context item value")
    fun contextItemValue() {
        val serialized = """
            <configuration>
              <option name="contextItem" value="/test/input.xml" />
            </configuration>
        """.trimIndent()

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.contextItemValue = "/test/input.xml"
        assertThat(settings.contextItemValue, `is`("/test/input.xml"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("state; get")
    @Suppress("UsePropertyAccessSyntax")
    fun getState() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration

        settings.processorId = 1
        settings.rdfOutputFormat = Turtle
        settings.updating = true
        settings.xpathSubset = XPathSubset.XsltPattern
        settings.server = "test-server"
        settings.database = "test-database"
        settings.modulePath = "/test/path"
        settings.scriptSource = QueryProcessorDataSourceType.DatabaseModule
        settings.scriptFilePath = "/test/script.xqy"
        settings.contextItemSource = QueryProcessorDataSourceType.LocalFile
        settings.contextItemValue = "/test/input.xml"

        val state = settings.getState()!!
        assertThat(state.processorId, `is`(1))
        assertThat(state.rdfOutputFormat, `is`("text/turtle"))
        assertThat(state.updating, `is`(true))
        assertThat(state.xpathSubset, `is`(XPathSubset.XsltPattern))
        assertThat(state.server, `is`("test-server"))
        assertThat(state.database, `is`("test-database"))
        assertThat(state.modulePath, `is`("/test/path"))
        assertThat(state.scriptSource, `is`(QueryProcessorDataSourceType.DatabaseModule))
        assertThat(state.scriptFile, `is`("/test/script.xqy"))
        assertThat(state.contextItemSource, `is`(QueryProcessorDataSourceType.LocalFile))
        assertThat(state.contextItem, `is`("/test/input.xml"))
    }

    @Test
    @DisplayName("state; load")
    fun loadState() {
        val factory = XPathConfigurationType().configurationFactories[0]

        val state = QueryProcessorRunConfigurationData()
        state.processorId = 1
        state.rdfOutputFormat = "text/turtle"
        state.updating = true
        state.xpathSubset = XPathSubset.XsltPattern
        state.server = "test-server"
        state.database = "test-database"
        state.modulePath = "/test/path"
        state.scriptSource = QueryProcessorDataSourceType.DatabaseModule
        state.scriptFile = "/test/script.xqy"
        state.contextItemSource = QueryProcessorDataSourceType.LocalFile
        state.contextItem = "/test/input.xml"

        val settings = factory.createTemplateConfiguration(project) as QueryProcessorRunConfiguration
        settings.loadState(state)

        assertThat(settings.processorId, `is`(1))
        assertThat(settings.rdfOutputFormat, `is`(Turtle))
        assertThat(settings.updating, `is`(true))
        assertThat(settings.xpathSubset, `is`(XPathSubset.XsltPattern))
        assertThat(settings.server, `is`("test-server"))
        assertThat(settings.database, `is`("test-database"))
        assertThat(settings.modulePath, `is`("/test/path"))
        assertThat(settings.scriptSource, `is`(QueryProcessorDataSourceType.DatabaseModule))
        assertThat(settings.scriptFilePath, `is`("/test/script.xqy"))
        assertThat(settings.contextItemSource, `is`(QueryProcessorDataSourceType.LocalFile))
        assertThat(settings.contextItemValue, `is`("/test/input.xml"))
    }

    // region Serialization Helpers

    private fun serialize(configuration: RunConfigurationBase<*>): String {
        val element = Element("configuration")
        serializeConfigurationInto(configuration, element)
        return JDOMUtil.write(element)
    }

    // endregion
}
