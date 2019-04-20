/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.profiler

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.TraceListener
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDecimal
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration
import uk.co.reecedunn.intellij.plugin.xpath.model.XsInteger
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

private val XMLSCHEMA_DATETIME_FORMAT: DateFormat by lazy {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    format
}

class SaxonProfileInstruction(
    val instruction: InstructionInfo,
    var deepTime: Long,
    var count: Int = 1
)

class SaxonProfileTraceListener(val version: String, val query: VirtualFile) : TraceListener {
    var elapsed: Long = 0
    var created: Date? = null

    val instructions: Stack<SaxonProfileInstruction> = Stack()
    val results: HashMap<InstructionInfo, SaxonProfileInstruction> = HashMap()

    override fun setOutputDestination(logger: Any) {
    }

    override fun open(controller: Any) {
        elapsed = System.nanoTime()
        created = Date()
    }

    override fun close() {
        elapsed = System.nanoTime() - elapsed
    }

    override fun enter(instruction: InstructionInfo, context: Any) {
        // Some instructions (e.g. ClauseInfo) don't provide column information,
        // and generate different instances for each iteration. Ignore these to
        // prevent noise in the profile report.
        if (instruction.getColumnNumber() == -1) return

        instructions.push(SaxonProfileInstruction(instruction, System.nanoTime()))
    }

    override fun leave(instruction: InstructionInfo) {
        // Some instructions (e.g. ClauseInfo) don't provide column information,
        // and generate different instances for each iteration. Ignore these to
        // prevent noise in the profile report.
        if (instruction.getColumnNumber() == -1) return

        val current = instructions.pop()
        current.deepTime = System.nanoTime() - current.deepTime

        val result = results[instruction]
        if (result == null) {
            results[instruction] = current
        } else {
            result.deepTime += current.deepTime
            result.count += 1
        }
    }

    override fun startCurrentItem(currentItem: Any) {
    }

    override fun endCurrentItem(currentItem: Any) {
    }

    override fun startRuleSearch() {
    }

    override fun endRuleSearch(rule: Any, mode: Any, item: Any) {
    }
}

fun SaxonProfileInstruction.toProfileEntry(query: VirtualFile): ProfileEntry {
    val deepTimeDuration = XsDuration(XsInteger(BigInteger.ZERO), XsDecimal(BigDecimal.valueOf(deepTime, 9)))
    return ProfileEntry(
        id = instruction.hashCode().toString(),
        expression = instruction.getObjectName()?.toString() ?: "",
        count = count,
        shallowTime = deepTimeDuration,
        deepTime = deepTimeDuration,
        frame = StackFrame(
            instruction.getSystemId().nullize()?.let { DatabaseModule(it) } ?: query,
            instruction.getLineNumber(),
            instruction.getColumnNumber()
        )
    )
}

fun SaxonProfileTraceListener.toProfileReport(): ProfileReport {
    return ProfileReport(
        xml = null,
        elapsed = XsDuration(XsInteger(BigInteger.ZERO), XsDecimal(BigDecimal.valueOf(elapsed, 9))),
        created = created?.let { XMLSCHEMA_DATETIME_FORMAT.format(it) } ?: "",
        version = version,
        results = results.values.asSequence().map { result -> result.toProfileEntry(query) }
    )
}
