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
package uk.co.reecedunn.intellij.plugin.xquery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LanguageLevel {
    XQUERY_1_0("1.0"),
    XQUERY_3_0("3.0"),
    XQUERY_3_1("3.1");

    private String mName;

    LanguageLevel(@NotNull String name) {
        mName = name;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @Nullable
    public static LanguageLevel parse(@Nullable String value) {
        if ("1.0".equals(value)) return XQUERY_1_0;
        if ("3.0".equals(value)) return XQUERY_3_0;
        if ("3.1".equals(value)) return XQUERY_3_1;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
