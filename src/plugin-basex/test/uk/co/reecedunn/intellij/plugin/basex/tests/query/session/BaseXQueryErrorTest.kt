/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.tests.query.session

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXError
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - BaseXQueryError")
class BaseXQueryErrorTest {
    @Test
    @DisplayName("with context")
    fun withContext() {
        val e = "Stopped at ., 1/7:\r\n[XPST0003] Incomplete expression.".toBaseXError()
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Incomplete expression."))
        assertThat(e.frames[0].module, `is`(nullValue()))
        assertThat(e.frames[0].lineNumber, `is`(1))
        assertThat(e.frames[0].columnNumber, `is`(7))
    }

    @Test
    @DisplayName("with context as line/column")
    fun withContextAsLineColumn() {
        val e = "Stopped at line 1, column 7:\r\n[XPST0003] Incomplete expression.".toBaseXError()
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Incomplete expression."))
        assertThat(e.frames[0].module, `is`(nullValue()))
        assertThat(e.frames[0].lineNumber, `is`(1))
        assertThat(e.frames[0].columnNumber, `is`(7))
    }

    @Test
    @DisplayName("without context")
    fun withoutContext() {
        val e = "[XPST0003] Unknown type: array-node().".toBaseXError()
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Unknown type: array-node()."))
        assertThat(e.frames[0].module, `is`(nullValue()))
        assertThat(e.frames[0].lineNumber, `is`(nullValue()))
        assertThat(e.frames[0].columnNumber, `is`(nullValue()))
    }
}
