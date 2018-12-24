/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.tests

import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.basex.query.session.BaseXQueryError
import uk.co.reecedunn.intellij.plugin.processor.impl.existdb.rest.EXistDBQueryError
import uk.co.reecedunn.intellij.plugin.processor.impl.marklogic.rest.MarkLogicQueryError

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - QueryError")
class QueryErrorTest {
    @Nested
    @DisplayName("eXist-db")
    internal inner class EXistDB {
        @Test
        @DisplayName("XPathException")
        fun xpathException() {
            @Language("xml")
            val exception = """<?xml version="1.0" ?>
                <exception>
                    <path>/db</path>
                    <message>exerr:ERROR org.exist.xquery.XPathException: err:XPST0003 unexpected token: null [at line 1, column 7]
</message>
                </exception>
            """

            val e = EXistDBQueryError(exception)
            assertThat(e.standardCode, `is`("XPST0003"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("unexpected token: null"))
            assertThat(e.module, `is`("/db"))
            assertThat(e.lineNumber, `is`(1))
            assertThat(e.columnNumber, `is`(7))
        }

        @Test
        @DisplayName("type error")
        fun typeError() {
            @Language("xml")
            val exception = """<?xml version="1.0" ?>
                <exception>
                    <path>/db</path>
                    <message>exerr:ERROR Type: xs:dateTimeStamp is not defined</message>
                </exception>
            """

            val e = EXistDBQueryError(exception)
            assertThat(e.standardCode, `is`("FOER0000"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("xs:dateTimeStamp is not defined"))
            assertThat(e.module, `is`("/db"))
            assertThat(e.lineNumber, `is`(nullValue()))
            assertThat(e.columnNumber, `is`(nullValue()))
        }

        @Test
        @DisplayName("error")
        fun error() {
            @Language("xml")
            val exception = """<?xml version="1.0" ?>
                <exception>
                    <path>/db</path>
                    <message>err:XPTY0004 Too many operands at the left of * [at line 1, column 11, source: (1, 2, 3) * 2]</message>
                </exception>
            """

            val e = EXistDBQueryError(exception)
            assertThat(e.standardCode, `is`("XPTY0004"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("Too many operands at the left of *"))
            assertThat(e.module, `is`("/db"))
            assertThat(e.lineNumber, `is`(1))
            assertThat(e.columnNumber, `is`(11))
        }
    }

    @Nested
    @DisplayName("MarkLogic")
    internal inner class MarkLogic {
        @Test
        @DisplayName("XQuery error code")
        fun xqueryErrorCode() {
            @Language("xml")
            val exception = """
                <err:error xmlns:dbg="http://reecedunn.co.uk/xquery/debug" xmlns:err="http://www.w3.org/2005/xqt-errors">
                    <err:code>err:XPST0003</err:code>
                    <err:vendor-code>XDMP-UNEXPECTED</err:vendor-code>
                    <err:description>Unexpected token</err:description>
                    <err:value count="0"/>
                    <err:module line="1" column="6"/>
                    <dbg:stack>
                        <dbg:frame><dbg:module line="1" column="6"/></dbg:frame>
                    </dbg:stack>
                </err:error>
            """

            val e = MarkLogicQueryError(exception)
            assertThat(e.standardCode, `is`("XPST0003"))
            assertThat(e.vendorCode, `is`("XDMP-UNEXPECTED"))
            assertThat(e.description, `is`("Unexpected token"))
            assertThat(e.module, `is`(nullValue()))
            assertThat(e.lineNumber, `is`(1))
            assertThat(e.columnNumber, `is`(6))
        }

        @Test
        @DisplayName("MarkLogic error code")
        fun marklogicErrorCode() {
            @Language("xml")
            val exception = """
                <err:error xmlns:dbg="http://reecedunn.co.uk/xquery/debug" xmlns:err="http://www.w3.org/2005/xqt-errors">
                    <err:code>err:FOER0000</err:code>
                    <err:vendor-code>XDMP-XQUERYVERSIONSWITCH</err:vendor-code>
                    <err:description>All modules in a module sequence must use the same XQuery version</err:description>
                    <err:value count="0"/>
                    <err:module line="1" column="53"/>
                    <dbg:stack>
                        <dbg:frame><dbg:module line="1" column="53"/></dbg:frame>
                    </dbg:stack>
                </err:error>
            """

            val e = MarkLogicQueryError(exception)
            assertThat(e.standardCode, `is`("FOER0000"))
            assertThat(e.vendorCode, `is`("XDMP-XQUERYVERSIONSWITCH"))
            assertThat(e.description, `is`("All modules in a module sequence must use the same XQuery version"))
            assertThat(e.module, `is`(nullValue()))
            assertThat(e.lineNumber, `is`(1))
            assertThat(e.columnNumber, `is`(53))
        }
    }
}
