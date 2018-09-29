/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.resources

import org.apache.xmlbeans.impl.common.IOUtil
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

object Resources {
    @Throws(IOException::class)
    fun streamToString(stream: InputStream): String {
        val writer = StringWriter()
        IOUtil.copyCompletely(InputStreamReader(stream), writer)
        return writer.toString()
    }

    fun load(resource: String): InputStream? {
        val loader = Resources::class.java.classLoader
        return loader.getResourceAsStream(resource)
    }
}
