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

import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTest
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestModule

class XRayXUnitTestSuite(private val suite: XmlElement) : XRayTestModule {
    override val path: String by lazy { suite.attribute("name")!! }

    override val total: Int by lazy { suite.attribute("tests")!!.toInt() }

    override val passed: Int by lazy { total - ignored - failed - errors }

    override val ignored: Int by lazy { suite.attribute("skipped")!!.toInt() }

    override val failed: Int by lazy { suite.attribute("failures")!!.toInt() }

    override val errors: Int by lazy { suite.attribute("errors")!!.toInt() }

    private val testsList by lazy {
        suite.children("testcase").map { XRayXUnitTestCase(it) }.toList()
    }

    override val tests: Sequence<XRayTest>
        get() = testsList.asSequence()

}
