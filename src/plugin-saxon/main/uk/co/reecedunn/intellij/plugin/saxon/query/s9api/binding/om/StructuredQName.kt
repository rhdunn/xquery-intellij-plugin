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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om

import uk.co.reecedunn.intellij.plugin.core.reflection.getAnyMethod
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.QName

class StructuredQName(`object`: Any, `class`: Class<*>) :
    QName(`object`, `class`) {

    override fun getNamespaceURI(): String {
        return saxonClass.getAnyMethod("getURI", "getNamespaceURI").invoke(`object`) as String
    }

    override fun getLocalName(): String {
        return saxonClass.getAnyMethod("getLocalPart", "getLocalName").invoke(`object`) as String
    }
}
