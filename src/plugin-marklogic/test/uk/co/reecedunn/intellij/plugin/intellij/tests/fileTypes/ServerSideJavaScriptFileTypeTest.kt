/*
 * Copyright (C) 2016-2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.tests.fileTypes

import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.fileTypes.FileTypeToArrayConsumer
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.ServerSideJavaScriptFileType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Custom Language Support - Registering a File Type - Server-side JavaScript")
private class ServerSideJavaScriptFileTypeTest : ParsingTestCase<PsiFile>(".xqy", XQuery) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("factory")
    fun testFactory() {
        val consumer = FileTypeToArrayConsumer()
        ServerSideJavaScriptFileType.Factory.createFileTypes(consumer)

        assertThat(consumer.fileTypes.size, `is`(1))
        assertThat(consumer.fileMatchers.size, `is`(0))

        assertThat(consumer.fileTypes[0].first.javaClass.name, `is`(ServerSideJavaScriptFileType::class.java.name))
        assertThat(consumer.fileTypes[0].second, `is`("sjs"))
    }

    @Test
    @DisplayName("properties")
    fun testProperties() {
        assertThat(ServerSideJavaScriptFileType.name, `is`("Server-side JavaScript"))
        assertThat(ServerSideJavaScriptFileType.description, `is`("MarkLogic Server-side JavaScript"))
        assertThat(ServerSideJavaScriptFileType.defaultExtension, `is`("sjs"))
    }
}
