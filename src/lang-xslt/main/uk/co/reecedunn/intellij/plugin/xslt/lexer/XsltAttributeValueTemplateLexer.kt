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

import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer

class XsltAttributeValueTemplateLexer(tokenRange: CodePointRange) : XPathLexer(tokenRange) {
    override fun stateDefault() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER -> {
                        mType = XsltSchemaTypesTokenType.ATTRIBUTE_VALUE_CONTENTS
                        return
                    }
                    else -> {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                }
            }
        }
    }
}
