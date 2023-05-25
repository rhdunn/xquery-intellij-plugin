/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.testframework.execution

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.testframework.execution.XQueryTestLocationProvider

@DisplayName("IntelliJ - Custom Language Support - Documentation Provider - XQuery")
class XQueryTestLocationProviderTest {
    @Test
    @DisplayName("function; NCName")
    fun functionNCName() {
        val locationHint = XQueryTestLocationProvider.locationHint("lorem-ipsum", "/tests/example/case.xqy")
        assertThat(locationHint, `is`("xquery:///tests/example/case.xqy#lorem-ipsum"))
    }

    @Test
    @DisplayName("function; QName")
    fun functionQName() {
        val locationHint = XQueryTestLocationProvider.locationHint("lorem:ipsum", "/tests/example/case.xqy")
        assertThat(locationHint, `is`("xquery:///tests/example/case.xqy#lorem:ipsum"))
    }

    @Test
    @DisplayName("function; URIQualifiedName without namespace")
    fun functionURIQualifiedNameWithoutNamespace() {
        val locationHint = XQueryTestLocationProvider.locationHint("Q{}lorem-ipsum", "/tests/example/case.xqy")
        assertThat(locationHint, `is`("xquery:///tests/example/case.xqy#Q{}lorem-ipsum"))
    }

    @Test
    @DisplayName("function; URIQualifiedName with namespace")
    fun functionURIQualifiedNameWithNamespace() {
        val locationHint = XQueryTestLocationProvider.locationHint(
            "Q{http://www.example.com}lorem-ipsum",
            "/tests/example/case.xqy"
        )
        assertThat(locationHint, `is`("xquery:///tests/example/case.xqy#Q{http://www.example.com}lorem-ipsum"))
    }

    @Test
    @DisplayName("module test suite")
    fun moduleTestSuite() {
        val locationHint = XQueryTestLocationProvider.locationHint("/tests/example/case.xqy")
        assertThat(locationHint, `is`("xquery:///tests/example/case.xqy"))
    }
}
