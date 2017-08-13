/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IXQueryKeywordOrNCNameType extends INCNameType {
    private KeywordType mType;

    public enum KeywordType {
        KEYWORD,
        RESERVED_FUNCTION_NAME,
        XQUERY30_RESERVED_FUNCTION_NAME,
        SCRIPTING10_RESERVED_FUNCTION_NAME,
        MARKLOGIC70_RESERVED_FUNCTION_NAME,
        MARKLOGIC80_RESERVED_FUNCTION_NAME,
    }

    public IXQueryKeywordOrNCNameType(@NotNull @NonNls String debugName) {
        super(debugName);
        mType = KeywordType.KEYWORD;
    }

    public IXQueryKeywordOrNCNameType(@NotNull @NonNls String debugName, @NotNull KeywordType type) {
        super(debugName);
        mType = type;
    }

    @NotNull
    public KeywordType getKeywordType() {
        return mType;
    }
}