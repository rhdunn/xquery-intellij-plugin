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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.basex.session.BaseXQueryError
import uk.co.reecedunn.intellij.plugin.processor.existdb.rest.EXistDBQueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.primitiveToItemType

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - QueryError")
class QueryErrorTest {
    @Nested
    @DisplayName("BaseX")
    internal inner class BaseX {
        @Test
        @DisplayName("with context")
        fun withContext() {
            val e = BaseXQueryError("Stopped at ., 1/7:\r\n[XPST0003] Incomplete expression.")
            assertThat(e.standardCode, `is`("XPST0003"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("Incomplete expression."))
            assertThat(e.module, `is`(nullValue()))
            assertThat(e.lineNumber, `is`(1))
            assertThat(e.columnNumber, `is`(7))
        }

        @Test
        @DisplayName("without context")
        fun withoutContext() {
            val e = BaseXQueryError("[XPST0003] Unknown type: array-node().")
            assertThat(e.standardCode, `is`("XPST0003"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("Unknown type: array-node()."))
            assertThat(e.module, `is`(nullValue()))
            assertThat(e.lineNumber, `is`(nullValue()))
            assertThat(e.columnNumber, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("eXist-db")
    internal inner class EXistDB {
        @Test
        @DisplayName("with context")
        fun withContext() {
            val e = EXistDBQueryError("<?xml version=\"1.0\" ?><exception><path>/db</path><message>exerr:ERROR org.exist.xquery.XPathException: err:XPST0003 unexpected token: null [at line 1, column 7]\n</message></exception>")
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
            val e = EXistDBQueryError("<?xml version=\"1.0\" ?><exception><path>/db</path><message>exerr:ERROR Type: xs:dateTimeStamp is not defined</message></exception>")
            assertThat(e.standardCode, `is`("FOER0000"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("xs:dateTimeStamp is not defined"))
            assertThat(e.module, `is`("/db"))
            assertThat(e.lineNumber, `is`(nullValue()))
            assertThat(e.columnNumber, `is`(nullValue()))
        }
    }
}
