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

import java.util.HashSet;
import java.util.Set;

public enum XQueryDialect {
    XQUERY_0_9_MARKLOGIC_3_2("0.9-ml/3.2",
        XQueryVersion.XQUERY_0_9_MARKLOGIC,
        XQueryBundle.message("xquery.dialect.name.0.9-ml/3.2")),
    XQUERY_1_0_FULL_TEXT_W3C("1.0+full-text/W3C",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0+full-text/W3C")),
    XQUERY_1_0_SEMANTICS_W3C("1.0+formal-semantics/W3C",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0+formal-semantics/W3C")),
    XQUERY_1_0_2ED_SEMANTICS_W3C("1.0+formal-semantics/W3C-2ed",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0+formal-semantics/W3C-2ed")),
    XQUERY_1_0_UPDATE_W3C("1.0+update/W3C",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0+update/W3C")),
    XQUERY_1_0_W3C("1.0/W3C",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0/W3C")),
    XQUERY_1_0_2ED_W3C("1.0/W3C-2ed",
        XQueryVersion.XQUERY_1_0,
        XQueryBundle.message("xquery.dialect.name.1.0/W3C-2ed")),
    XQUERY_1_0_MARKLOGIC_7("1.0-ml/7",
        XQueryVersion.XQUERY_1_0_MARKLOGIC,
        XQueryBundle.message("xquery.dialect.name.1.0-ml/7")),
    XQUERY_1_0_MARKLOGIC_8("1.0-ml/8",
        XQueryVersion.XQUERY_1_0_MARKLOGIC,
        XQueryBundle.message("xquery.dialect.name.1.0-ml/8")),
    XQUERY_3_0_FULL_TEXT_W3C("3.0+full-text/W3C",
        XQueryVersion.XQUERY_3_0,
        XQueryBundle.message("xquery.dialect.name.3.0+full-text/W3C")),
    XQUERY_3_0_UPDATE_W3C("3.0+update/W3C",
        XQueryVersion.XQUERY_3_0,
        XQueryBundle.message("xquery.dialect.name.3.0+update/W3C")),
    XQUERY_3_0_W3C("3.0/W3C",
        XQueryVersion.XQUERY_3_0,
        XQueryBundle.message("xquery.dialect.name.3.0/W3C")),
    XQUERY_3_1_W3C("3.1/W3C",
        XQueryVersion.XQUERY_3_1,
        XQueryBundle.message("xquery.dialect.name.3.1/W3C"));

    private final String mID;
    private final String mName;
    private final XQueryVersion mVersion;
    private final Set<XQuerySpecification> mConformsTo = new HashSet<>();

    static {
        XQUERY_0_9_MARKLOGIC_3_2.mConformsTo.add(XQuerySpecification.XQUERY_1_0_20030502);
        XQUERY_1_0_FULL_TEXT_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_1_0_SEMANTICS_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_1_0_2ED_SEMANTICS_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0_20101214);
        XQUERY_1_0_UPDATE_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_1_0_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_1_0_2ED_W3C.mConformsTo.add(XQuerySpecification.XQUERY_1_0_20101214);
        XQUERY_1_0_MARKLOGIC_7.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_1_0_MARKLOGIC_8.mConformsTo.add(XQuerySpecification.XQUERY_1_0);
        XQUERY_3_0_FULL_TEXT_W3C.mConformsTo.add(XQuerySpecification.XQUERY_3_0);
        XQUERY_3_0_UPDATE_W3C.mConformsTo.add(XQuerySpecification.XQUERY_3_0);
        XQUERY_3_0_W3C.mConformsTo.add(XQuerySpecification.XQUERY_3_0);
        XQUERY_3_1_W3C.mConformsTo.add(XQuerySpecification.XQUERY_3_1);

        XQUERY_1_0_SEMANTICS_W3C.mConformsTo.add(XQuerySpecification.SEMANTICS_1_0);
        XQUERY_1_0_2ED_SEMANTICS_W3C.mConformsTo.add(XQuerySpecification.SEMANTICS_1_0_20101214);

        XQUERY_1_0_FULL_TEXT_W3C.mConformsTo.add(XQuerySpecification.FULL_TEXT_1_0);
        XQUERY_3_0_FULL_TEXT_W3C.mConformsTo.add(XQuerySpecification.FULL_TEXT_3_0);

        XQUERY_1_0_UPDATE_W3C.mConformsTo.add(XQuerySpecification.UPDATE_1_0);
        XQUERY_3_0_UPDATE_W3C.mConformsTo.add(XQuerySpecification.UPDATE_3_0);
    }

    XQueryDialect(@NotNull String id, @NotNull XQueryVersion version, @NotNull String name) {
        mID = id;
        mName = name;
        mVersion = version;
    }

    @NotNull
    public String getName() {
        return mName;
    }

    @NotNull
    public XQueryVersion getLanguageVersion() {
        return mVersion;
    }

    public boolean conformsTo(XQuerySpecification specification) {
        return mConformsTo.contains(specification);
    }

    @Nullable
    public static XQueryDialect parse(@Nullable String value) {
        if ("0.9-ml/3.2".equals(value)) return XQUERY_0_9_MARKLOGIC_3_2;
        if ("1.0/W3C".equals(value)) return XQUERY_1_0_W3C;
        if ("1.0/W3C-2ed".equals(value)) return XQUERY_1_0_2ED_W3C;
        if ("1.0+full-text/W3C".equals(value)) return XQUERY_1_0_FULL_TEXT_W3C;
        if ("1.0+formal-semantics/W3C".equals(value)) return XQUERY_1_0_SEMANTICS_W3C;
        if ("1.0+formal-semantics/W3C-2ed".equals(value)) return XQUERY_1_0_2ED_SEMANTICS_W3C;
        if ("1.0+update/W3C".equals(value)) return XQUERY_1_0_UPDATE_W3C;
        if ("1.0-ml/7".equals(value)) return XQUERY_1_0_MARKLOGIC_7;
        if ("1.0-ml/8".equals(value)) return XQUERY_1_0_MARKLOGIC_8;
        if ("3.0/W3C".equals(value)) return XQUERY_3_0_W3C;
        if ("3.0+full-text/W3C".equals(value)) return XQUERY_3_0_FULL_TEXT_W3C;
        if ("3.0+update/W3C".equals(value)) return XQUERY_3_0_UPDATE_W3C;
        if ("3.1/W3C".equals(value)) return XQUERY_3_1_W3C;
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return mID;
    }
}
