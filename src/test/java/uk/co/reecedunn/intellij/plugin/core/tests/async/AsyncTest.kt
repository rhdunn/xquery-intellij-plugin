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
    var localCallCount: Int = 0
    val local: ExecutableOnPooledThread<Int> = ExecuteOnLocalThread {
        localCallCount += 1
        2
    }

    var pooledCallCount: Int = 0
    val pooled: ExecutableOnPooledThread<Int> = ExecuteOnPooledThread {
        pooledCallCount += 1
        2
    }
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
            assertThat(test.localCallCount, `is`(0))

            val e = test.local.execute()
            assertThat(test.localCallCount, `is`(0))

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            var callbackCalled = false
            val e = test.local.execute { v ->
                assertThat(v, `is`(2))
                assertThat(test.localCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.localCallCount, `is`(0))
            assertThat(callbackCalled, `is`(false))

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("then")
        fun then() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            val e = test.local.then { v -> v + 1 }.execute()
            assertThat(test.localCallCount, `is`(0))

            assertThat(e.get(), `is`(3))
            assertThat(test.localCallCount, `is`(1))
        }
    }

    @Nested
    @DisplayName("executed on pooled thread")
    internal inner class OnPooledThread {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            val e = test.pooled.execute()
            assertThat(test.pooledCallCount, `is`(0))

            assertThat(e.get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            var callbackCalled = false
            val e = test.pooled.execute { v ->
                assertThat(v, `is`(2))
                assertThat(test.pooledCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.pooledCallCount, `is`(0))
            assertThat(callbackCalled, `is`(false))

            assertThat(e.get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed
        }

        @Test
        @DisplayName("then")
        fun then() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            val e = test.pooled.then { v -> v + 1 }.execute()
            assertThat(test.pooledCallCount, `is`(0))

            assertThat(e.get(), `is`(3))
            assertThat(test.pooledCallCount, `is`(1))
        }
    }
}
