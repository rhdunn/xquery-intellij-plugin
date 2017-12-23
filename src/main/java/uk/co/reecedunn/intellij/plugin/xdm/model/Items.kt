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
/*
 * XPath and XQuery Type System Part 1: Items
 *
 * Reference: https://www.w3.org/TR/xpath-datamodel-31
 * Reference: https://www.w3.org/TR/2012/REC-xmlschema11-1-20120405
 */
package uk.co.reecedunn.intellij.plugin.xdm.model

import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped

interface XdmItem: XdmSequenceType

open class XdmFunction: XdmItem {
    override val itemType get(): XdmSequenceType = XsUntyped
    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
}

open class XdmMap: XdmFunction()

open class XdmArray: XdmFunction()

open class XdmNode: XdmItem {
    override val itemType get(): XdmSequenceType = XsUntyped
    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
}

open class XdmAttribute: XdmNode()

open class XdmComment: XdmNode()

open class XdmDocument: XdmNode()

open class XdmElement: XdmNode()

open class XdmNamespace: XdmNode()

open class XdmProcessingInstruction: XdmNode()

open class XdmText: XdmNode()
