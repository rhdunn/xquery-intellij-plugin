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

class LocalSession(context: Context) {
    private val `class`: Class<*> =
        context.contextClass.classLoader.loadClassOrNull("org.basex.api.client.LocalSession")
            ?: context.contextClass.classLoader.loadClass("org.basex.server.LocalSession")
    private val `object`: Any = `class`.getConstructor(context.contextClass).newInstance(context.basexObject)

    fun query(query: String): LocalQuery {
        val localQueryClass: Class<*> = `class`.classLoader.loadClassOrNull("org.basex.api.client.LocalQuery")
            ?: `class`.classLoader.loadClass("org.basex.server.LocalQuery")
        return LocalQuery(`class`.getMethod("query", String::class.java).invoke(`object`, query), localQueryClass)
    }

    fun close() {
        `class`.getMethod("close").invoke(`object`)
    }
}
