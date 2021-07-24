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

import com.google.gson.JsonParser
import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites

object XRayJsonFormat : TestFormat {
    override val id: String = "json"

    override val name: String = "JSON"

    override val language: Language by lazy { JsonLanguage.INSTANCE }

    override fun parse(result: QueryResult): TestSuites? {
        if (result.mimetype != QueryResult.APPLICATION_JSON) return null
        val doc = JsonParser.parseString(result.value as String)
        return XRayJsonTests(doc.asJsonObject.getAsJsonObject("tests"))
    }

    override fun toString(): String = name
}
