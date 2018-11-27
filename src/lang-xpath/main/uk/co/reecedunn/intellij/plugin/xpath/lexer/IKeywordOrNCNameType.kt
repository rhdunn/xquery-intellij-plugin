/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lexer

import com.intellij.lang.Language
import org.jetbrains.annotations.NonNls

class IKeywordOrNCNameType(@NonNls debugName: String, language: Language, type: KeywordType = KeywordType.KEYWORD) :
    INCNameType(debugName, language) {

    val keywordType: KeywordType = type

    enum class KeywordType {
        KEYWORD,
        RESERVED_FUNCTION_NAME,
        XQUERY30_RESERVED_FUNCTION_NAME,
        SCRIPTING10_RESERVED_FUNCTION_NAME,
        MARKLOGIC70_RESERVED_FUNCTION_NAME,
        MARKLOGIC80_RESERVED_FUNCTION_NAME
    }
}