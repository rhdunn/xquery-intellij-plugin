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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import java.lang.reflect.Proxy

interface TraceListener {
    fun setOutputDestination(logger: Any)

    fun open(controller: Any?)

    fun close()

    fun enter(instruction: InstructionInfo, context: Any)

    fun leave(instruction: InstructionInfo)

    fun startCurrentItem(currentItem: Any)

    fun endCurrentItem(currentItem: Any)

    fun startRuleSearch()

    fun endRuleSearch(rule: Any, mode: Any, item: Any)
}

fun TraceListener.proxy(vararg classes: Class<*>): Any {
    val classLoader = classes[0].classLoader
    val instructionInfoClass = classLoader.loadClass("net.sf.saxon.trace.InstructionInfo")
    return Proxy.newProxyInstance(classLoader, classes) { _, method, params ->
        when (method.name) {
            "setOutputDestination" -> setOutputDestination(params[0])
            "open" -> open(params?.getOrNull(0))
            "close" -> close()
            "enter" -> enter(InstructionInfo(params[0], instructionInfoClass), params[1])
            "leave" -> leave(InstructionInfo(params[0], instructionInfoClass))
            "startCurrentItem" -> startCurrentItem(params[0])
            "endCurrentItem" -> endCurrentItem(params[0])
            // TraceListener2 (Saxon 9.7+)
            "startRuleSearch" -> startRuleSearch()
            "endRuleSearch" -> endRuleSearch(params[0], params[1], params[2])
        }
    }
}
