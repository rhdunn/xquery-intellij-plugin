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

/**
 * XQuery Formal Semantics
 */
object FormalSemantics : Versioned {
    val REC_1_0_20070123 = Specification(
        "1.0-20070123", 1.0, 20070123, "1.0", "https://www.w3.org/TR/2007/REC-xquery-semantics-20070123/", this
    )

    val REC_1_0_20101214 = Specification(
        "1.0-20101214", 1.0, 20101214, "1.0", "http://www.w3.org/TR/2010/REC-xquery-semantics-20101214/", this
    )

    override val id get(): String = "xquery-semantics"

    override val name get(): String = "XQuery Formal Semantics"

    override val versions
        get(): List<Version> = listOf(
            REC_1_0_20070123,
            REC_1_0_20101214
        )

    override fun supportsDialect(dialect: Versioned): Boolean = dialect === this
}
