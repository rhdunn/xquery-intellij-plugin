/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

object XmlSchemaDataTypeTokenType {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    val UNKNOWN = IElementType("XSDM_UNNKNOWN_TOKEN", XQuery)

    val EMPTY_ENTITY_REFERENCE = IElementType("XSDM_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery)
    val PARTIAL_ENTITY_REFERENCE = IElementType("XSDM_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery)
    val PREDEFINED_ENTITY_REFERENCE = IElementType("XSDM_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery)
    val CHARACTER_REFERENCE = IElementType("XSDM_CHARACTER_REFERENCE_TOKEN", XQuery)

    val NCNAME = IElementType("XSDM_NCNAME_TOKEN", XQuery)
}
