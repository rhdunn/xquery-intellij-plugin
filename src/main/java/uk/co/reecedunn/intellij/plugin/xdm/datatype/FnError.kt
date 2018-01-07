/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.datatype

import uk.co.reecedunn.intellij.plugin.xdm.createQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue

/**
 * Unspecified error.
 */
val FOER0000 = createQName("http://www.w3.org/2005/xqt-errors", "FOER0000")

/**
 * Cannot cast to 'type'.
 */
val FORG0001 = createQName("http://www.w3.org/2005/xqt-errors", "FORG0001")

/**
 * Cannot use 'type' here.
 */
val XPTY0004 = createQName("http://www.w3.org/2005/xqt-errors", "XPTY0004")

data class FnErrorObject(val code: QName,
                         val description: XdmStaticValue?,
                         val errorObject: List<XdmStaticValue>) {

    constructor(): this(FOER0000, null, listOf())
    constructor(code: QName): this(code, null, listOf())
    constructor(code: QName, description: XdmStaticValue): this(code, description, listOf())
}
