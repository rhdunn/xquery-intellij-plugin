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
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult

class XRayXUnitTestFailure(private val failure: XmlElement) : TestAssert {
    companion object {
        private val RE_ACTUAL = ", actual: ".toRegex()
    }

    private val expectedActual by lazy {
        val message = failure.text()
        when {
            message == null -> null
            message.startsWith("expected: ") && RE_ACTUAL.findAll(message).count() == 1 -> {
                val parts = message.substring(10).split(", actual: ")
                parts[0] to parts[1]
            }
            else -> null
        }
    }

    override val type: String by lazy { failure.attributes.find { it.localName == "type" }!!.value }

    override val result: TestResult = TestResult.Failed

    override val expected: String? = expectedActual?.first

    override val actual: String? = expectedActual?.second

    override val message: String? by lazy {
        when (expectedActual) {
            null -> failure.text()
            else -> null
        }
    }
}
