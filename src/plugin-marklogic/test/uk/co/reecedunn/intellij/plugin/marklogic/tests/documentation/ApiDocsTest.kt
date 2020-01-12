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
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import java.util.zip.ZipEntry

private fun String.splitXml(): List<String> = split("\r?\n".toRegex()).filter { it.isNotBlank() }

private fun <T> isListOf(vararg items: T) = `is`(listOf(*items))

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
        @Suppress("Reformat")
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

            val ref = modules[0] as XdmDocumentation
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(
                ref.summary(XdmModuleType.XQuery)?.splitXml(), isListOf(
                    "<p>Lorem ipsum dolor.</p>",
                    "<p>Sed <code>emit</code> et dolor.</p>",
                    "<p><code>import module namespace admin = \"http://marklogic.com/xdmp/admin\"",
                    "at \"/MarkLogic/admin.xqy\" ;",
                    "</code></p>"
                )
            )
            assertThat(
                ref.summary(XdmModuleType.XPath)?.splitXml(), isListOf(
                    "<p>Lorem ipsum dolor.</p>",
                    "<p>Sed <code>emit</code> et dolor.</p>",
                    "<p><code>import module namespace admin = \"http://marklogic.com/xdmp/admin\"",
                    "at \"/MarkLogic/admin.xqy\" ;",
                    "</code></p>"
                )
            )
            assertThat(
                ref.summary(XdmModuleType.JavaScript)?.splitXml(), isListOf(
                    "<p>Lorem ipsum dolor.</p>",
                    "<p>Sed <code>emit</code> et dolor.</p>",
                    "<p><code>import module namespace admin = \"http://marklogic.com/xdmp/admin\"",
                    "at \"/MarkLogic/admin.xqy\" ;",
                    "</code></p>"
                )
            )

            assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/admin"))
            assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/admin"))
            assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/js/admin"))

            assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))
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

            val ref = modules[0] as XdmDocumentation
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`("Lorem ipsum dolor."))
            assertThat(ref.summary(XdmModuleType.XPath), `is`("Lorem ipsum dolor."))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`("Lorem ipsum dolor."))

            assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/xdmp"))
            assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/xdmp"))
            assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/js/xdmp"))

            assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))
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

            val ref = modules[0] as XdmDocumentation
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`("Lorem ipsum dolor."))
            assertThat(ref.summary(XdmModuleType.XPath), `is`("Lorem ipsum dolor."))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`("Lorem ipsum dolor."))

            assertThat(ref.href(XdmModuleType.XQuery), `is`(nullValue()))
            assertThat(ref.href(XdmModuleType.XPath), `is`(nullValue()))
            assertThat(ref.href(XdmModuleType.JavaScript), `is`(nullValue()))

            assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))
        }
    }

    @Nested
    @DisplayName("functions")
    internal inner class Functions {
        @Nested
        @DisplayName("namespace mapping")
        internal inner class NamespaceMapping {
            @Test
            @DisplayName("importable module, function in the module namespace")
            fun importableModuleSameNamespace() {
                @Language("XML")
                val adminLib = """
                    <apidoc:module lib="admin" xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:summary>
                            <p><code>import module namespace admin = "http://marklogic.com/xdmp/admin"
                                                          at "/MarkLogic/admin.xqy" ;</code></p>
                        </apidoc:summary>
                        <apidoc:function name="get-database-ids" lib="admin"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("admin"))
                assertThat(ref.namespace, `is`("http://marklogic.com/xdmp/admin"))
                assertThat(ref.isBuiltin, `is`(false))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.MARKLOGIC))

                assertThat(ref.name(XdmModuleType.XPath), `is`("get-database-ids"))
                assertThat(ref.name(XdmModuleType.XQuery), `is`("get-database-ids"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("getDatabaseIds"))

                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/admin:get-database-ids"))
                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/admin:get-database-ids"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/admin.getDatabaseIds"))
            }

            @Test
            @DisplayName("importable module, funtion in a builtin namespace")
            fun importableModuleDifferentNamespace() {
                @Language("XML")
                val adminLib = """
                    <apidoc:module lib="admin" xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:summary>
                            <p><code>import module namespace admin = "http://marklogic.com/xdmp/admin"
                                                          at "/MarkLogic/admin.xqy" ;</code></p>
                        </apidoc:summary>
                        <apidoc:function name="cpu" type="builtin" lib="cntk"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("cntk"))
                assertThat(ref.namespace, `is`("http://marklogic.com/cntk"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.MARKLOGIC))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("cpu"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("cpu"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("cpu"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/cntk:cpu"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/cntk:cpu"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/cntk.cpu"))
            }

            @Test
            @DisplayName("builtin 'cntk' namespace")
            fun builtinCntk() {
                @Language("XML")
                val builtins = """
                    <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="cpu" type="builtin" lib="cntk"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/CNTKBuiltins.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("cntk"))
                assertThat(ref.namespace, `is`("http://marklogic.com/cntk"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.MARKLOGIC))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("cpu"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("cpu"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("cpu"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/cntk:cpu"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/cntk:cpu"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/cntk.cpu"))
            }

            @Test
            @DisplayName("builtin 'cts' namespace")
            fun builtinCts() {
                @Language("XML")
                val builtins = """
                    <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="train" type="builtin" lib="cts"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/ClassifierBuiltins.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("cts"))
                assertThat(ref.namespace, `is`("http://marklogic.com/cts"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.MARKLOGIC))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("train"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("train"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("train"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/cts:train"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/cts:train"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/cts.train"))
            }

            @Test
            @DisplayName("builtin 'xdmp' namespace; JavaScript name override")
            fun builtinXdmp() {
                @Language("XML")
                val builtins = """
                    <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="to-json" type="builtin" lib="xdmp">
                            <apidoc:name class="javascript">toJSON</apidoc:name>
                        </apidoc:function>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/ClassifierBuiltins.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("xdmp"))
                assertThat(ref.namespace, `is`("http://marklogic.com/xdmp"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.MARKLOGIC))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("to-json"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("to-json"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("toJSON"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/xdmp:to-json"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/xdmp:to-json"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/xdmp.toJSON"))
            }

            @Test
            @DisplayName("XQuery only")
            fun xqueryOnly() {
                @Language("XML")
                val builtins = """
                    <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="walk" type="builtin" lib="cts" class="xquery"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/Walkers.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("cts"))
                assertThat(ref.namespace, `is`("http://marklogic.com/cts"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.XPATH_OR_XQUERY))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("walk"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("walk"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("walk"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/cts:walk"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/cts:walk"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/cts.walk"))
            }

            @Test
            @DisplayName("JavaScript only")
            fun javascriptOnly() {
                @Language("XML")
                val builtins = """
                    <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="xquery-eval" type="builtin" lib="xdmp" class="javascript"/>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/Eval.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.lib, `is`("xdmp"))
                assertThat(ref.namespace, `is`("http://marklogic.com/xdmp"))
                assertThat(ref.isBuiltin, `is`(true))
                assertThat(ref.moduleTypes, `is`(XdmModuleType.JAVASCRIPT))

                assertThat(ref.name(XdmModuleType.XQuery), `is`("xquery-eval"))
                assertThat(ref.name(XdmModuleType.XPath), `is`("xquery-eval"))
                assertThat(ref.name(XdmModuleType.JavaScript), `is`("xqueryEval"))

                assertThat(ref.href(XdmModuleType.XQuery), `is`("https://docs.marklogic.com/xdmp:xquery-eval"))
                assertThat(ref.href(XdmModuleType.XPath), `is`("https://docs.marklogic.com/xdmp:xquery-eval"))
                assertThat(ref.href(XdmModuleType.JavaScript), `is`("https://docs.marklogic.com/xdmp.xqueryEval"))
            }
        }

        @Test
        @DisplayName("apidoc properties")
        fun apidocProperties() {
            @Language("XML")
            val adminLib = """
                <apidoc:module lib="admin" xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary>
                        <p>Lorem ipsum dolor.</p>
                        <p>Sed <code>emit</code> et dolor.</p>
                        <p><code>import module namespace admin = "http://marklogic.com/xdmp/admin"
              at "/MarkLogic/admin.xqy" ;
                        </code></p>
                    </apidoc:summary>
                    <apidoc:function name="get-database-ids" lib="admin" category="Admin Library"
                                     bucket="XQuery Library Modules" subcategory="database">
                        <apidoc:summary>Lorem function dolor sed emit.</apidoc:summary>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val functions = apidocs.modules[0].functions
            assertThat(functions.size, `is`(1))

            val ref = functions[0]
            assertThat(ref.category, `is`("Admin Library"))
            assertThat(ref.subcategory, `is`("database"))
            assertThat(ref.bucket, `is`("XQuery Library Modules"))
        }

        @Nested
        @DisplayName("summary")
        internal inner class Summary {
            @Test
            @DisplayName("language generic")
            fun languageGeneric() {
                @Language("XML")
                val builtins = """
                    <apidoc:module lib="cts" xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="train" type="builtin" lib="cts">
                            <apidoc:summary>Lorem function dolor sed emit.</apidoc:summary>
                        </apidoc:function>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/ClassifierBuiltins.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.notes, `is`(nullValue()))

                assertThat(ref.summary(XdmModuleType.XQuery), `is`("Lorem function dolor sed emit."))
                assertThat(ref.summary(XdmModuleType.XPath), `is`("Lorem function dolor sed emit."))
                assertThat(ref.summary(XdmModuleType.JavaScript), `is`("Lorem function dolor sed emit."))

                assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
                assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
                assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))

                assertThat(ref.operatorMapping, `is`(nullValue()))
                assertThat(ref.signatures, `is`(nullValue()))
                assertThat(ref.properties, `is`(nullValue()))
                assertThat(ref.privileges, `is`(nullValue()))
                assertThat(ref.rules, `is`(nullValue()))
                assertThat(ref.errorConditions, `is`(nullValue()))
            }

            @Test
            @DisplayName("language-specific")
            fun languageSpecific() {
                @Language("XML")
                val builtins = """
                    <apidoc:module lib="cts" xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                        <apidoc:function name="train" type="builtin" lib="cts">
                            <apidoc:summary class="xquery">1. Lorem function dolor sed emit.</apidoc:summary>
                            <apidoc:summary class="javascript">2. Lorem function dolor sed emit.</apidoc:summary>
                        </apidoc:function>
                    </apidoc:module>
                """
                val apidocs = create(
                    "MarkLogic_10_pubs/pubs/raw/apidoc/ClassifierBuiltins.xml" to builtins
                )

                val ref = apidocs.modules[0].functions[0]
                assertThat(ref.notes, `is`(nullValue()))

                assertThat(ref.summary(XdmModuleType.XQuery), `is`("1. Lorem function dolor sed emit."))
                assertThat(ref.summary(XdmModuleType.XPath), `is`("1. Lorem function dolor sed emit."))
                assertThat(ref.summary(XdmModuleType.JavaScript), `is`("2. Lorem function dolor sed emit."))

                assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
                assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
                assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))

                assertThat(ref.operatorMapping, `is`(nullValue()))
                assertThat(ref.signatures, `is`(nullValue()))
                assertThat(ref.properties, `is`(nullValue()))
                assertThat(ref.privileges, `is`(nullValue()))
                assertThat(ref.rules, `is`(nullValue()))
                assertThat(ref.errorConditions, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("usage (rules)")
        fun usage() {
            @Language("XML")
            val classifier = """
                <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:function name="train" type="builtin" lib="cts">
                        <apidoc:usage>These are the usage notes.</apidoc:usage>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/ClassifierBuiltins.xml" to classifier
            )

            val ref = apidocs.modules[0].functions[0]
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.XPath), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`(nullValue()))

            assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`(nullValue()))
            assertThat(ref.rules, `is`("These are the usage notes."))
            assertThat(ref.errorConditions, `is`(nullValue()))
        }

        @Test
        @DisplayName("examples, xquery only")
        fun examplesXQuery() {
            @Language("XML")
            val adminLib = """
                <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:function name="train" type="builtin" lib="cts" category="Classifier">
                        <apidoc:example><pre xml:space="preserve"><![CDATA[
  a < b & b > c
]]></pre></apidoc:example>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val ref = apidocs.modules[0].functions[0]
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.XPath), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`(nullValue()))

            val xqueryExamples = ref.examples(XdmModuleType.XQuery).toList()
            assertThat(xqueryExamples.size, `is`(1))
            assertThat(
                xqueryExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  a &lt; b &amp; b &gt; c",
                    "</pre></div>"
                )
            )

            val xpathExamples = ref.examples(XdmModuleType.XPath).toList()
            assertThat(xpathExamples.size, `is`(1))
            assertThat(
                xpathExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  a &lt; b &amp; b &gt; c",
                    "</pre></div>"
                )
            )

            val javascriptExamples = ref.examples(XdmModuleType.JavaScript).toList()
            assertThat(javascriptExamples.size, `is`(1))
            assertThat(
                javascriptExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  a &lt; b &amp; b &gt; c",
                    "</pre></div>"
                )
            )

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`(nullValue()))
            assertThat(ref.rules, `is`(nullValue()))
            assertThat(ref.errorConditions, `is`(nullValue()))
        }

        @Test
        @DisplayName("examples, xquery and javascript")
        fun examplesXQueryAndJavaScript() {
            @Language("XML")
            val adminLib = """
                <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:function name="version" lib="xdmp" type="builtin">
                        <apidoc:example><pre xml:space="preserve"><![CDATA[
  1. Lorem ipsum dolor.
]]></pre></apidoc:example>
                        <apidoc:example class="xquery"><pre xml:space="preserve"><![CDATA[
  2. Lorem ipsum dolor.
]]></pre></apidoc:example>
                        <apidoc:example class="javascript"><pre xml:space="preserve"><![CDATA[
  3. Lorem ipsum dolor.
]]></pre></apidoc:example>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val ref = apidocs.modules[0].functions[0]
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.XPath), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`(nullValue()))

            val xqueryExamples = ref.examples(XdmModuleType.XQuery).toList()
            assertThat(xqueryExamples.size, `is`(2))
            assertThat(
                xqueryExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  1. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )
            assertThat(
                xqueryExamples[1].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  2. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )

            val xpathExamples = ref.examples(XdmModuleType.XPath).toList()
            assertThat(xpathExamples.size, `is`(2))
            assertThat(
                xpathExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  1. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )
            assertThat(
                xpathExamples[1].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  2. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )

            val javascriptExamples = ref.examples(XdmModuleType.JavaScript).toList()
            assertThat(javascriptExamples.size, `is`(2))
            assertThat(
                javascriptExamples[0].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  1. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )
            assertThat(
                javascriptExamples[1].splitXml(), isListOf(
                    "<div class=\"example\"><pre xml:space=\"preserve\">",
                    "  3. Lorem ipsum dolor.",
                    "</pre></div>"
                )
            )

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`(nullValue()))
            assertThat(ref.rules, `is`(nullValue()))
            assertThat(ref.errorConditions, `is`(nullValue()))
        }

        @Test
        @DisplayName("privileges")
        fun privileges() {
            @Language("XML")
            val adminLib = """
                <apidoc:module xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:function name="version" lib="xdmp" type="builtin">
                        <apidoc:privilege>MarkLogic privileges are documented here.</apidoc:privilege>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val ref = apidocs.modules[0].functions[0]
            assertThat(ref.notes, `is`(nullValue()))

            assertThat(ref.summary(XdmModuleType.XQuery), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.XPath), `is`(nullValue()))
            assertThat(ref.summary(XdmModuleType.JavaScript), `is`(nullValue()))

            assertThat(ref.examples(XdmModuleType.XQuery).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.XPath).count(), `is`(0))
            assertThat(ref.examples(XdmModuleType.JavaScript).count(), `is`(0))

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`("MarkLogic privileges are documented here."))
            assertThat(ref.rules, `is`(nullValue()))
            assertThat(ref.errorConditions, `is`(nullValue()))
        }

        @Test
        @DisplayName("stub (semantics-stub.xml) ; null localName")
        fun stub() {
            @Language("XML")
            val stub = """
                <apidoc:module name="semantics-lib" category="Semantics Library" bucket="XQuery Library Modules"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary><p>Lorem ipsum dolor.</p></apidoc:summary>
                    <apidoc:function name="" lib="sem" category="Semantics Library" bucket="XQuery Library Modules">
                        <apidoc:summary/>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/semantics-stub.xml" to stub
            )

            val functions = apidocs.modules[0].functions
            assertThat(functions.size, `is`(0)) // Function without a name.
        }
    }
}
