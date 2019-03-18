/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.http.mime

import org.apache.http.Header
import org.apache.http.message.BasicHeader

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - MimeResponse")
class MimeResponseTest {
    @Test
    @DisplayName("empty response")
    fun emptyResponse() {
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", "0")
        )
        val body = ""
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(1))
        assertThat(response.getHeader("Content-Length"), `is`("0"))
        assertThat(response.getHeader("Content-Type"), `is`(nullValue()))

        assertThat(response.parts[0].getHeader("Content-Length"), `is`("0"))
        assertThat(response.parts[0].getHeader("Content-Type"), `is`(nullValue()))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`(nullValue()))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`(""))
    }

    @Test
    @DisplayName("simple response content")
    fun simpleResponseContent() {
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", "5"),
            BasicHeader("Content-Type", "text/plain")
        )
        val body = "Hello"
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(1))
        assertThat(response.getHeader("Content-Length"), `is`("5"))
        assertThat(response.getHeader("Content-Type"), `is`("text/plain"))

        assertThat(response.parts[0].getHeader("Content-Length"), `is`("5"))
        assertThat(response.parts[0].getHeader("Content-Type"), `is`("text/plain"))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`(nullValue()))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`(body))
    }

    @Test
    @DisplayName("multipart with a single part")
    fun multipartWithASinglePart() {
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", "98"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=212ab95a34643c9d")
        )
        val body =
            "\r\n" +
            "--212ab95a34643c9d\r\n" +
            "Content-Type: text/plain\r\n" +
            "X-Primitive: integer\r\n" +
            "\r\n" +
            "15\r\n" +
            "--212ab95a34643c9d--\r\n"
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(1))
        assertThat(response.getHeader("Content-Length"), `is`("98"))
        assertThat(response.getHeader("Content-Type"), `is`("multipart/mixed; boundary=212ab95a34643c9d"))

        assertThat(response.parts[0].getHeader("Content-Type"), `is`("text/plain"))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`("integer"))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`("15"))
    }

    @Test
    @DisplayName("multipart with a single part with overridden Content-Type")
    fun multipartWithASinglePartWithOverriddenContentType() {
        // If a MarkLogic query sets the Content-Type header for a request, that value is placed
        // before the MarkLogic set multipart/mixed Content-Type header.
        val headers = arrayOf<Header>(
            BasicHeader("Content-Type", "text/rtf"),
            BasicHeader("Content-Length", "98"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=212ab95a34643c9d")
        )
        val body =
            "\r\n" +
            "--212ab95a34643c9d\r\n" +
            "Content-Type: text/plain\r\n" +
            "X-Primitive: integer\r\n" +
            "\r\n" +
            "15\r\n" +
            "--212ab95a34643c9d--\r\n"
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(1))
        assertThat(response.getHeader("Content-Length"), `is`("98"))
        assertThat(response.getHeader("Content-Type"), `is`("text/rtf"))

        assertThat(response.parts[0].getHeader("Content-Type"), `is`("text/plain"))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`("integer"))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`("15"))
    }

    @Test
    @DisplayName("multipart with multiple parts")
    fun multipartWithMultipleParts() {
        val headers = arrayOf<Header>(
            BasicHeader("Content-Length", "205"),
            BasicHeader("Content-Type", "multipart/mixed; boundary=47c813e0bbfa09d4")
        )
        val body =
            "\r\n" +
            "--47c813e0bbfa09d4\r\n" +
            "Content-Type: text/plain\r\n" +
            "X-Primitive: integer\r\n" +
            "\r\n" +
            "1\r\n" +
            "--47c813e0bbfa09d4\r\n" +
            "Content-Type: application/json\r\n" +
            "X-Primitive: number-node()\r\n" +
            "X-Path: number-node()\r\n" +
            "\r\n" +
            "5\r\n" +
            "--47c813e0bbfa09d4--\r\n"
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(2))
        assertThat(response.getHeader("Content-Length"), `is`("205"))
        assertThat(response.getHeader("Content-Type"), `is`("multipart/mixed; boundary=47c813e0bbfa09d4"))

        assertThat(response.parts[0].getHeader("Content-Type"), `is`("text/plain"))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`("integer"))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`("1"))

        assertThat(response.parts[1].getHeader("Content-Type"), `is`("application/json"))
        assertThat(response.parts[1].getHeader("X-Primitive"), `is`("number-node()"))
        assertThat(response.parts[1].getHeader("X-Path"), `is`("number-node()"))
        assertThat(response.parts[1].body, `is`("5"))
    }

    @Test
    @DisplayName("multipart with blank line in part body")
    fun multipartWithBlankLineInPartBody() {
        val body =
            "\r\n" +
            "--31c406fa29f12029\r\n" +
            "Content-Type: text/plain\r\n" +
            "X-Primitive: string\r\n" +
            "\r\n" +
            "@prefix p0: <http://example.co.uk/test> .\r\n" +
            "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\r\n" +
            "\r\n" +
            "p0:case a skos:Concept .\r\n" +
            "\r\n" +
            "--31c406fa29f12029--\r\n"
        val headers = arrayOf<Header>(BasicHeader("Content-Length", Integer.toString(body.length)), BasicHeader("Content-Type", "multipart/mixed; boundary=31c406fa29f12029"), BasicHeader("X-Content-Type", "text/turtle"))
        val response = MimeResponse(headers, body)

        assertThat(response.parts.size, `is`(1))
        assertThat(response.getHeader("Content-Length"), `is`("222"))
        assertThat(response.getHeader("Content-Type"), `is`("multipart/mixed; boundary=31c406fa29f12029"))

        val part1 =
            "@prefix p0: <http://example.co.uk/test> .\r\n" +
            "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\r\n" +
            "\r\n" +
            "p0:case a skos:Concept .\r\n"
        assertThat(response.parts[0].getHeader("Content-Type"), `is`("text/plain"))
        assertThat(response.parts[0].getHeader("X-Primitive"), `is`("string"))
        assertThat(response.parts[0].getHeader("X-Path"), `is`(nullValue()))
        assertThat(response.parts[0].body, `is`(part1))
    }
}
