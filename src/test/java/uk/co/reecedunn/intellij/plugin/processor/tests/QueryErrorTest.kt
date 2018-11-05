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
}
