/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.optree.item

import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue

// region XQuery and XPath 3.1 Data Model (2.7.4) : attribute()

val XdmAttributeNode.localName: String?
    get() = nodeName?.localName?.data

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : map(*)

val XpmMapEntry.keyName: String?
    get() = when (val name = keyExpression) {
        is XsNCNameValue -> name.data
        else -> null
    }

// endregion
