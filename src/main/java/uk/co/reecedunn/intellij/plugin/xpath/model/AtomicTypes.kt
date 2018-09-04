/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import java.math.BigDecimal
import java.math.BigInteger

// region XML Schema 1.1 Part 2 (3.3.3) xs:decimal

interface XsDecimalValue : XsAnyAtomicType {
    val data: BigDecimal
}

// endregion
// region XML Schema 1.1 Part 2 (3.4.13) xs:integer

interface XsIntegerValue : XsAnyAtomicType {
    val data: BigInteger
}

fun XsIntegerValue.toInt(): Int = data.toInt()

// endregion
