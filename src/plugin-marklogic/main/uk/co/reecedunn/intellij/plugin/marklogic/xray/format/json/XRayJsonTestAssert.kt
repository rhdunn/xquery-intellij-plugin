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

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.processor.test.TestAssert
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult

class XRayJsonTestAssert(private val assertion: JsonObject) : TestAssert {
    private fun value(json: JsonElement): String? = when (json) {
        is JsonPrimitive -> json.asString
        else -> json.toString()
    }

    override val type: String by lazy { assertion.get("test").asString }

    override val result: TestResult by lazy { TestResult.value(assertion.get("result").asString) }

    override val expected: String? by lazy { value(assertion.get("expected")) }

    override val actual: String? by lazy { value(assertion.get("actual")) }

    override val message: String? by lazy { assertion.get("message").asString.nullize() }
}
