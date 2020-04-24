/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.profiler

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.QueryStackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private val XMLSCHEMA_DATETIME_FORMAT: DateFormat by lazy {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    format
}

fun Map<String, Any>.toFlatProfileReport(queryFile: VirtualFile): FlatProfileReport {
    return FlatProfileReport(
        xml = null,
        elapsed = this["Total Time"] as XsDurationValue,
        created = XMLSCHEMA_DATETIME_FORMAT.format(Date()),
        version = "",
        results = asSequence().map { (key, value) ->
            (value as? XsDurationValue)?.let {
                FlatProfileEntry(
                    id = key,
                    context = key,
                    count = 1,
                    selfTime = if (key == "Total Time") XsDuration.ZERO else it,
                    totalTime = it,
                    frame = QueryStackFrame(queryFile, 0, 0)
                )
            }
        }.filterNotNull()
    )
}
