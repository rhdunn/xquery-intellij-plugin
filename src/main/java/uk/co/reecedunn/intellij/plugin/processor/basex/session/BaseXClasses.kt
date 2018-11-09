/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.basex.session

import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader

internal class BaseXClasses(path: File) {
    val contextClass: Class<*>
    val localSessionClass: Class<*>
    val clientSessionClass: Class<*>
    val localQueryClass: Class<*>
    val clientQueryClass: Class<*>
    val basexExceptionClass: Class<*>

    init {
        val loader = URLClassLoader(arrayOf(path.toURI().toURL()))
        contextClass = loader.loadClass("org.basex.core.Context")
        localSessionClass = loader.loadClassOrNull("org.basex.api.client.LocalSession")
                ?: loader.loadClass("org.basex.server.LocalSession")
        clientSessionClass = loader.loadClassOrNull("org.basex.api.client.ClientSession")
                ?: loader.loadClass("org.basex.server.ClientSession")
        localQueryClass = loader.loadClassOrNull("org.basex.api.client.LocalQuery")
                ?: loader.loadClass("org.basex.server.LocalQuery")
        clientQueryClass = loader.loadClassOrNull("org.basex.api.client.ClientQuery")
                ?: loader.loadClass("org.basex.server.ClientQuery")
        basexExceptionClass = loader.loadClass("org.basex.core.BaseXException")
    }

    fun <T> check(f: () -> T): T {
        return try {
            f()
        } catch (e: InvocationTargetException) {
            if (basexExceptionClass.isInstance(e.targetException)) {
                throw BaseXQueryError(e.targetException.message!!)
            } else if (e.targetException is RuntimeException && e.targetException.message == "Not Implemented.") {
                throw UnsupportedOperationException()
            } else {
                throw e
            }
        }
    }
}
