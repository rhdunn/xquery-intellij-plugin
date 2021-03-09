/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lang

import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmLanguageVersion

/**
 * XML Query Language
 */
@Suppress("MemberVisibilityCanBePrivate")
object XQuery : Language(XPath, "XQuery", "application/xquery") {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    // endregion
    // region Versions

    val VERSION_0_9_ML: XpmLanguageVersion = XQueryVersion(
        "0.9-ml",
        listOf(XQuerySpec.WD_1_0_20030502)
    )

    val VERSION_1_0: XpmLanguageVersion = XQueryVersion(
        "1.0",
        listOf(XQuerySpec.REC_1_0_20070123, XQuerySpec.REC_1_0_20101214, XQuerySpec.WD_1_0_20030502)
    )

    val VERSION_1_0_ML: XpmLanguageVersion = XQueryVersion(
        "1.0-ml",
        listOf(XQuerySpec.REC_1_0_20070123)
    )

    val VERSION_3_0: XpmLanguageVersion = XQueryVersion(
        "3.0",
        listOf(XQuerySpec.REC_3_0_20140408)
    )

    val VERSION_3_1: XpmLanguageVersion = XQueryVersion(
        "3.1",
        listOf(XQuerySpec.REC_3_1_20170321, XQuerySpec.CR_3_1_20151217)
    )

    val VERSION_4_0: XpmLanguageVersion = XQueryVersion(
        "4.0",
        listOf(XQuerySpec.ED_4_0_20210113)
    )

    val versions: Map<String, XpmLanguageVersion> = listOf(
        VERSION_0_9_ML,
        VERSION_1_0,
        VERSION_1_0_ML,
        VERSION_3_0,
        VERSION_3_1,
        VERSION_4_0
    ).associateBy { it.version }

    // endregion
}
