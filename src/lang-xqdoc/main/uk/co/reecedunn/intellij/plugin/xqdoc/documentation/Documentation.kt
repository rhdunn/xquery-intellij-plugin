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
package uk.co.reecedunn.intellij.plugin.xqdoc.documentation

import uk.co.reecedunn.intellij.plugin.xqdoc.resources.XQDocBundle

val XQDocDocumentation.sections: String
    get() {
        val sections = sequenceOf(
            XQDocBundle.message("section.summary") to summary,
            XQDocBundle.message("section.operator-mapping") to (this as? XQDocFunctionDocumentation)?.operatorMapping,
            XQDocBundle.message("section.signatures") to (this as? XQDocFunctionDocumentation)?.signatures,
            XQDocBundle.message("section.parameters") to (this as? XQDocFunctionDocumentation)?.parameters,
            XQDocBundle.message("section.properties") to (this as? XQDocFunctionDocumentation)?.properties,
            XQDocBundle.message("section.required-privileges") to (this as? XQDocFunctionDocumentation)?.privileges,
            XQDocBundle.message("section.rules") to (this as? XQDocFunctionDocumentation)?.rules,
            XQDocBundle.message("section.error-conditions") to (this as? XQDocFunctionDocumentation)?.errorConditions,
            XQDocBundle.message("section.notes") to notes,
            XQDocBundle.message("section.examples") to examples
        ).filter { it.second != null }
        return "<dl>${sections.joinToString("") { "<dt>${it.first}</dt><dd>${it.second}</dd>" }}</dl>"
    }
