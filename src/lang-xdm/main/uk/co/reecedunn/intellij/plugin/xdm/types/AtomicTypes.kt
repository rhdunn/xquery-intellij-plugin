/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
/**
 * XPath 3.1 and XQuery 3.1 Type System Part 3: Atomic Types
 *
 * Immediate descendants of XsAnyAtomicType are the primitive types defined in
 * XPath and XQuery Functions and Operators 3.1 (19.1). This is:
 *   1.  The 19 primitive types from XML Schema 1.1 (3.3);
 *   2.  xs:untypedAtomic;
 *   3.  xs:integer;
 *   4.  xs:yearMonthDuration;
 *   5.  xs:dayTimeDuration.
 *
 * This is for two reasons. The first reason is that it makes it easier to
 * identify primitive types (they directly implement XsAnyAtomicType). The
 * second reason is that each primitive type has a different representation
 * for its data.
 */
package uk.co.reecedunn.intellij.plugin.xdm.types

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.text.Units
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import java.math.BigDecimal
import java.math.BigInteger
import java.text.NumberFormat

// region XQuery and XPath Data Model 3.1 (2.7.2) xs:untypedAtomic

interface XsUntypedAtomicValue : XsAnyAtomicType {
    val data: String
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.1) xs:string

interface XsStringValue : XsAnyAtomicType {
    val data: String
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.3) xs:decimal

interface XsDecimalValue : XsAnyAtomicType {
    val data: BigDecimal
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.5) xs:double

interface XsDoubleValue : XsAnyAtomicType {
    val data: Double
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.6) xs:duration

interface XsDurationValue : XsAnyAtomicType, Comparable<XsDurationValue> {
    val months: XsIntegerValue
    val seconds: XsDecimalValue
}

fun XsDurationValue.toSeconds(precision: NumberFormat = Units.Precision.milli): String {
    return "${precision.format(seconds.data)} s"
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.17) xs:anyURI

interface XsAnyUriValue : XsAnyAtomicType {
    val data: String

    val context: XdmUriContext

    val moduleTypes: Array<XdmModuleType>
}

interface XsAnyUriPsiElement : PsiElement, XsAnyUriValue

// endregion
// region XML Schema 1.1 Part 2 (3.3.18) xs:QName

interface XsQNameValue : XsAnyAtomicType {
    val namespace: XsAnyUriValue?
    val prefix: XsNCNameValue?
    val localName: XsNCNameValue?
    val isLexicalQName: Boolean
}

fun XsQNameValue.isPrefixOrNCName(element: PsiElement): Boolean {
    return isLexicalQName && (prefix == null || prefix?.element === element)
}

fun XsQNameValue.isLocalNameOrNCName(element: PsiElement): Boolean {
    return (isLexicalQName && prefix == null) || localName?.element === element
}

// endregion
// region XML Schema 1.1 Part 2 (3.4.1) xs:normalizedString

interface XsNormalizedStringValue : XsStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.2) xs:token

interface XsTokenValue : XsNormalizedStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.6) xs:Name

interface XsNameValue : XsTokenValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.7) xs:NCName

interface XsNCNameValue : XsNameValue

val XsNCNameValue.element: PsiElement?
    get() = (this as XdmItem).element

// endregion
// region XML Schema 1.1 Part 2 (3.4.8) xs:ID

interface XsIDValue : XsNCNameValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.13) xs:integer

interface XsIntegerValue : XsAnyAtomicType {
    val data: BigInteger
}

fun XsIntegerValue.toInt(): Int = data.toInt()

// endregion
// region XQuery IntelliJ Plugin (2.2.3) xdm:wildcard

interface XdmWildcardValue : XsNCNameValue

// endregion
