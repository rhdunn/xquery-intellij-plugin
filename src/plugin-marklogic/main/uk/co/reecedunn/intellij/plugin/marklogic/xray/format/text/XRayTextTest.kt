/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlTestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

class XRayTextTest(private val test: String) : TestCase {
    private val titleParts by lazy {
        test.substringBefore("\n").split(" ?-- ".toRegex()).filter { it != "" }
    }

    override val name: String
        get() = titleParts[0]

    override val result: TestResult by lazy { TestResult.value(titleParts[1].lowercase().trim()) }

    override val duration: XsDurationValue? = null

    private val failuresList by lazy {
        val start = test.indexOf("<assert ")
        if (start == -1)
            listOf()
        else
            XRayXmlTestAssert.parseList(test.substring(start)).toList()
    }

    override val asserts: Sequence<TestAssert>
        get() = failuresList.asSequence()

    override val error: Throwable? by lazy {
        val start = test.indexOf("<error:error ")
        val end = test.indexOf("</error:error>")
        if (start == -1)
            null
        else
            test.substring(start, end + 14).toMarkLogicQueryError(null)
    }
}
