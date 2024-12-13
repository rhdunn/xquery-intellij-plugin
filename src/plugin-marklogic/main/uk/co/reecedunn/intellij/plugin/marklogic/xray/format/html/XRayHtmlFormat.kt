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

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import org.jsoup.Jsoup
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites

object XRayHtmlFormat : TestFormat {
    override val id: String = "html"

    override val name: String = "HTML"

    override val language: Language by lazy { HTMLLanguage.INSTANCE }

    override fun parse(result: QueryResult): TestSuites? {
        if (result.mimetype != QueryResult.TEXT_HTML) return null
        val doc = Jsoup.parse(result.value as String)
        return XRayHtmlTests(doc.selectFirst("html > body")!!)
    }

    override fun toString(): String = name
}
