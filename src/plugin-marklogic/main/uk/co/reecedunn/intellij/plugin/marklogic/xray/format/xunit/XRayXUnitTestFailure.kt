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
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult

class XRayXUnitTestFailure(private val failure: XmlElement) : TestAssert {
    override val type: String by lazy { failure.attributes.find { it.localName == "type" }!!.value }

    override val result: TestResult = TestResult.Failed

    override val expected: String? = null

    override val actual: String? = null

    override val message: String? by lazy { failure.text() }
}
