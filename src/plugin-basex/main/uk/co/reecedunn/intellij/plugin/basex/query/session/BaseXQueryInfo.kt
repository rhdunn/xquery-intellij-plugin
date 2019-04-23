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

fun String.toBaseXInfo(): Map<String, Any> {
    val info = HashMap<String, Any>()
    split("(\r\n|\r|\n)".toRegex()).forEach { block ->
        when {
            block.contains(": ") -> {
                val data = block.split(": ")
                if (data[1].endsWith(" ms")) {
                    info[data[0]] = XsDuration.ms(data[1].substringBefore(" ms"))
                } else {
                    info[data[0]] = data[1]
                }
            }
            block.startsWith("Query executed in ") -> {
                info["Total Time"] = XsDuration.ms(block.subSequence(18, block.length - 4).toString())
            }
        }
    }
    return info
}
