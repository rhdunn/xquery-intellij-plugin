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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration

private fun String.toBaseXInfoBlocks(): Sequence<String> {
    return replace("\r\n", "\n").replace('\r', '\n').split("\n\n").asSequence().map {
        when {
            it.startsWith('\n') -> it.substring(1)
            it.endsWith('\n') -> it.substring(0, it.length - 1)
            else -> it
        }
    }
}

private fun String.parseBaseXInfoBlock(info: HashMap<String, Any>) {
    split('\n').forEach { row ->
        val data = row.split(": ")
        if (data[1].endsWith(" ms")) {
            info[data[0]] = XsDuration.ms(data[1].substringBefore(" ms"))
        } else {
            info[data[0]] = data[1]
        }
    }
}

fun String.toBaseXInfo(): Map<String, Any> {
    val info = HashMap<String, Any>()
    toBaseXInfoBlocks().forEach { block ->
        val part = block.substringBefore('\n')
        when {
            part.contains(": ") -> block.parseBaseXInfoBlock(info)
            part == "Query Plan:" -> {
                info["Query Plan"] = block.substringAfter('\n')
            }
            part == "Compiling:" -> {
                info["Compilation"] = block.substringAfter('\n').split('\n').map { it.substring(2) }
            }
            part == "Optimized Query:" -> {
                info["Optimized Query"] = block.substringAfter('\n')
            }
            block.startsWith("Query executed in ") -> {
                info["Total Time"] = XsDuration.ms(block.subSequence(18, block.length - 4).toString())
            }
        }
    }
    return info
}
