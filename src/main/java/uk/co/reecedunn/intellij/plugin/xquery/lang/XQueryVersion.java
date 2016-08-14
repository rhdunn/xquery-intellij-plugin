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
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;

public enum XQueryVersion {
    XQUERY_0_9_MARKLOGIC("0.9-ml", XQueryBundle.message("xquery.version.description.0.9-ml"), "https://docs.marklogic.com/5.0/guide/xquery/dialects"),
    XQUERY_1_0("1.0", XQueryBundle.message("xquery.version.description.1.0"), "https://www.w3.org/TR/xquery/"),
    XQUERY_1_0_MARKLOGIC("1.0-ml", XQueryBundle.message("xquery.version.description.1.0-ml"), "https://docs.marklogic.com/5.0/guide/xquery/dialects"),
    XQUERY_3_0("3.0", XQueryBundle.message("xquery.version.description.3.0"), "https://www.w3.org/TR/xquery-30/"),
    XQUERY_3_1("3.1", XQueryBundle.message("xquery.version.description.3.1"), "https://www.w3.org/TR/xquery-31/");

    private final String mID;
    private final String mName;
    private final String mReference;

    XQueryVersion(@NotNull String id, @NotNull String name, @NotNull String reference) {
        mID = id;
        mName = name;
        mReference = reference;
    }

    public String getName() {
        return mName;
    }

    public String getReference() {
        return mReference;
    }

    @Nullable
    public static XQueryVersion parse(@Nullable CharSequence value) {
        if ("0.9-ml".equals(value)) return XQUERY_0_9_MARKLOGIC;
        if ("1.0".equals(value)) return XQUERY_1_0;
        if ("1.0-ml".equals(value)) return XQUERY_1_0_MARKLOGIC;
        if ("3.0".equals(value)) return XQUERY_3_0;
        if ("3.1".equals(value)) return XQUERY_3_1;
        return null;
    }

    @Override
    public String toString() {
        return mID;
    }
}
