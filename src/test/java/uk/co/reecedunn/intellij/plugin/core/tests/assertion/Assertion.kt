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
package uk.co.reecedunn.intellij.plugin.core.tests.assertion

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert

/*
 * Using the hamcrest version of `assertThat` fails on different but
 * convertible types in Kotlin:
 *
 *     val s: CharSequence = "test"
 *     MatcherAssert.assertThat(s, CoreMatchers.`is`("test"))
 *                   ^^^^^^^^^^
 *                   > Undefined of the following functions can be called with the arguments supplied.
 *
 * This is a Kotlin version of assertThat which does work in the above use
 * case. The fix is to specify the `matcher` template argument as an `out`
 * value.
 *
 * See: https://youtrack.jetbrains.com/issue/KT-22012.
 */

@Suppress("UNCHECKED_CAST")
fun <T> assertThat(actual: T?, matcher: Matcher<out T?>) {
    MatcherAssert.assertThat(actual, matcher as Matcher<T?>)
}

@Suppress("UNCHECKED_CAST")
fun <T> assertThat(reason: String, actual: T?, matcher: Matcher<out T?>) {
    MatcherAssert.assertThat(reason, actual, matcher as Matcher<T?>)
}
