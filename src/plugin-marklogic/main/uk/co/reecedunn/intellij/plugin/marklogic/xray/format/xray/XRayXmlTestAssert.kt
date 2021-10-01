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

import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult

class XRayXmlTestAssert(private val assertion: XmlElement) : TestAssert {
    override val type: String by lazy { assertion.attribute("test")!! }

    override val result: TestResult by lazy {
        assertion.attribute("result")!!.let { result -> TestResult.value(result) }
    }

    override val expected: String? by lazy {
        val expected = assertion.child("xray:expected") ?: return@lazy null
        expected.innerXml().trim()
    }

    override val actual: String? by lazy {
        val actual = assertion.child("xray:actual") ?: return@lazy null
        actual.innerXml().trim()
    }

    override val message: String? by lazy { assertion.child("xray:message")?.text() }

    companion object {
        fun parseList(asserts: String): Sequence<TestAssert> {
            val doc = XmlDocument.parse("<list>$asserts</list>", XRayXmlFormat.NAMESPACES)
            return doc.root.children("xray:assert").map { XRayXmlTestAssert(it) }
        }
    }
}
