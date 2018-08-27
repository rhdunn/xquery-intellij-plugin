/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

class XQueryTest {
    @Test
    fun testID() {
        assertThat(XQuery.getID(), `is`("XQuery"))
    }

    @Test
    fun testBaseLanguage() {
        assertThat(XQuery.baseLanguage, `is`(nullValue()))
    }

    @Test
    fun testMimeTypes() {
        assertThat(XQuery.mimeTypes.size, `is`(1))
        assertThat(XQuery.mimeTypes[0], `is`("application/xquery"))
    }

    @Test
    fun testCaseSensitivity() {
        assertThat(XQuery.isCaseSensitive, `is`(true))
    }
}
