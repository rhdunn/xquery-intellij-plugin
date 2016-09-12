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
package uk.co.reecedunn.intellij.plugin.xquery.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum XQueryVersion {
    VERSION_0_9_MARKLOGIC("0.9-ml"),
    VERSION_1_0("1.0"),
    VERSION_1_0_MARKLOGIC("1.0-ml"),
    VERSION_3_0("3.0"),
    VERSION_3_1("3.1");

    private final String mID;

    XQueryVersion(@NotNull String id) {
        mID = id;
    }

    @Nullable
    public static XQueryVersion parse(@Nullable CharSequence value) {
        if ("0.9-ml".equals(value)) return VERSION_0_9_MARKLOGIC;
        if ("1.0".equals(value)) return VERSION_1_0;
        if ("1.0-ml".equals(value)) return VERSION_1_0_MARKLOGIC;
        if ("3.0".equals(value)) return VERSION_3_0;
        if ("3.1".equals(value)) return VERSION_3_1;
        return null;
    }

    @Override
    public String toString() {
        return mID;
    }
}
