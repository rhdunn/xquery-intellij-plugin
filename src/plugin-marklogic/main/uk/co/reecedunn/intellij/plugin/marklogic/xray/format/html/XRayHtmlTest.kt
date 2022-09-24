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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.html

import org.jsoup.nodes.Element
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlTestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.toXsDuration

class XRayHtmlTest(private val test: Element) : TestCase {
    private val titleParts by lazy { test.selectFirst("a")!!.text().split(" -- ") }

    override val name: String
        get() = titleParts[0]

    override val result: TestResult by lazy { TestResult.value(test.attr("class")) }

    override val duration: XsDurationValue? by lazy {
        titleParts.getOrNull(2)?.let { "PT$it".toXsDuration() }
    }

    private val failuresList by lazy {
        test.nextElementSiblings().asSequence().takeWhile { it.nodeName() != "h4" }.flatMap {
            when {
                it.nodeName() == "pre" && it.attr("class") != "error" -> {
                    val text = it.text()
                    when {
                        text.startsWith("<assert") -> XRayXmlTestAssert.parseList(text)
                        else -> emptySequence()
                    }
                }
                else -> emptySequence()
            }
        }.toList()
    }

    override val asserts: Sequence<TestAssert>
        get() = failuresList.asSequence()

    override val error: Throwable? by lazy {
        val error = test.nextElementSiblings().takeWhile { it.nodeName() != "h4" }.find {
            it.nodeName() == "pre" && it.attr("class") == "error"
        }
        error?.text()?.toMarkLogicQueryError(null)
    }
}
