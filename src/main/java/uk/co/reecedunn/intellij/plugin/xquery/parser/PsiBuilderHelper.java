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
package uk.co.reecedunn.intellij.plugin.xquery.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiBuilderHelper {
    private final PsiBuilder mBuilder;

    public PsiBuilderHelper(@NotNull PsiBuilder builder) {
        mBuilder = builder;
    }

    public PsiBuilder.Marker mark() {
        return mBuilder.mark();
    }

    public IElementType getTokenType() {
        return mBuilder.getTokenType();
    }

    public void advanceLexer() {
        mBuilder.advanceLexer();
    }

    public void error(String message) {
        mBuilder.error(message);
    }
}
