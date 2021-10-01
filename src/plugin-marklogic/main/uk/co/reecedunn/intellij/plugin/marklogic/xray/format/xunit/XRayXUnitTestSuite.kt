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
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestStatistics
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite
import kotlin.math.max

class XRayXUnitTestSuite(private val suite: XmlElement) : TestSuite, TestStatistics {
    override val name: String by lazy { suite.attribute("name")!! }

    override val total: Int by lazy { suite.attribute("tests")!!.toInt() }

    override val passed: Int by lazy { max(total - ignored - failed - errors, 0) }

    override val ignored: Int by lazy { suite.attribute("skipped")!!.toInt() }

    override val failed: Int by lazy { suite.attribute("failures")!!.toInt() }

    override val errors: Int by lazy { suite.attribute("errors")!!.toInt() }

    private val testCasesList by lazy {
        suite.children("testcase").map { XRayXUnitTestCase(it) }.toList()
    }

    override val testCases: Sequence<TestCase>
        get() = testCasesList.asSequence()

    override val error: Throwable? by lazy {
        suite.child("error")?.child("error:error")?.toMarkLogicQueryError(null)
    }
}
