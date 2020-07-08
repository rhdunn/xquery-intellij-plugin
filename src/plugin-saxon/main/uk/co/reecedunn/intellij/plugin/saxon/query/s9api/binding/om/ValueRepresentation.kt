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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmValue

class ValueRepresentation(`object`: Any, `class`: Class<*>) : Sequence(`object`, `class`) {
    override fun getXdmValue(): XdmValue {
        val xdmValueClass = saxonClass.classLoader.loadClass("net.sf.saxon.s9api.XdmValue")
        val wrap = xdmValueClass.getDeclaredMethod("wrap", saxonClass)
        wrap.isAccessible = true // XdmValue.wrap is protected.
        return XdmValue(wrap.invoke(null, `object`), xdmValueClass)
    }
}
