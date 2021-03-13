/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.lang

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher
import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.fileTypes.LanguageFileType
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageData
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmLanguageVersion

/**
 * XML Stylesheet Language: Transform
 */
@Suppress("MemberVisibilityCanBePrivate")
object XSLT : XMLLanguage(INSTANCE, "XSLT", "application/xslt+xml") {
    // region Language

    const val NAMESPACE: String = "http://www.w3.org/1999/XSL/Transform"

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = "XSLT"

    override fun getAssociatedFileType(): LanguageFileType? = null

    init {
        putUserData(LanguageData.KEY, object : LanguageData {
            override val associations: List<FileNameMatcher> = listOf(
                ExtensionFileNameMatcher("xsl"),
                ExtensionFileNameMatcher("xslt")
            )

            override val mimeTypes: Array<String> = arrayOf("application/xslt+xml")
        })
    }

    // endregion
    // region Versions

    val VERSION_1_0: XpmLanguageVersion = XsltVersion("1.0", XsltSpec.REC_1_0_19991116)
    val VERSION_2_0: XpmLanguageVersion = XsltVersion("2.0", XsltSpec.REC_2_0_20070123)
    val VERSION_3_0: XpmLanguageVersion = XsltVersion("3.0", XsltSpec.REC_3_0_20170608)
    val VERSION_4_0: XpmLanguageVersion = XsltVersion("4.0", XsltSpec.ED_4_0_20210113)

    val versions: Map<String, XpmLanguageVersion> = listOf(
        VERSION_1_0,
        VERSION_2_0,
        VERSION_3_0,
        VERSION_4_0
    ).associateBy { it.version }

    // endregion
}
