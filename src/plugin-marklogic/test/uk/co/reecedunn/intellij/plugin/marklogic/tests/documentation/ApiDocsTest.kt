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
import org.hamcrest.CoreMatchers.nullValue
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.marklogic.documentation.ApiDocs
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationReference
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
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

    @Nested
    @DisplayName("modules")
    internal inner class Modules {
        @Test
        @DisplayName("importable module ; multi-paragraph summary")
        fun importableModule() {
            @Language("XML")
            val adminLib = """
                <apidoc:module name="AdminModule" category="Admin Library" lib="admin" bucket="XQuery Library Modules"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary>
                        <p>Lorem ipsum dolor.</p>
                        <p>Sed <code>emit</code> et dolor.</p>
                        <p><code>import module namespace admin = "http://marklogic.com/xdmp/admin"
              at "/MarkLogic/admin.xqy" ;
                        </code></p>
                    </apidoc:summary>
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
            assertThat(modules[0].lib, `is`("admin"))
            assertThat(modules[0].bucket, `is`("XQuery Library Modules"))
            assertThat(modules[0].namespaceUri, `is`("http://marklogic.com/xdmp/admin"))
            assertThat(modules[0].locationUri, `is`("/MarkLogic/admin.xqy"))

            val ref = modules[0] as XdmDocumentationReference
            assertThat(ref.href, `is`("https://docs.marklogic.com/admin"))
            assertThat(
                ref.documentation.split("\r?\n[ \t]+".toRegex()),
                `is`(
                    listOf(
                        "",
                        "<p>Lorem ipsum dolor.</p>",
                        "<p>Sed <code>emit</code> et dolor.</p>",
                        "<p><code>import module namespace admin = \"http://marklogic.com/xdmp/admin\"",
                        "at \"/MarkLogic/admin.xqy\" ;",
                        "</code></p>",
                        ""
                    )
                )
            )
            assertThat(ref.summary, `is`("<p>Lorem ipsum dolor.</p>"))
        }

        @Test
        @DisplayName("builtin module ; single-paragraph summary")
        fun builtinModule() {
            @Language("XML")
            val adminBuiltins = """
                <apidoc:module name="AdminBuiltins" category="AdminBuiltins" lib="xdmp"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary>Lorem ipsum dolor.</apidoc:summary>
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
            assertThat(modules[0].lib, `is`("xdmp"))
            assertThat(modules[0].bucket, `is`(nullValue()))
            assertThat(modules[0].namespaceUri, `is`(nullValue()))
            assertThat(modules[0].locationUri, `is`(nullValue()))

            val ref = modules[0] as XdmDocumentationReference
            assertThat(ref.href, `is`("https://docs.marklogic.com/xdmp"))
            assertThat(ref.documentation, `is`("Lorem ipsum dolor."))
            assertThat(ref.summary, `is`("Lorem ipsum dolor."))
        }

        @Test
        @DisplayName("builtin module ; no lib attribute")
        fun builtinModuleNoLibAttribute() {
            @Language("XML")
            val crypt = """
                <apidoc:module name="Crypt" category="Crypt"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary>Lorem ipsum dolor.</apidoc:summary>
                    <apidoc:function name="crypt" type="builtin" lib="xdmp" category="Extension" subcategory="Extension"/>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/Crypt.xml" to crypt
            )

            val modules = apidocs.modules
            assertThat(modules.size, `is`(1))

            assertThat(modules[0].name, `is`("Crypt"))
            assertThat(modules[0].category, `is`("Crypt"))
            assertThat(modules[0].lib, `is`(nullValue()))
            assertThat(modules[0].bucket, `is`(nullValue()))
            assertThat(modules[0].namespaceUri, `is`(nullValue()))
            assertThat(modules[0].locationUri, `is`(nullValue()))

            val ref = modules[0] as XdmDocumentationReference
            assertThat(ref.href, `is`(nullValue()))
            assertThat(ref.documentation, `is`("Lorem ipsum dolor."))
            assertThat(ref.summary, `is`("Lorem ipsum dolor."))
        }
    }

    @Nested
    @DisplayName("functions")
    internal inner class Functions {
        @Test
        @DisplayName("importable")
        fun importable() {
            @Language("XML")
            val adminLib = """
                <apidoc:module name="AdminModule" category="Admin Library" lib="admin" bucket="XQuery Library Modules"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary>Lorem ipsum dolor.</apidoc:summary>
                    <apidoc:function name="get-database-ids" lib="admin" category="Admin Library"
                                     bucket="XQuery Library Modules" subcategory="database"/>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val functions = apidocs.modules[0].functions
            assertThat(functions.size, `is`(1))

            val qname = functions[0] as XsQNameValue
            assertThat(qname.prefix?.data, `is`("admin"))
            assertThat(qname.localName?.data, `is`("get-database-ids"))
            assertThat(qname.namespace, `is`(nullValue()))
            assertThat(qname.isLexicalQName, `is`(true))
            assertThat(qname.element, `is`(nullValue()))
        }
    }
}
