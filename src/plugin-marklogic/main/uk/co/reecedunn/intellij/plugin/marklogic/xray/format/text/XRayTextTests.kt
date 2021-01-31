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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.text

import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites

class XRayTextTests(private val tests: String) : TestSuites {
    private val summary: Map<String, Int> by lazy {
        tests.split("\nFinished: ").last().split(", ").associate {
            it.split(' ').let { parts -> parts[0] to parts[1].toInt() }
        }
    }

    override val total: Int
        get() = summary["Total"] ?: 0

    override val passed: Int
        get() = summary["Passed"] ?: 0

    override val ignored: Int
        get() = summary["Ignored"] ?: 0

    override val failed: Int
        get() = summary["Failed"] ?: 0

    override val errors: Int
        get() = summary["Errors"] ?: 0

    private val testSuitesList by lazy {
        tests.split("\nModule ").map {
            XRayTextTestModule(it.substringBefore("\nFinished: "))
        }
    }

    override val testSuites: Sequence<TestSuite>
        get() = testSuitesList.asSequence()
}
