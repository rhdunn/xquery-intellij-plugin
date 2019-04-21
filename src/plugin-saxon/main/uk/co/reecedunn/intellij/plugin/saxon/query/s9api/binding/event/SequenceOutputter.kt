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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.event

import uk.co.reecedunn.intellij.plugin.core.reflection.getConstructorOrNull

class SequenceOutputter(pipe: Any, classLoader: ClassLoader) : Receiver {
    private val `class` = classLoader.loadClass("net.sf.saxon.event.SequenceOutputter")
    override val saxonObject: Any

    init {
        val pipelineConfigurationClass = classLoader.loadClass("net.sf.saxon.event.PipelineConfiguration")
        val constructor = `class`.getConstructorOrNull(pipelineConfigurationClass)
        if (constructor != null) { // Saxon >= 9.4
            saxonObject = constructor.newInstance(pipe)
        } else { // Saxon <= 9.3
            saxonObject = `class`.getConstructor().newInstance()
        }
    }

    fun close() {
        `class`.getMethod("close").invoke(saxonObject)
    }

    fun getSequence(): Any {
        return `class`.getMethod("getSequence").invoke(saxonObject)
    }
}
