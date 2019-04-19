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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

object XdmNumeric {
    fun tryInstance(value: String, itemtype: String, classLoader: ClassLoader): XdmAtomicValue? {
        return try {
            XdmAtomicValue(value, itemtype, classLoader)
        } catch (e: Throwable) {
            null
        }
    }

    fun newInstance(value: String, classLoader: ClassLoader): XdmAtomicValue {
        return tryInstance(value, "xs:double", classLoader)
            ?: tryInstance(value, "xs:integer", classLoader)
            ?: XdmAtomicValue(value, "xs:decimal", classLoader)
    }
}
