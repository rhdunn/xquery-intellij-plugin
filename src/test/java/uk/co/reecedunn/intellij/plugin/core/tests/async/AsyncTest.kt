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
import org.hamcrest.CoreMatchers.anyOf
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.async.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

class TestAsync {
    var localCallCount: Int = 0
    val local: ExecutableOnPooledThread<Int> = local_thread {
        localCallCount += 1
        2
    }

    var pooledCallCount: Int = 0
    val pooled: ExecutableOnPooledThread<Int> = pooled_thread {
        pooledCallCount += 1
        2
    }
}

class ForwardingTestAsync {
    val async = TestAsync()

    val local by forwarded { async.local }

    val pooled by forwarded { async.pooled }
}

class CachingTestAsync {
    val async = TestAsync()

    val local by cached { async.local }

    val pooled by cached { async.pooled }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Threading - Executable On Pooled Thread")
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
            assertThat(test.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.localCallCount, `is`(2))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            var callbackCalled = false
            val e = test.local.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.localCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true))

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue

            assertThat(e.get(), `is`(2))
            assertThat(test.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.local.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.localCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.localCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(true))

            var secondCallbackCalled = false
            val second = test.local.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.localCallCount, `is`(2))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.localCallCount, `is`(2))
            assertThat(secondCallbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("then")
        fun then() {
            val test = TestAsync()
            assertThat(test.localCallCount, `is`(0))

            val e = test.local.then { v -> v + 1 }.execute()
            assertThat(test.localCallCount, `is`(1))

            assertThat(e.get(), `is`(3))
            assertThat(test.localCallCount, `is`(1))
        }
    }

    @Nested
    @DisplayName("executed on local thread by cached")
    internal inner class OnLocalThreadByCachedDelegate {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = CachingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            val e = test.local.execute()
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = CachingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = CachingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            var callbackCalled = false
            val e = test.local.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = CachingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.local.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(true))

            var secondCallbackCalled = false
            val second = test.local.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(1))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(secondCallbackCalled, `is`(true)) // local execute not added to the event queue
        }
    }

    @Nested
    @DisplayName("executed on local thread by forwarded")
    internal inner class OnLocalThreadByForwardedDelegate {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = ForwardingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            val e = test.local.execute()
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = ForwardingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(test.local.execute().get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(2))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = ForwardingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            var callbackCalled = false
            val e = test.local.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue

            assertThat(e.get(), `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(callbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = ForwardingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.local.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.async.localCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(true))

            var secondCallbackCalled = false
            val second = test.local.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.localCallCount, `is`(2))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.async.localCallCount, `is`(2))
            assertThat(secondCallbackCalled, `is`(true)) // local execute not added to the event queue
        }

        @Test
        @DisplayName("then")
        fun then() {
            val test = ForwardingTestAsync()
            assertThat(test.async.localCallCount, `is`(0))

            val e = test.local.then { v -> v + 1 }.execute()
            assertThat(test.async.localCallCount, `is`(1))

            assertThat(e.get(), `is`(3))
            assertThat(test.async.localCallCount, `is`(1))
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

            assertThat(e.get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(2))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            var callbackCalled = false
            val e = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.pooledCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.pooledCallCount, `is`(0))
            assertThat(callbackCalled, `is`(false))

            assertThat(e.get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed

            assertThat(e.get(), `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = TestAsync()
            assertThat(test.pooledCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.pooledCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.pooledCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(false)) // event queue not flushed

            var secondCallbackCalled = false
            val second = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.pooledCallCount, `is`(2))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.pooledCallCount, `is`(2))
            assertThat(secondCallbackCalled, `is`(false)) // event queue not flushed
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

    @Nested
    @DisplayName("executed on pooled thread by cached")
    internal inner class OnPooledThreadByCachedDelegate {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = CachingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            val e = test.pooled.execute()
            assertThat(test.async.pooledCallCount, `is`(0))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = CachingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = CachingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            var callbackCalled = false
            val e = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.async.pooledCallCount, `is`(0))
            assertThat(callbackCalled, `is`(false))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = CachingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(false)) // event queue not flushed

            var secondCallbackCalled = false
            val second = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(1))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(secondCallbackCalled, `is`(true))
        }
    }

    @Nested
    @DisplayName("executed on pooled thread by forwarded")
    internal inner class OnPooledThreadByForwardedDelegate {
        @Test
        @DisplayName("execute")
        fun execute() {
            val test = ForwardingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            val e = test.pooled.execute()
            assertThat(test.async.pooledCallCount, anyOf(`is`(0), `is`(1)))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
        }

        @Test
        @DisplayName("execute; multiple calls")
        fun executeMultiple() {
            val test = ForwardingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))

            assertThat(test.pooled.execute().get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(2))
        }

        @Test
        @DisplayName("execute with later callback")
        fun executeWithLater() {
            val test = ForwardingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            var callbackCalled = false
            val e = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", callbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(1))
                callbackCalled = true
            }
            assertThat(test.async.pooledCallCount, anyOf(`is`(0), `is`(1)))
            assertThat(callbackCalled, `is`(false))

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed

            assertThat(e.get(), `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(callbackCalled, `is`(false)) // event queue not flushed
        }

        @Test
        @DisplayName("execute with later callback; multiple calls")
        fun executeWithLaterMultiple() {
            val test = ForwardingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            var firstCallbackCalled = false
            val first = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", firstCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(1))
                firstCallbackCalled = true
            }.get()

            assertThat(first, `is`(2))
            assertThat(test.async.pooledCallCount, `is`(1))
            assertThat(firstCallbackCalled, `is`(false)) // event queue not flushed

            var secondCallbackCalled = false
            val second = test.pooled.execute { v ->
                assertThat("Later callback should only be called once.", secondCallbackCalled, `is`(false))
                assertThat(v, `is`(2))
                assertThat(test.async.pooledCallCount, `is`(2))
                secondCallbackCalled = true
            }.get()

            assertThat(second, `is`(2))
            assertThat(test.async.pooledCallCount, `is`(2))
            assertThat(secondCallbackCalled, `is`(false)) // event queue not flushed
        }

        @Test
        @DisplayName("then")
        fun then() {
            val test = ForwardingTestAsync()
            assertThat(test.async.pooledCallCount, `is`(0))

            val e = test.pooled.then { v -> v + 1 }.execute()
            assertThat(test.async.pooledCallCount, anyOf(`is`(0), `is`(1)))

            assertThat(e.get(), `is`(3))
            assertThat(test.async.pooledCallCount, `is`(1))
        }
    }
}
