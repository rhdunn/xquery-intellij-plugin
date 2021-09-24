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
/**
 * XPath 3.1 and XQuery 3.1 Type System Part 1: Items
 */
package uk.co.reecedunn.intellij.plugin.xdm.types

import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsNCName

// region XQuery and XPath 3.1 Data Model (2.7.4) : node()

interface XdmNode : XdmItem

object XdmNodeItem : XdmItemType {
    override val typeName: String = "node()"
    override val itemType: XdmItemType get() = this
    override val lowerBound: Int = 1
    override val upperBound: Int = 1
    override val typeClass: Class<*> = XdmNode::class.java
}

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : attribute()

interface XdmAttributeNode : XdmNode {
    val nodeName: XsQNameValue?

    val parentNode: XdmNode?

    val stringValue: String?

    val typedValue: XsAnyAtomicType?
}

object XdmAttributeItem : XdmItemType {
    override val typeName: String = "attribute()"
    override val itemType: XdmItemType get() = this
    override val lowerBound: Int = 1
    override val upperBound: Int = 1
    override val typeClass: Class<*> = XdmAttributeNode::class.java
}

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : document-node()

interface XdmDocumentNode : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : element()

interface XdmElementNode : XdmNode {
    val attributes: Sequence<XdmAttributeNode>

    val nodeName: XsQNameValue?

    val parentNode: XdmNode?

    val namespaceAttributes: Sequence<XdmNamespaceNode>
}

object XdmElementItem : XdmItemType {
    override val typeName: String = "element()"
    override val itemType: XdmItemType get() = this
    override val lowerBound: Int = 1
    override val upperBound: Int = 1
    override val typeClass: Class<*> = XdmElementNode::class.java
}

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : comment()

interface XdmCommentNode : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : namespace-node()

interface XdmNamespaceNode : XdmNode {
    companion object {
        val EMPTY_PREFIX: XsNCNameValue = XsNCName("")
    }

    val namespacePrefix: XsNCNameValue?

    val namespaceUri: XsAnyUriValue?

    val parentNode: XdmNode?
}

object XdmNamespaceItem : XdmItemType {
    override val typeName: String = "namespace-node()"
    override val itemType: XdmItemType get() = this
    override val lowerBound: Int = 1
    override val upperBound: Int = 1
    override val typeClass: Class<*> = XdmElementNode::class.java
}

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : processing-instruction()

interface XdmProcessingInstructionNode : XdmNode

// endregion
// region XQuery and XPath 3.1 Data Model (2.7.4) : text()

interface XdmTextNode : XdmNode

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
