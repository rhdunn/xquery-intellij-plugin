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
package uk.co.reecedunn.intellij.plugin.core.tests.async

import com.intellij.testFramework.PlatformLiteFixture
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.ExecuteOnLocalThread
import uk.co.reecedunn.intellij.plugin.core.async.ExecuteOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

class TestAsync {
    val local: ExecutableOnPooledThread<Int> = ExecuteOnLocalThread { 2 }

    val pooled: ExecutableOnPooledThread<Int> = ExecuteOnPooledThread { 2 }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Threading - Execute On Pooled Thread")
private class AsyncTest : PlatformLiteFixture() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()
    }

    @Nested
    @DisplayName("executed on local thread")
    internal inner class OnLocalThread {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = TestAsync()
            assertThat(test.local.execute().get(), `is`(2))
        }
    }

    @Nested
    @DisplayName("executed on pooled thread")
    internal inner class OnPooledThread {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = TestAsync()
            assertThat(test.pooled.execute().get(), `is`(2))
        }
    }
}
