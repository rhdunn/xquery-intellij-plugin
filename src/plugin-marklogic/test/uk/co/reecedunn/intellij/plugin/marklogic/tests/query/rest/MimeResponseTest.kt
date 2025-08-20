// Copyright (C) 2019-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.tests.query.rest

import com.intellij.openapi.extensions.PluginId
import com.intellij.testFramework.LightVirtualFile
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.http.StringMessage
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.xdebugger.requiresXDebuggerUtilCreatePosition
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.queryResults
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType

@Suppress("Reformat")
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - MarkLogic MIME Response to Query Results")
class MimeResponseTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("MimeResponseTest")

    override fun registerServicesAndExtensions() {
        requiresXDebuggerUtilCreatePosition()

        XQueryFileType.registerFileType()
    }

    @Test
    @DisplayName("content length 0")
    fun emptySequence() {
        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val body = ""
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        assertThat(results.size, `is`(0))
    }

    @Test
    @DisplayName("xs:integer -- X-Primitive header")
    fun integer() {
        val testFile = LightVirtualFile("test.xq", XQuery, "1")
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("1"))
        assertThat(results[0].type, `is`("xs:integer"))
        assertThat(results[0].mimetype, `is`("text/plain"))
    }

    @Test
    @DisplayName("xs:integer+ -- multipart response")
    fun integerSequence() {
        val testFile = LightVirtualFile("test.xq", XQuery, "1, 2, 3")
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
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
        val testFile = LightVirtualFile("test.xq", XQuery, "5 cast as xs:short, 8 cast as xs:long")
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
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
        val testFile = LightVirtualFile("test.xq", XQuery, "array-node { 1, 2, 3 }")
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("[1, 2, 3]"))
        assertThat(results[0].type, `is`("array-node()"))
        assertThat(results[0].mimetype, `is`("application/json"))
    }

    @Test
    @DisplayName("fn:error -- MarkLogicQueryError")
    fun error() {
        val testFile = LightVirtualFile("test.xq", XQuery, "1 div 0")
        val body = listOf(
            "",
            "--fb98e57ec409700a",
            "Content-Type: application/xml",
            "X-Primitive: element()",
            "X-Path: /err:error",
            "",
            "<error:error xmlns:error=\"http://marklogic.com/xdmp/error\">",
            "  <error:code>XDMP-DIVBYZERO</error:code>",
            "  <error:name>err:FOAR0001</error:name>",
            "  <error:message>Division by zero</error:message>",
            "  <error:format-string>XDMP-DIVBYZERO: (err:FOAR0001) Division by zero</error:format-string>",
            "  <error:data/>",
            "  <error:stack>",
            "    <error:frame><error:line>23</error:line><error:column>2</error:column></error:frame>",
            "    <error:frame><error:line>253</error:line><error:column>27</error:column><error:uri>/eval</error:uri></error:frame>",
            "  </error:stack>",
            "</error:error>",
            "--fb98e57ec409700a--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("X-Derived-1", "err:error"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=fb98e57ec409700a"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val e: QueryError = Assertions.assertThrows(QueryError::class.java) {
            MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        }

        assertThat(e.description, `is`("Division by zero"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(22))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(2))
        assertThat(e.standardCode, `is`("FOAR0001"))
        assertThat(e.vendorCode, `is`("XDMP-DIVBYZERO"))
    }

    @Test
    @DisplayName("xdmp:set-response-content-type -- Custom Content-Type header")
    fun responseContentType() {
        val testFile = LightVirtualFile(
            "test.xq", XQuery, "xdmp:set-response-content-type(\"text/css\"), \"strong { font-weight: bold; }\""
        )
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("strong { font-weight: bold; }"))
        assertThat(results[0].type, `is`("xs:string"))
        assertThat(results[0].mimetype, `is`("text/css"))
    }

    @Test
    @DisplayName("xdmp:set-response-content-type -- Custom Content-Type header with non-ASCII characters")
    fun responseContentTypeWithNonAsciiCharacters() {
        val testFile = LightVirtualFile(
            "test.xq", XQuery, "xdmp:set-response-content-type(\"text/html; charset=URF-8\"), <p>a\u00a0b</p>"
        )
        val body = listOf(
            "",
            "--40d8fa04d13bdcb1",
            "Content-Type: text/plain",
            "X-Primitive: string",
            "",
            "<p>a\u00a0b</p>",
            "--40d8fa04d13bdcb1--",
            ""
        ).joinToString("\r\n")
        val headers = arrayOf<Header>(
            BasicHeader("Content-type", "text/html; charset=UTF-8"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=40d8fa04d13bdcb1"),
            BasicHeader("Content-Length", body.length.toString())
        )
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].position, `is`(0L))
        assertThat(results[0].value, `is`("<p>a\u00a0b</p>"))
        assertThat(results[0].type, `is`("xs:string"))
        assertThat(results[0].mimetype, `is`("text/html"))
    }

    @Test
    @DisplayName("sem:triple+ -- overriding the default Content-Type")
    fun triples() {
        val testFile = LightVirtualFile(
            "test.xq", XQuery,
            """
            import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";
            sem:rdf-parse("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                           <http://www.example.co.uk/test1> a rdf:Property .
                           <http://www.example.co.uk/test2> a rdf:Property .", "turtle")
            """
        )
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
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
        val testFile = LightVirtualFile(
            "test.xq", XQuery,
            """
            import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";
            sem:rdf-parse("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                           <http://www.example.co.uk/test1> a rdf:Property .
                           <http://www.example.co.uk/test2> a rdf:Property .", "turtle")
            """
        )
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
        val results = MimeResponse(StringMessage(headers, body)).queryResults(testFile).toList()
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
