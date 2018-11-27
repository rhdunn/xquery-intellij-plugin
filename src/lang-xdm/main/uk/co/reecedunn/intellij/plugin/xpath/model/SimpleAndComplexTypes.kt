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
 * XPath 3.1 and XQuery 3.1 Type System Part 2: Simple and Complex Types
 */
package uk.co.reecedunn.intellij.plugin.xpath.model

// region XQuery IntelliJ Plugin (2.2.2) xs:anyType

interface XsAnyType

// endregion
// region XQuery IntelliJ Plugin (2.2.2) xdm:anyComplexType

interface XdmAnyComplexType : XsAnyType

// endregion
// region XQuery IntelliJ Plugin (2.2.2) xs:anySimpleType

interface XsAnySimpleType : XsAnyType

// endregion
// region XQuery IntelliJ Plugin (2.2.2) xs:anyAtomicType

interface XsAnyAtomicType : XsAnySimpleType,
    XdmItem

// endregion
// region XQuery IntelliJ Plugin (2.2.2) xdm:anyListType

interface XdmAnyListType : XsAnySimpleType

// endregion
// region XQuery IntelliJ Plugin (2.2.2) xdm:anyUnionType

interface XdmAnyUnionType : XsAnySimpleType

// endregion
