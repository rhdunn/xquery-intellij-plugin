/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.context

import uk.co.reecedunn.intellij.plugin.intellij.resources.XdmBundle

enum class XstUsageType(val label: String) {
    Annotation(XdmBundle.message("usage-type.annotation")),
    Attribute(XdmBundle.message("usage-type.attribute")),
    DecimalFormat(XdmBundle.message("usage-type.decimal-format")),
    Element(XdmBundle.message("usage-type.element")),
    Function(XdmBundle.message("usage-type.function")),
    Namespace(XdmBundle.message("usage-type.namespace")),
    Option(XdmBundle.message("usage-type.option")),
    Parameter(XdmBundle.message("usage-type.parameter")),
    Pragma(XdmBundle.message("usage-type.pragma")),
    Type(XdmBundle.message("usage-type.type")),
    Unknown(XdmBundle.message("usage-type.identifier")),
    Variable(XdmBundle.message("usage-type.variable"))
}
