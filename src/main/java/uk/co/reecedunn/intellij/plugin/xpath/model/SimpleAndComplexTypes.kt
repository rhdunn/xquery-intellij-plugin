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

// region XML Schema 1.1 Part 1 (2.2.1.1) xs:anyType

interface XsAnyType

// endregion
// region XML Schema 1.1 Part 2 (3.2.1) xs:anySimpleType

interface XsAnySimpleType : XsAnyType

// endregion
// region XML Schema 1.1 Part 2 (2.4.1.1) Atomic Datatypes ; (3.2.2) xs:anyAtomicType

interface XsAnyAtomicType : XsAnySimpleType, XdmItem

// endregion
// region XML Schema 1.1 Part 2 (2.4.1.2) List Datatypes

/*
 * XML Schema provides a definition of list types, but does not implement this
 * through an `xs:anyListType` type. XPath and XQuery Data Model only displays
 * this as "list types".
 */
interface XsAnyListType : XsAnySimpleType

// endregion
// region XML Schema 1.1 Part 2 (2.4.1.3) Union Datatypes

/*
 * XML Schema provides a definition of union types, but does not implement this
 * through an `xs:anyUnionType` type. XPath and XQuery Data Model only displays
 * this as "union types".
 */
interface XsAnyUnionType : XsAnySimpleType

// endregion
// region XML Schema 1.1 Part 1 (3.4) Complex Type Definitions

/*
 * XML Schema provides a definition of complex types, but does not implement
 * this through an `xs:anyComplexType` type. XPath and XQuery Data Model only
 * displays this as "complex types".
 */
interface XsAnyComplexType : XsAnyType

// endregion
