/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.intellij.execution.configurations.type.XPathConfigurationType
import uk.co.reecedunn.intellij.plugin.intellij.lang.Turtle
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath

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
    fun defaultValues() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        assertThat(settings.processorId, `is`(nullValue()))
        assertThat(settings.rdfOutputFormat, `is`(nullValue()))
        assertThat(settings.updating, `is`(false))
        assertThat(settings.server, `is`(nullValue()))
        assertThat(settings.database, `is`(nullValue()))
        assertThat(settings.modulePath, `is`(nullValue()))
        assertThat(settings.scriptFilePath, `is`(nullValue()))

        val state = settings.state!!
        assertThat(state.processorId, `is`(nullValue()))
        assertThat(state.rdfOutputFormat, `is`(nullValue()))
        assertThat(state.updating, `is`(false))
        assertThat(state.server, `is`(nullValue()))
        assertThat(state.database, `is`(nullValue()))
        assertThat(state.modulePath, `is`(nullValue()))
        assertThat(state.scriptFile, `is`(nullValue()))
    }

    @Test
    @DisplayName("setting: processor ID")
    fun processorId() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.processorId = 1
        assertThat(settings.processorId, `is`(1))
    }

    @Test
    @DisplayName("setting: RDF output format")
    fun rdfOutputFormat() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.rdfOutputFormat = Turtle
        assertThat(settings.rdfOutputFormat, `is`(Turtle))
    }

    @Test
    @DisplayName("setting: updating")
    fun updating() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.updating = true
        assertThat(settings.updating, `is`(true))
    }

    @Test
    @DisplayName("setting: server")
    fun server() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.server = "test-server"
        assertThat(settings.server, `is`("test-server"))
    }

    @Test
    @DisplayName("setting: database")
    fun database() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.database = "test-database"
        assertThat(settings.database, `is`("test-database"))
    }

    @Test
    @DisplayName("setting: module path")
    fun modulePath() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.modulePath = "/test/path"
        assertThat(settings.modulePath, `is`("/test/path"))
    }

    @Test
    @DisplayName("setting: script file")
    fun scriptFile() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.scriptFilePath = "/test/script.xqy"
        assertThat(settings.scriptFilePath, `is`("/test/script.xqy"))
    }

    @Test
    @DisplayName("state; get")
    fun getState() {
        val factory = XPathConfigurationType().configurationFactories[0]
        val settings = factory.createTemplateConfiguration(myProject) as QueryProcessorRunConfiguration
        assertThat(settings.language, `is`(XPath))

        settings.processorId = 1
        settings.rdfOutputFormat = Turtle
        settings.updating = true
        settings.server = "test-server"
        settings.database = "test-database"
        settings.modulePath = "/test/path"
        settings.scriptFilePath = "/test/script.xqy"

        val state = settings.state!!
        assertThat(state.processorId, `is`(1))
        assertThat(state.rdfOutputFormat, `is`("text/turtle"))
        assertThat(state.updating, `is`(true))
        assertThat(state.server, `is`("test-server"))
        assertThat(state.database, `is`("test-database"))
        assertThat(state.modulePath, `is`("/test/path"))
        assertThat(state.scriptFile, `is`("/test/script.xqy"))
    }
}
