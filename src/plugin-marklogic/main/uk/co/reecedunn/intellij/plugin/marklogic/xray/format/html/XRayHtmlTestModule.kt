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
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite

class XRayHtmlTestModule(private val module: Element) : TestSuite {
    override val name: String by lazy { module.selectFirst("summary > a")!!.text() }

    private val testCasesList by lazy {
        module.select("h4").map { XRayHtmlTest(it) }
    }

    override val testCases: Sequence<TestCase>
        get() = testCasesList.asSequence()

    override val error: Throwable? by lazy {
        val error = module.children().takeWhile { it.nodeName() != "h4" }.find {
            it.nodeName() == "pre" && it.attr("class") == "error"
        }
        error?.text()?.toMarkLogicQueryError(null)
    }
}
