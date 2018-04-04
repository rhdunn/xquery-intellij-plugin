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
package uk.co.reecedunn.intellij.plugin.xdm

import org.jetbrains.annotations.PropertyKey
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FnErrorObject
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmTypeCastResult
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

val FnError = XmlSchemaType(createQName("http://www.w3.org/2005/xpath-functions", "error"), XsAnyType)

fun createCastError(code: QName,
                    @PropertyKey(resourceBundle = "messages.XQueryBundle") description: String,
                    vararg params: Any): XdmTypeCastResult {
    val message = XQueryBundle.message(description, *params)
    return XdmTypeCastResult(FnErrorObject(code, createString(message)), FnError)
}
