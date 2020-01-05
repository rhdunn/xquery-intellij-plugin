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
import uk.co.reecedunn.intellij.plugin.marklogic.documentation.ApiDocsFunction
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmFunctionDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
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
            assertThat(modules[0].namespaceUri?.data, `is`("http://marklogic.com/xdmp/admin"))
            assertThat(modules[0].namespaceUri?.context, `is`(XdmUriContext.Namespace))
            assertThat(modules[0].namespaceUri?.moduleTypes, `is`(XdmModuleType.MODULE))
            assertThat(modules[0].locationUri, `is`("/MarkLogic/admin.xqy"))

            val ref = modules[0] as XdmDocumentation
            assertThat(ref.href, `is`("https://docs.marklogic.com/admin"))
            assertThat(ref.summary?.splitXml(), isListOf(
                "<p>Lorem ipsum dolor.</p>",
                "<p>Sed <code>emit</code> et dolor.</p>",
                "<p><code>import module namespace admin = \"http://marklogic.com/xdmp/admin\"",
                "at \"/MarkLogic/admin.xqy\" ;",
                "</code></p>"
            ))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples, `is`(nullValue()))
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
            assertThat(ref.href, `is`("https://docs.marklogic.com/xdmp"))
            assertThat(ref.summary, `is`("Lorem ipsum dolor."))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples, `is`(nullValue()))
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
            assertThat(ref.href, `is`(nullValue()))
            assertThat(ref.summary, `is`("Lorem ipsum dolor."))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples, `is`(nullValue()))
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

            assertThat(functions[0].isBuiltin, `is`(false))
            assertThat(functions[0].category, `is`("Admin Library"))
            assertThat(functions[0].subcategory, `is`("database"))
            assertThat(functions[0].bucket, `is`("XQuery Library Modules"))

            val qname = functions[0] as XsQNameValue
            assertThat(qname.prefix?.data, `is`("admin"))
            assertThat(qname.localName?.data, `is`("get-database-ids"))
            assertThat(qname.namespace?.data, `is`("http://marklogic.com/xdmp/admin"))
            assertThat(qname.namespace?.context, `is`(XdmUriContext.Namespace))
            assertThat(qname.namespace?.moduleTypes, `is`(XdmModuleType.MODULE))
            assertThat(qname.isLexicalQName, `is`(true))
            assertThat(qname.element, `is`(nullValue()))

            val ref = functions[0] as XdmFunctionDocumentation
            assertThat(ref.href, `is`("https://docs.marklogic.com/admin:get-database-ids"))
            assertThat(ref.summary, `is`("Lorem function dolor sed emit."))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples, `is`(nullValue()))

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`(nullValue()))
            assertThat(ref.rules, `is`(nullValue()))
            assertThat(ref.errorConditions, `is`(nullValue()))

            (ref as ApiDocsFunction).moduleType = XdmModuleType.JavaScript
            assertThat(ref.examples, `is`(nullValue()))
        }

        @Suppress("Reformat")
        @Test
        @DisplayName("from other module")
        fun fromOtherModule() {
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
                    <apidoc:function name="version" lib="xdmp" type="builtin" category="Other">
                        <apidoc:summary>
<p>Lorem function dolor.</p>
<p>Sed <code>emit</code> et dolor.</p>
                        </apidoc:summary>
                        <apidoc:example><pre xml:space="preserve"><![CDATA[
  1. Lorem ipsum dolor.
]]></pre></apidoc:example>
                        <apidoc:example class="xquery"><pre xml:space="preserve"><![CDATA[
  2. Lorem ipsum dolor.
]]></pre></apidoc:example>
                        <apidoc:example class="javascript"><pre xml:space="preserve"><![CDATA[
  3. Lorem ipsum dolor.
]]></pre></apidoc:example>
                        <apidoc:privilege>MarkLogic privileges are documented here.</apidoc:privilege>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val functions = apidocs.modules[0].functions
            assertThat(functions.size, `is`(1))

            assertThat(functions[0].isBuiltin, `is`(true))
            assertThat(functions[0].category, `is`("Other"))
            assertThat(functions[0].subcategory, `is`(nullValue()))
            assertThat(functions[0].bucket, `is`(nullValue()))

            val qname = functions[0] as XsQNameValue
            assertThat(qname.prefix?.data, `is`("xdmp"))
            assertThat(qname.localName?.data, `is`("version"))
            assertThat(qname.namespace, `is`(nullValue()))
            assertThat(qname.isLexicalQName, `is`(true))
            assertThat(qname.element, `is`(nullValue()))

            val ref = functions[0] as XdmFunctionDocumentation
            assertThat(ref.href, `is`("https://docs.marklogic.com/xdmp:version"))
            assertThat(ref.summary?.splitXml(), isListOf(
                "<p>Lorem function dolor.</p>",
                "<p>Sed <code>emit</code> et dolor.</p>"
            ))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples?.splitXml(), isListOf(
                "<div class=\"example\"><pre xml:space=\"preserve\">",
                "  1. Lorem ipsum dolor.",
                "</pre></div>",
                "<div class=\"example\"><pre xml:space=\"preserve\">",
                "  2. Lorem ipsum dolor.",
                "</pre></div>"
            ))

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`("MarkLogic privileges are documented here."))
            assertThat(ref.rules, `is`(nullValue()))
            assertThat(ref.errorConditions, `is`(nullValue()))

            (ref as ApiDocsFunction).moduleType = XdmModuleType.JavaScript
            assertThat(ref.examples?.splitXml(), isListOf(
                "<div class=\"example\"><pre xml:space=\"preserve\">",
                "  3. Lorem ipsum dolor.",
                "</pre></div>"
            ))
        }

        @Suppress("Reformat")
        @Test
        @DisplayName("builtin (cts namespace)")
        fun builtinCts() {
            @Language("XML")
            val adminLib = """
                <apidoc:module name="ClassifierBuiltins" category="Classifier" lib="cts"
                               xmlns:apidoc="http://marklogic.com/xdmp/apidoc">
                    <apidoc:summary><p>Lorem ipsum dolor.</p></apidoc:summary>
                    <apidoc:function name="train" type="builtin" lib="cts" category="Classifier">
                        <apidoc:summary>Lorem function dolor sed emit.</apidoc:summary>
                        <apidoc:usage>These are the usage notes.</apidoc:usage>
                        <apidoc:example><pre xml:space="preserve"><![CDATA[
  a < b & b > c
]]></pre></apidoc:example>
                    </apidoc:function>
                </apidoc:module>
            """
            val apidocs = create(
                "MarkLogic_10_pubs/pubs/raw/apidoc/admin.xml" to adminLib
            )

            val functions = apidocs.modules[0].functions
            assertThat(functions.size, `is`(1))

            assertThat(functions[0].isBuiltin, `is`(true))
            assertThat(functions[0].category, `is`("Classifier"))
            assertThat(functions[0].subcategory, `is`(nullValue()))
            assertThat(functions[0].bucket, `is`(nullValue()))

            val qname = functions[0] as XsQNameValue
            assertThat(qname.prefix?.data, `is`("cts"))
            assertThat(qname.localName?.data, `is`("train"))
            assertThat(qname.namespace, `is`(nullValue()))
            assertThat(qname.isLexicalQName, `is`(true))
            assertThat(qname.element, `is`(nullValue()))

            val ref = functions[0] as XdmFunctionDocumentation
            assertThat(ref.href, `is`("https://docs.marklogic.com/cts:train"))
            assertThat(ref.summary, `is`("Lorem function dolor sed emit."))
            assertThat(ref.notes, `is`(nullValue()))
            assertThat(ref.examples?.splitXml(), isListOf(
                "<div class=\"example\"><pre xml:space=\"preserve\">",
                "  a &lt; b &amp; b &gt; c",
                "</pre></div>"
            ))

            assertThat(ref.operatorMapping, `is`(nullValue()))
            assertThat(ref.signatures, `is`(nullValue()))
            assertThat(ref.properties, `is`(nullValue()))
            assertThat(ref.privileges, `is`(nullValue()))
            assertThat(ref.rules, `is`("These are the usage notes."))
            assertThat(ref.errorConditions, `is`(nullValue()))

            (ref as ApiDocsFunction).moduleType = XdmModuleType.JavaScript
            assertThat(ref.examples, `is`(nullValue()))
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
