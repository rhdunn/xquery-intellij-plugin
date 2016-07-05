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
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;

public enum XQueryDialect {
    XQUERY_1_0_W3C("1.0/W3C", XQueryVersion.XQUERY_1_0, XQueryBundle.message("xquery.variant.description.1.0/W3C"), "https://www.w3.org/TR/2007/REC-xquery-20070123/"),
    XQUERY_3_0_W3C("3.0/W3C", XQueryVersion.XQUERY_3_0, XQueryBundle.message("xquery.variant.description.3.0/W3C"), "https://www.w3.org/TR/2014/REC-xquery-30-20140408/"),
    XQUERY_3_1_W3C("3.1/W3C", XQueryVersion.XQUERY_3_1, XQueryBundle.message("xquery.variant.description.3.1/W3C"), "https://www.w3.org/TR/2015/CR-xquery-31-20151217/");

    private final String mName;
    private final XQueryVersion mVersion;
    private final String mDescription;
    private final String mReference;

    XQueryDialect(@NotNull String name, @NotNull XQueryVersion version, @NotNull String description, @NotNull String reference) {
        mName = name;
        mVersion = version;
        mDescription = description;
        mReference = reference;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @NotNull
    public XQueryVersion getLanguageVersion() {
        return mVersion;
    }

    @NotNull
    public String getDescription() {
        return mDescription;
    }

    @NotNull
    public String getReference() {
        return mReference;
    }

    @Nullable
    public static XQueryDialect parse(@Nullable String value) {
        if ("1.0/W3C".equals(value)) return XQUERY_1_0_W3C;
        if ("3.0/W3C".equals(value)) return XQUERY_3_0_W3C;
        if ("3.1/W3C".equals(value)) return XQUERY_3_1_W3C;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
