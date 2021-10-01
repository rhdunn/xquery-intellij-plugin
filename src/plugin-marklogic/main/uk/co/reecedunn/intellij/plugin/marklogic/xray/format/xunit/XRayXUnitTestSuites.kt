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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xunit

import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.test.TestStatistics
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites
import kotlin.math.max

class XRayXUnitTestSuites(private val suites: XmlElement) : TestSuites {
    override val total: Int by lazy { suites.attribute("tests")!!.toInt() }

    override val passed: Int by lazy { max(total - ignored - failed - errors, 0) }

    override val ignored: Int by lazy { suites.attribute("skipped")!!.toInt() }

    override val failed: Int by lazy { suites.attribute("failures")!!.toInt() }

    override val errors: Int by lazy {
        // NOTE: The XRay xunit formatter has a bug where the errors attribute
        // does not match the total from the separate test suites. This is
        // because it includes the error:error element inside a failing xray:test.
        testSuites.sumBy { (it as TestStatistics).errors }
    }

    private val testSuitesList by lazy {
        suites.children("testsuite").map { XRayXUnitTestSuite(it) }.toList()
    }

    override val testSuites: Sequence<TestSuite>
        get() = testSuitesList.asSequence()
}
