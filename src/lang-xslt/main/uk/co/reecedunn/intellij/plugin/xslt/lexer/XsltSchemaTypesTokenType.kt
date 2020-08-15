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
package uk.co.reecedunn.intellij.plugin.xslt.lexer

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.XslAVT

object XsltSchemaTypesTokenType {
    // region xsl:avt

    val ATTRIBUTE_VALUE_CONTENTS: IElementType = IElementType("XSLT_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XslAVT)
    val ATTRIBUTE_ESCAPED_CHARACTER: IElementType = IElementType("XSLT_ATTRIBUTE_ESCAPED_CHARACTER_TOKEN", XslAVT)

    // endregion
}
