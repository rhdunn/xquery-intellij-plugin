/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
 * XPath 3.1 and XQuery 3.1 Type System Part 1: Items
 */
package uk.co.reecedunn.intellij.plugin.xdm.types

// region XQuery and XPath 3.1 Data Model (2.7.4) : node()

interface XdmNode : XdmItem

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : attribute()

interface XdmAttribute : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : document-node()

interface XdmDocument : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : element()

interface XdmElement : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : comment()

interface XdmComment : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : namespace-node()

interface XdmNamespace : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : processing-instruction()

interface XdmProcessingInstruction : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : text()

interface XdmText : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : array-node() [MarkLogic]

interface XdmArrayNode : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : boolean-node() [MarkLogic]

interface XdmBooleanNode : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : null-node() [MarkLogic]

interface XdmNullNode : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : number-node() [MarkLogic]

interface XdmNumberNode : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : object-node() [MarkLogic]

interface XdmObjectNode : XdmNode

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : attribute-decl() [MarkLogic]

interface XdmAttributeDecl : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : binary() [MarkLogic]

interface XdmBinary : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : complex-type() [MarkLogic]

interface XdmComplexType : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : element-decl() [MarkLogic]

interface XdmElementDecl : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : model-group() [MarkLogic]

interface XdmModelGroup : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-component() [MarkLogic]

interface XdmSchemaComponent : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-facet() [MarkLogic]

interface XdmSchemaFacet : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-particle() [MarkLogic]

interface XdmSchemaParticle : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-root() [MarkLogic]

interface XdmSchemaRoot : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-type() [MarkLogic]

interface XdmSchemaType : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : schema-wildcard() [MarkLogic]

interface XdmSchemaWildcard : XdmItem

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : simple-type() [MarkLogic]

interface XdmSimpleType : XdmItem

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : function(*)

interface XdmFunction : XdmItem

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : map(*)

interface XdmMap : XdmFunction

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : array(*)

interface XdmArray : XdmFunction

// endregion
// region XQuery IntelliJ Plugin Data Model (2.1) : annotation(*)

interface XdmAnnotation : XdmItem {
    val name: XsQNameValue?

    val values: Sequence<XsAnyAtomicType>
}

// endregion
