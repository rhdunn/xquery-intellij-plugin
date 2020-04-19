/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.query.rest

import com.intellij.compat.testFramework.PlatformLiteFixture
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.queryResults
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

@Suppress("Reformat")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - MarkLogic MIME Response to Query Results")
class Response : PlatformLiteFixture() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()

        registerApplicationService(XDebuggerUtil::class.java, XDebuggerUtilImpl())
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("content length 0")
    fun emptySequence() {
        // query: ()
        val body = ""
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(0))
    }

    @Test
    @DisplayName("xs:integer -- X-Primitive header")
    fun integer() {
        // query: 1
        val body = listOf(
            "",
            "--f260e14402e96a50",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "1",
            "--f260e14402e96a50--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-Type", "multipart/mixed; boundary=f260e14402e96a50"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("1"))
        assertThat(results[0].type, `is`("xs:integer"))
        assertThat(results[0].mimetype, `is`("text/plain"))
    }

    @Test
    @DisplayName("xs:integer+ -- multipart response")
    fun integerSequence() {
        // query: 1, 2, 3
        val body = listOf(
            "",
            "--869b5d54aa36954a",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "1",
            "--869b5d54aa36954a",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "2",
            "--869b5d54aa36954a",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "3",
            "--869b5d54aa36954a--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-Type", "multipart/mixed; boundary=869b5d54aa36954a"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(3))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("1"))
        assertThat(results[0].type, `is`("xs:integer"))
        assertThat(results[0].mimetype, `is`("text/plain"))

        assertThat(results[1].position, `is`(1L))
        assertThat(results[1].value, `is`("2"))
        assertThat(results[1].type, `is`("xs:integer"))
        assertThat(results[1].mimetype, `is`("text/plain"))

        assertThat(results[2].position, `is`(2L))
        assertThat(results[2].value, `is`("3"))
        assertThat(results[2].type, `is`("xs:integer"))
        assertThat(results[2].mimetype, `is`("text/plain"))
    }

    @Test
    @DisplayName("xs:numeric+ -- X-Primitive and X-Derived-N headers")
    fun numericSequence() {
        // query: 5 cast as xs:short, 8 cast as xs:long
        val body = listOf(
            "",
            "--3734bc5a2395b3de",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "5",
            "--3734bc5a2395b3de",
            "Content-Type: text/plain",
            "X-Primitive: integer",
            "",
            "8",
            "--3734bc5a2395b3de--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("X-Derived-1", "xs:short"),
            BasicHeader("X-Derived-2", "xs:long"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=3734bc5a2395b3de"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(2))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("5"))
        assertThat(results[0].type, `is`("xs:short"))
        assertThat(results[0].mimetype, `is`("text/plain"))

        assertThat(results[1].position, `is`(1L))
        assertThat(results[1].value, `is`("8"))
        assertThat(results[1].type, `is`("xs:long"))
        assertThat(results[1].mimetype, `is`("text/plain"))
    }

    @Test
    @DisplayName("array-node() -- application/json Content-Type header")
    fun arrayNode() {
        // query: array-node { 1, 2, 3 }
        val body = listOf(
            "",
            "--b857af6eabb53cb2",
            "Content-Type: application/json",
            "X-Primitive: array-node()",
            "X-Path: array-node()",
            "",
            "[1, 2, 3]",
            "--b857af6eabb53cb2--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-Type", "multipart/mixed; boundary=b857af6eabb53cb2"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("[1, 2, 3]"))
        assertThat(results[0].type, `is`("array-node()"))
        assertThat(results[0].mimetype, `is`("application/json"))
    }

    @Test
    @DisplayName("fn:error -- MarkLogicQueryError")
    fun error() {
        // query: 1 div 0
        val body = listOf(
            "",
            "--fb98e57ec409700a",
            "Content-Type: application/xml",
            "X-Primitive: element()",
            "X-Path: /err:error",
            "",
            "<err:error xmlns:err=\"http://www.w3.org/2005/xqt-errors\"",
            "           xmlns:error=\"http://marklogic.com/xdmp/error\">",
            "  <err:code>err:FOAR0001</err:code>",
            "  <err:vendor-code>XDMP-DIVBYZERO</err:vendor-code>",
            "  <error:message>Division by zero</error:message>",
            "  <error:data/>",
            "  <error:stack>",
            "    <error:frame><error:line>23</error:line><error:column>2</error:column></error:frame>",
            "    <error:frame><error:line>253</error:line><error:column>27</error:column><error:uri>/eval</error:uri></error:frame>",
            "  </error:stack>",
            "</err:error>",
            "--fb98e57ec409700a--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("X-Derived-1", "err:error"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=fb98e57ec409700a"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val e: QueryError = Assertions.assertThrows(QueryError::class.java) {
            MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        }

        assertThat(e.description, `is`("Division by zero"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(22))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(2))
        assertThat(e.standardCode, `is`("FOAR0001"))
        assertThat(e.vendorCode, `is`("XDMP-DIVBYZERO"))
    }

    @Test
    @DisplayName("xdmp:set-response-content-type -- Custom Content-Type header")
    fun responseContentType() {
        // query: xdmp:set-response-content-type("text/css"), "strong { font-weight: bold; }"
        val body = listOf(
            "",
            "--40d8fa04d13bdcb1",
            "Content-Type: text/plain",
            "X-Primitive: string",
            "",
            "strong { font-weight: bold; }",
            "--40d8fa04d13bdcb1--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-type", "text/css; charset=UTF-8"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=40d8fa04d13bdcb1"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("strong { font-weight: bold; }"))
        assertThat(results[0].type, `is`("xs:string"))
        assertThat(results[0].mimetype, `is`("text/css"))
    }

    @Test
    @DisplayName("sem:triple+ -- overriding the default Content-Type")
    fun triples() {
        // rdf-output-format: ""
        // query:
        //     import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";
        //     sem:rdf-parse("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
        //                    <http://www.example.co.uk/test1> a rdf:Property .
        //                    <http://www.example.co.uk/test2> a rdf:Property .", "turtle")
        val body = listOf(
            "",
            "--b54154d3ae21a0f1",
            "Content-Type: text/plain",
            "X-Primitive: triple",
            "",
            "sem:triple(sem:iri(\"http://www.example.co.uk/test1\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"))",
            "--b54154d3ae21a0f1",
            "Content-Type: text/plain",
            "X-Primitive: triple",
            "",
            "sem:triple(sem:iri(\"http://www.example.co.uk/test2\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"))",
            "--b54154d3ae21a0f1--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-Type", "multipart/mixed; boundary=b54154d3ae21a0f1"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(2))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("sem:triple(sem:iri(\"http://www.example.co.uk/test1\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"))"))
        assertThat(results[0].type, `is`("sem:triple"))
        assertThat(results[0].mimetype, `is`("application/xquery"))

        assertThat(results[1].position, `is`(1L))
        assertThat(results[1].value, `is`("sem:triple(sem:iri(\"http://www.example.co.uk/test2\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"), sem:iri(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"))"))
        assertThat(results[1].type, `is`("sem:triple"))
        assertThat(results[1].mimetype, `is`("application/xquery"))
    }

    @Test
    @DisplayName("sem:triple+ -- rdf-output-format")
    fun triplesRdfOutputFormat() {
        // rdf-output-format: ""
        // query:
        //     import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";
        //     sem:rdf-parse("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
        //                    <http://www.example.co.uk/test1> a rdf:Property .
        //                    <http://www.example.co.uk/test2> a rdf:Property .", "turtle")
        val body = listOf(
            "",
            "--e59349f9cdb07885",
            "Content-Type: text/plain",
            "X-Primitive: string",
            "",
            "<http://www.example.co.uk/test1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> .",
            "<http://www.example.co.uk/test2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> .",
            "--e59349f9cdb07885--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("X-Content-Type-1", "application/n-triples"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=e59349f9cdb07885"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(headers, body, Charsets.ISO_8859_1).queryResults(DatabaseModule("test.xqy")).toList()
        assertThat(results.size, `is`(1))

        val value = listOf(
            "<http://www.example.co.uk/test1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> .",
            "<http://www.example.co.uk/test2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> ."
        ).joinToString("\r\n")

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`(value))
        assertThat(results[0].type, `is`("xs:string"))
        assertThat(results[0].mimetype, `is`("application/n-triples"))
    }
}
