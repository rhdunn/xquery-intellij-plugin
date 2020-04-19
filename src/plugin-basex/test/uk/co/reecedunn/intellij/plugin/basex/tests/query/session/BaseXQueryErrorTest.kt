/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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

import com.intellij.compat.testFramework.PlatformLiteFixture
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXQueryError
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - BaseXQueryError")
class BaseXQueryErrorTest : PlatformLiteFixture() {
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
    @DisplayName("at runtime; BaseX 8.0")
    fun runtime80() {
        // Query: ```1 div```
        val e = "Stopped at ., 1/6:\r\n[XPST0003] Calculation is incomplete."
            .toBaseXQueryError(DatabaseModule("test.xqy"))
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Calculation is incomplete."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }

    @Test
    @DisplayName("with context; BaseX 7.0")
    fun runtime70() {
        // Query: ```1 div```
        val e = "Stopped at line 1, column 5:\r\n[XPST0003] Calculation is incomplete."
            .toBaseXQueryError(DatabaseModule("test.xqy"))
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Calculation is incomplete."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(4))
    }

    @Test
    @DisplayName("bind context; unknown type")
    fun bindContextUnknownType() {
        // Bind context to unknown type -- "[]" as "array-node()".
        val e = "[XPST0003] Unknown type: array-node().".toBaseXQueryError(DatabaseModule("test.xqy"))
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Unknown type: array-node()."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(0))
    }
}
