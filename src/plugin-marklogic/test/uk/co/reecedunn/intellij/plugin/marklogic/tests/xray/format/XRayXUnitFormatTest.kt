/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.xray.format

import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.XRayTestFormat
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites

@DisplayName("XRay Unit Tests - XUnit XML Output Format")
class XRayXUnitFormatTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("XRayXUnitFormatTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    private fun parse(resource: String): TestSuites {
        val xml = res.findFileByPath(resource)?.decode()!!
        val value = QueryResult(0, xml, "document-node()", QueryResult.APPLICATION_XML)
        return XRayTestFormat.format("xunit").parse(value)!!
    }

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerServiceInstance(EditorFactory::class.java, MockEditorFactoryEx())
        app.registerServiceInstance(EncodingManager::class.java, EncodingManagerImpl())
    }

    @Test
    @DisplayName("no test suites")
    fun noTestSuites() {
        val tests = parse("xray/format/xunit/empty.xml")
        assertThat(tests.passed, `is`(0))
        assertThat(tests.failed, `is`(0))
        assertThat(tests.ignored, `is`(0))
        assertThat(tests.errors, `is`(0))
        assertThat(tests.total, `is`(0))

        assertThat(tests.testSuites.count(), `is`(0))
    }
}
