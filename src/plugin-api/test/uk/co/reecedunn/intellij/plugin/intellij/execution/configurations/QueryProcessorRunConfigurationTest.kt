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
    }
}
