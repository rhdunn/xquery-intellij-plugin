/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.model

interface XdmExpression {
    val staticType: XdmSequenceType
}

interface XdmSimpleExpression : XdmExpression {
    val constantValue: Any?
}

interface XdmAtomicValue : XdmExpression {
    val lexicalRepresentation: String
}

fun XdmAtomicValue.toInt(): Int {
    return lexicalRepresentation.toInt()
}
