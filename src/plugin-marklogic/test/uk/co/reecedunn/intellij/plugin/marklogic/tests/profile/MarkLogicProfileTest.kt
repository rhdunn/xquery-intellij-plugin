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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.profile

import org.hamcrest.CoreMatchers.`is`
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.marklogic.profile.MarkLogicProfile

@Suppress("XmlPathReference")
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Profiler - MarkLogicProfile")
class MarkLogicProfileTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        @Language("xml")
        val profile = """
            <prof:report xsi:schemaLocation="http://marklogic.com/xdmp/profile profile.xsd" xmlns:prof="http://marklogic.com/xdmp/profile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <prof:metadata>
                    <prof:overall-elapsed>PT0.0000564S</prof:overall-elapsed>
                    <prof:created>2019-01-03T09:44:37.9608193Z</prof:created>
                    <prof:server-version>9.0-5</prof:server-version>
                </prof:metadata>
                <prof:histogram/>
            </prof:report>
        """

        val p = MarkLogicProfile(profile)
        assertThat(p.elapsed, `is`("PT0.0000564S"))
        assertThat(p.created, `is`("2019-01-03T09:44:37.9608193Z"))
        assertThat(p.version, `is`("9.0-5"))
    }
}
