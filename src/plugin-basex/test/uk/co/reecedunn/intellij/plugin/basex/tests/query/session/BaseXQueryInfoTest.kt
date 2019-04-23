/*
 * Copyright (C) 2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXInfo
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXQueryError
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - BaseX info text")
class BaseXQueryInfoTest {
    @Test
    @DisplayName("BaseX 7.0")
    fun basex70() {
        val info = listOf(
            "Results: 4 Items",
            "Updated: 0 Items",
            "Total Time: 413.16 ms",
            ""
        ).joinToString("\r\n").toBaseXInfo()

        assertThat(info.size, `is`(3))
        assertThat(info["Results"], `is`("4 Items"))
        assertThat(info["Updated"], `is`("0 Items"))
        assertThat(info["Total Time"], `is`(XsDuration.ns(413160000)))
    }
    @Test
    @DisplayName("BaseX 8.0")
    fun basex80run() {
        val info = "\r\nQuery executed in 448.43 ms.".toBaseXInfo()

        assertThat(info.size, `is`(1))
        assertThat(info["Total Time"], `is`(XsDuration.ns(448430000)))
    }
}
