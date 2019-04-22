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
package uk.co.reecedunn.intellij.plugin.basex.query.session.binding

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull

class LocalQuery(private val `object`: Any, private val `class`: Class<*>) : Query {
    override fun bind(name: String, value: Any?, type: String?) {
        // BaseX cannot bind to namespaced variables, so only pass the NCName.
        `class`.getMethod("bind", String::class.java, Any::class.java, String::class.java)
            .invoke(`object`, name, value, type)
    }

    override fun context(value: Any?, type: String?) {
        `class`.getMethod("context", Any::class.java, String::class.java).invoke(`object`, value, type)
    }

    override fun more(): Boolean {
        return `class`.getMethod("more").invoke(`object`) as Boolean
    }

    override fun next(): String? {
        return `class`.getMethod("next").invoke(`object`) as String?
    }

    override fun type(): Any? {
        return `class`.getMethodOrNull("type")?.invoke(`object`)
    }

    override fun info(): String? {
        return `class`.getMethod("info").invoke(`object`) as String?
    }

    override fun close() {
        `class`.getMethod("close").invoke(`object`)
    }
}
