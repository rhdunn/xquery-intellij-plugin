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
package uk.co.reecedunn.intellij.plugin.xdm.documentation

import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

interface XdmDocumentation {
    val moduleTypes: Array<XdmModuleType>

    fun href(moduleType: XdmModuleType): String?

    val summary: String?

    val notes: String?

    fun examples(moduleType: XdmModuleType): Sequence<String>
}

interface XdmFunctionDocumentation : XdmDocumentation {
    val operatorMapping: String?

    val signatures: String?

    val properties: String?

    val privileges: String?

    val rules: String?

    val errorConditions: String?
}

fun XdmDocumentation.sections(moduleType: XdmModuleType): String {
    val sections = sequenceOf(
        "Summary" to summary,
        "Operator Mapping" to (this as? XdmFunctionDocumentation)?.operatorMapping,
        "Signatures" to (this as? XdmFunctionDocumentation)?.signatures,
        "Properties" to (this as? XdmFunctionDocumentation)?.properties,
        "Required Privileges" to (this as? XdmFunctionDocumentation)?.privileges,
        "Rules" to (this as? XdmFunctionDocumentation)?.rules,
        "Error Conditions" to (this as? XdmFunctionDocumentation)?.errorConditions,
        "Notes" to notes,
        "Examples" to examples(moduleType).joinToString("\n").nullize()
    ).filter { it.second != null }
    return "<dl>${sections.joinToString("") { "<dt>${it.first}</dt><dd>${it.second}</dd>" }}</dl>"
}
