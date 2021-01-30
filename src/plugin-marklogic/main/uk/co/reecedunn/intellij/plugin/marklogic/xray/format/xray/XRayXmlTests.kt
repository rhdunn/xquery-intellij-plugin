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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray

import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestResults

class XRayXmlTests(private val tests: XmlElement) : XRayTestResults {
    override val total: Int by lazy {
        modules.sumOf { it.total }
    }

    override val passed: Int by lazy {
        modules.sumOf { it.passed }
    }

    override val ignored: Int by lazy {
        modules.sumOf { it.ignored }
    }

    override val failed: Int by lazy {
        modules.sumOf { it.failed }
    }

    override val errors: Int by lazy {
        modules.sumOf { it.errors }
    }

    private val modulesList by lazy {
        tests.children("xray:module").map { XRayXmlTestModule(it) }.toList()
    }

    override val modules: Sequence<TestSuite>
        get() = modulesList.asSequence()
}
