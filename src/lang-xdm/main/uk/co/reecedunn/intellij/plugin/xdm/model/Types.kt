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
package uk.co.reecedunn.intellij.plugin.xdm.model

interface XdmSequenceType {
    val typeName: String

    val itemType: XdmItemType?

    val lowerBound: Int?

    val upperBound: Int?
}

interface XdmItemType : XdmSequenceType {
    val typeClass: Class<*>
}

interface XdmSequenceTypeList : XdmSequenceType {
    val types: Sequence<XdmSequenceType>
}

interface XdmSequenceTypeUnion : XdmSequenceType {
    val types: Sequence<XdmSequenceType>
}

object XdmSingleItemType : XdmItemType {
    override val typeName: String = "item()"
    override val itemType: XdmItemType? = this
    override val lowerBound: Int? = 1
    override val upperBound: Int? = 1
    override val typeClass: Class<*> = XdmItem::class.java
}

object XdmEmptySequence : XdmSequenceType {
    override val typeName: String = "empty-sequence()"
    override val itemType: XdmItemType? = null
    override val lowerBound: Int? = 0
    override val upperBound: Int? = 0
}
