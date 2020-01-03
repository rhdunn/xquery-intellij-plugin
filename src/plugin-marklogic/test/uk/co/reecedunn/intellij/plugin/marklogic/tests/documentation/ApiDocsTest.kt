/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.documentation

import org.hamcrest.CoreMatchers.`is`
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.marklogic.documentation.ApiDocs
import java.util.zip.ZipEntry

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Documentation - MarkLogic API Documentation")
private class ApiDocsTest {
    private fun create(vararg files: Pair<String, String>): ApiDocs {
        val zip = files.asSequence().map { (name, value) ->
            ZipEntry(name) to value.toByteArray()
        }.toZipByteArray()
        return ApiDocs.create(zip)
    }

    @Test
    @DisplayName("importable module")
    fun importableModule() {
        @Language("XML")
        val adminLib = """
            <apidoc:module name="AdminModule" category="Admin Library" lib="admin" bucket="XQuery Library Modules"
                           xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                <apidoc:summary><p>Lorem ipsum dolor.</p></apidoc:summary>
                <apidoc:function name="get-database-ids" lib="admin"/>
            </apidoc:module>
        """
        val apidocs = create(
            "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
        )

        val modules = apidocs.modules
        assertThat(modules.size, `is`(1))

        assertThat(modules[0].name, `is`("AdminModule"))
        assertThat(modules[0].category, `is`("Admin Library"))
    }

    @Test
    @DisplayName("builtin module")
    fun builtinModule() {
        @Language("XML")
        val adminBuiltins = """
            <apidoc:module name="AdminBuiltins" category="AdminBuiltins" lib="xdmp"
                           xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                <apidoc:function name="database-journal-archive-purge" type="builtin" lib="xdmp" category="AdminBuiltins"/>
            </apidoc:module>
        """
        val apidocs = create(
            "MarkLogic_10_pubs/pubs/raw/apidoc/AdminBuiltins.xml" to adminBuiltins
        )

        val modules = apidocs.modules
        assertThat(modules.size, `is`(1))

        assertThat(modules[0].name, `is`("AdminBuiltins"))
        assertThat(modules[0].category, `is`("AdminBuiltins"))
    }
}
