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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format

import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.html.XRayHtmlFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.json.XRayJsonFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.text.XRayTextFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xunit.XRayXUnitFormat
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat

object XRayTestFormat {
    fun format(id: String): TestFormat = when (id) {
        XRayHtmlFormat.id -> XRayHtmlFormat
        XRayJsonFormat.id -> XRayJsonFormat
        XRayTextFormat.id -> XRayTextFormat
        XRayXmlFormat.id -> XRayXmlFormat
        XRayXUnitFormat.id -> XRayXUnitFormat
        else -> throw UnsupportedOperationException("Unsupported test format '$id'")
    }
}
