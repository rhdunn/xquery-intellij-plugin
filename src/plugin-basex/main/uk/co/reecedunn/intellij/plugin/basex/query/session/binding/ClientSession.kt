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

import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull

class ClientSession(classLoader: ClassLoader, hostname: String, port: Int, username: String?, password: String?) {
    private val `class`: Class<*> = classLoader.loadClassOrNull("org.basex.api.client.ClientSession")
        ?: classLoader.loadClass("org.basex.server.ClientSession")
    val `object`: Any

    init {
        val constructor = `class`.getConstructor(
            String::class.java, Int::class.java, String::class.java, String::class.java
        )
        `object` = constructor.newInstance(hostname, port, username, password)
    }

    fun query(query: String): Any {
        return `class`.getMethod("query", String::class.java).invoke(`object`, query)
    }

    fun close() {
        `class`.getMethod("close").invoke(`object`)
    }
}
