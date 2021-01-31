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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.text

import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite

class XRayTextTestModule(private val module: String) : TestSuite {
    override val name: String by lazy { module.substringBefore("\n").substringAfter("Module ") }

    private val testCasesList by lazy {
        module.substringAfter("\n").split("\n-- ").mapNotNull {
            if (it.startsWith("ERROR: "))
                null
            else
                XRayTextTest(it)
        }
    }

    override val testCases: Sequence<TestCase>
        get() = testCasesList.asSequence()

    override val error: Throwable? by lazy {
        val error = module.substringAfter("\n")
        val start = error.indexOf("<error:error ")
        val end = error.indexOf("</error:error>")
        if (error.startsWith("ERROR: ") && start != -1)
            error.substring(start, end + 14).toMarkLogicQueryError(null)
        else
            null
    }
}
