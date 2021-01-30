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
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestFailure
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.toXsDuration

class XRayJsonTest(private val test: JsonObject) : TestCase {
    override val name: String by lazy { test.get("name").asString }

    override val result: TestResult by lazy { TestResult.value(test.get("result").asString) }

    override val duration: XsDurationValue? by lazy { test.get("time").asString.toXsDuration() }

    private val failuresList by lazy {
        test.getAsJsonArray("assert").map { XRayJsonTestAssert(it.asJsonObject) }.toList()
    }

    override val failures: Sequence<TestFailure>
        get() = failuresList.asSequence()

    override val error: Throwable? by lazy { test.getAsJsonObject("error").toMarkLogicQueryError(null) }
}
