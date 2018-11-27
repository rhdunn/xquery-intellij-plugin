/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType

/**
 * XML Path Language
 *
 * NOTE: The 'XPath' language ID is used by the IntelliJ plugin for the built-in
 * XPath 1.0 and 2.0 support. Using that causes those plugins to fail.
 */
object XPath : Language("XMLPath"), Versioned {
    val REC_1_0_19991116 = Specification(
        "1.0-19991116", 1.0, 19991116, "1.0", "https://www.w3.org/TR/1999/REC-xpath-19991116/", this
    )

    val REC_2_0_20070123 = Specification(
        "1.0-20070123", 2.0, 20070123, "2.0", "https://www.w3.org/TR/2007/REC-xpath20-20070123/", this
    )

    val REC_2_0_20101214 = Specification(
        "1.0-20101214", 2.0, 20101214, "2.0", "https://www.w3.org/TR/2010/REC-xpath20-20101214/", this
    )

    val REC_3_0_20140408 = Specification(
        "3.0-20140408", 3.0, 20140408, "3.0", "https://www.w3.org/TR/2014/REC-xpath-30-20140408/", this
    )

    val REC_3_1_20170321 = Specification(
        "3.1-20170321", 3.1, 20170321, "3.1", "https://www.w3.org/TR/2017/REC-xpath-31-20170321/", this
    )

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = "XPath"

    override fun getAssociatedFileType(): LanguageFileType? = null

    override val id: String = "xpath"

    override val name: String = displayName

    override val versions: List<Version> = listOf(
        REC_1_0_19991116,
        REC_2_0_20070123,
        REC_2_0_20101214,
        REC_3_0_20140408,
        REC_3_1_20170321
    )
}
