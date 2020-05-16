/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.lang

import uk.co.reecedunn.intellij.plugin.core.io.decode
import java.io.File
import java.io.FileInputStream

data class XpmSchemaFile(val location: String, val targetNamespace: String) {
    constructor(location: String) : this(location, getTargetNamespace(location))
    constructor(file: File) : this(file.canonicalPath)

    companion object {
        private val TARGET_NAMESPACE_RE = "[ \t\r\n]targetNamespace=[\"']([^\"']*)[\"']".toRegex()

        private fun getTargetNamespace(location: String): String {
            // NOTE: The Java XML processor does not handle comments in the prolog, so detect it manually.
            return FileInputStream(location).use {
                TARGET_NAMESPACE_RE.find(it.decode())?.groupValues?.get(1) ?: ""
            }
        }
    }
}
