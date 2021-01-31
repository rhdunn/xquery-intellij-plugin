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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.json

import com.google.gson.JsonObject
import uk.co.reecedunn.intellij.plugin.core.gson.getOrNull
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestStatistics
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite

class XRayJsonTestModule(private val module: JsonObject) : TestSuite, TestStatistics {
    override val name: String by lazy { module.get("path").asString }

    override val total: Int by lazy { module.get("total").asString.toInt() }

    override val passed: Int by lazy { module.get("passed").asString.toInt() }

    override val ignored: Int by lazy { module.get("ignored").asString.toInt() }

    override val failed: Int by lazy { module.get("failed").asString.toInt() }

    override val errors: Int by lazy {
        val error = module.get("error")
        when {
            error.isJsonPrimitive -> module.get("error").asString.toInt()
            error.isJsonObject -> 1
            else -> 0
        }
    }

    private val testCasesList by lazy {
        module.getOrNull("test")?.asJsonArray?.map { XRayJsonTest(it.asJsonObject) }?.toList() ?: listOf()
    }

    override val testCases: Sequence<TestCase>
        get() = testCasesList.asSequence()

    override val error: Throwable? by lazy {
        val error = module.get("error")
        when {
            error.isJsonObject -> error.asJsonObject.toMarkLogicQueryError(null)
            else -> null
        }
    }
}
