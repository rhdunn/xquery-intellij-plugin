/*
 * Copyright (C) 2018, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.context

import uk.co.reecedunn.intellij.plugin.xdm.resources.XdmBundle
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType

enum class XpmUsageType(val label: String, val namespaceType: XdmNamespaceType) {
    Annotation(XdmBundle.message("usage-type.annotation"), XdmNamespaceType.XQuery),
    Attribute(XdmBundle.message("usage-type.attribute"), XdmNamespaceType.None),
    DecimalFormat(XdmBundle.message("usage-type.decimal-format"), XdmNamespaceType.None),
    Element(XdmBundle.message("usage-type.element"), XdmNamespaceType.DefaultElement),
    FunctionDecl(XdmBundle.message("usage-type.function"), XdmNamespaceType.DefaultFunctionDecl),
    FunctionRef(XdmBundle.message("usage-type.function"), XdmNamespaceType.DefaultFunctionRef),
    MapKey(XdmBundle.message("usage-type.map-key"), XdmNamespaceType.None),
    Namespace(XdmBundle.message("usage-type.namespace"), XdmNamespaceType.None),
    Option(XdmBundle.message("usage-type.option"), XdmNamespaceType.XQuery),
    Parameter(XdmBundle.message("usage-type.parameter"), XdmNamespaceType.None),
    Pragma(XdmBundle.message("usage-type.pragma"), XdmNamespaceType.None),
    ProcessingInstruction(XdmBundle.message("usage-type.processing-instruction"), XdmNamespaceType.None),
    Type(XdmBundle.message("usage-type.type"), XdmNamespaceType.DefaultType),
    Unknown(XdmBundle.message("usage-type.identifier"), XdmNamespaceType.Undefined),
    Variable(XdmBundle.message("usage-type.variable"), XdmNamespaceType.None)
}
