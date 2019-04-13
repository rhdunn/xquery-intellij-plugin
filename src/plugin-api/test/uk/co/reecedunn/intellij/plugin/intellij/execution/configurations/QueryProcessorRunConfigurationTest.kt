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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.*
import org.jdom.Element
import org.jdom.output.XMLOutputter
import org.junit.jupiter.api.*
import uk.co.reecedunn.compat.execution.configurations.RunConfigurationBase
import uk.co.reecedunn.compat.execution.configurations.serializeConfigurationInto
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.intellij.execution.configurations.type.XPathConfigurationType
import uk.co.reecedunn.intellij.plugin.intellij.lang.Turtle
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Processor Settings")
private class QueryProcessorRunConfigurationTest : ParsingTestCase<PsiFile>(null) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("default values")
    @Suppress("UsePropertyAccessSyntax")
    fun defaultValues() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        assertThat(settings.processorId, `is`(nullValue()))
        assertThat(settings.rdfOutputFormat, `is`(nullValue()))
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
        assertThat(state.processorId, `is`(nullValue()))
        assertThat(state.rdfOutputFormat, `is`(nullValue()))
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
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" value="1" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.processorId = 1
        assertThat(settings.processorId, `is`(1))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: RDF output format")
    fun rdfOutputFormat() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" value="text/turtle" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.rdfOutputFormat = Turtle
        assertThat(settings.rdfOutputFormat, `is`(Turtle))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: updating")
    fun updating() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="true" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.updating = true
        assertThat(settings.updating, `is`(true))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: XPath subset")
    fun xpathSubset() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XsltPattern" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.xpathSubset = XPathSubset.XsltPattern
        assertThat(settings.xpathSubset, `is`(XPathSubset.XsltPattern))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: server")
    fun server() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" value="test-server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.server = "test-server"
        assertThat(settings.server, `is`("test-server"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: database")
    fun database() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" value="test-database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.database = "test-database"
        assertThat(settings.database, `is`("test-database"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: module path")
    fun modulePath() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" value="/test/path" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.modulePath = "/test/path"
        assertThat(settings.modulePath, `is`("/test/path"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: script file source")
    fun scriptFileSource() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="ActiveEditorFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.scriptSource = QueryProcessorDataSourceType.ActiveEditorFile
        assertThat(settings.scriptSource, `is`(QueryProcessorDataSourceType.ActiveEditorFile))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: script file path")
    fun scriptFilePath() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" value="/test/script.xqy" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.scriptFilePath = "/test/script.xqy"
        assertThat(settings.scriptFilePath, `is`("/test/script.xqy"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: context item source")
    fun contextItemSource() {
        val serialized = """<configuration>
            <option name="contextItem" />
            <option name="contextItemSource" value="ActiveEditorFile" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.contextItemSource = QueryProcessorDataSourceType.ActiveEditorFile
        assertThat(settings.contextItemSource, `is`(QueryProcessorDataSourceType.ActiveEditorFile))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("setting: context item value")
    fun contextItemValue() {
        val serialized = """<configuration>
            <option name="contextItem" value="/test/input.xml" />
            <option name="contextItemSource" />
            <option name="database" />
            <option name="modulePath" />
            <option name="processorId" />
            <option name="rdfOutputFormat" />
            <option name="scriptFile" />
            <option name="scriptSource" value="LocalFile" />
            <option name="server" />
            <option name="updating" value="false" />
            <option name="xpathSubset" value="XPath" />
        </configuration>""".replace("\n[ ]*".toRegex(), "")

        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

        settings.contextItemValue = "/test/input.xml"
        assertThat(settings.contextItemValue, `is`("/test/input.xml"))

        assertThat(serialize(settings), anyOf(`is`(serialized)))
    }

    @Test
    @DisplayName("state; get")
    @Suppress("UsePropertyAccessSyntax")
    fun getState() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration

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

        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
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
        return XMLOutputter().outputString(element)
    }

    // endregion
}
