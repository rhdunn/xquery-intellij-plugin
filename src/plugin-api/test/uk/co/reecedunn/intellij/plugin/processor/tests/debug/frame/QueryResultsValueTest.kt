/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.tests.debug.frame

import com.intellij.compat.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XFullValueEvaluator
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.QueryResultsValue
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import javax.swing.Icon

@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Debugger - Values")
class QueryResultsValueTest : XValueNode {
    // region XValueNode

    private var icon: Icon? = null
    private var presentation: XValuePresentation? = null
    private var hasChildren: Boolean = false

    private fun computePresentation(value: XValue, place: XValuePlace) {
        icon = null
        presentation = null
        value.computePresentation(this, place)
    }

    private fun renderValue(): String? = presentation?.let {
        it.renderValue(ValueTextRenderer)
        ValueTextRenderer.rendered
    }

    override fun setFullValueEvaluator(fullValueEvaluator: XFullValueEvaluator): Unit = TODO()

    override fun setPresentation(icon: Icon?, type: String?, value: String, hasChildren: Boolean) {
        setPresentation(icon, XRegularValuePresentation(value, type), hasChildren)
    }

    override fun setPresentation(icon: Icon?, presentation: XValuePresentation, hasChildren: Boolean) {
        this.icon = icon
        this.presentation = presentation
        this.hasChildren = hasChildren
    }

    // endregion

    @Test
    @DisplayName("empty sequence")
    fun emptySequence() {
        val v = QueryResultsValue(listOf())

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("empty-sequence()"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("()"))
        assertThat(hasChildren, `is`(false))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("empty-sequence()"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("()"))
        assertThat(hasChildren, `is`(false))
    }

    @Test
    @DisplayName("single item")
    fun singleItem() {
        val v = QueryResultsValue(listOf(QueryResult.fromItemType(0, "1234", "xs:integer")))

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XNumericValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:integer"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("1234"))
        assertThat(hasChildren, `is`(false))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XNumericValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:integer"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("1234"))
        assertThat(hasChildren, `is`(false))
    }

    @Test
    @DisplayName("multiple items of the same type")
    fun multipleItemsOfSameType() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "xs:integer"),
                QueryResult.fromItemType(1, "2", "xs:integer"),
                QueryResult.fromItemType(2, "3", "xs:integer")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:integer+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:integer+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple items of different item() types")
    fun multipleItemsOfDifferentItemTypes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "xs:integer"),
                QueryResult.fromItemType(1, "2", "text()"),
                QueryResult.fromItemType(2, "3", "xs:integer")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("item()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("item()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple items with a type subtype-itemType to the other types")
    fun multipleItemsSubtypeItemType() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "xs:integer"),
                QueryResult.fromItemType(1, "2.0", "xs:decimal"),
                QueryResult.fromItemType(2, "3", "xs:integer")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:decimal+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("xs:decimal+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple processing instructions with different NCNames")
    fun multipleProcessingInstructionsDifferentNCName() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "processing-instruction(a)"),
                QueryResult.fromItemType(1, "2", "processing-instruction(b)"),
                QueryResult.fromItemType(2, "3", "processing-instruction(c)")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("processing-instruction()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("processing-instruction()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("named processing instrucuions with other nodes")
    fun namedProcessingInstructionsWithOtherNodes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "processing-instruction(a)"),
                QueryResult.fromItemType(1, "2", "comment()"),
                QueryResult.fromItemType(2, "3", "text()")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple document nodes with different element types")
    fun multipleDocumentNodesDifferentTypes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "document-node(element(Q{}a))"),
                QueryResult.fromItemType(1, "2", "document-node(element(Q{}b))"),
                QueryResult.fromItemType(2, "3", "document-node(element(Q{}c))")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("document-node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("document-node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("typed document nodes with other nodes")
    fun typedDocumentNodesWithOtherNodes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "document-node(element(Q{}a))"),
                QueryResult.fromItemType(1, "2", "comment()"),
                QueryResult.fromItemType(2, "3", "text()")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple elements with different QNames")
    fun multipleElementsDifferentQName() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "element(Q{}a)"),
                QueryResult.fromItemType(1, "2", "element(Q{}b)"),
                QueryResult.fromItemType(2, "3", "element(Q{}c)")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("element()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("element()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("named element with other nodes")
    fun namedElementWithOtherNodes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "element(Q{}a)"),
                QueryResult.fromItemType(1, "2", "comment()"),
                QueryResult.fromItemType(2, "3", "text()")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("multiple attributes with different QNames")
    fun multipleAttributesDifferentQName() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "attribute(Q{}a)"),
                QueryResult.fromItemType(1, "2", "attribute(Q{}b)"),
                QueryResult.fromItemType(2, "3", "attribute(Q{}c)")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("attribute()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("attribute()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }

    @Test
    @DisplayName("named attribute with other nodes")
    fun namedAttributesWithOtherNodes() {
        val v = QueryResultsValue(
            listOf(
                QueryResult.fromItemType(0, "1", "attribute(Q{}a)"),
                QueryResult.fromItemType(1, "2", "comment()"),
                QueryResult.fromItemType(2, "3", "text()")
            )
        )

        computePresentation(v, XValuePlace.TREE)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))

        computePresentation(v, XValuePlace.TOOLTIP)
        assertThat(icon, `is`(nullValue()))
        assertThat(presentation, `is`(instanceOf(XRegularValuePresentation::class.java)))
        assertThat(presentation?.type, `is`("node()+"))
        assertThat(presentation?.separator, `is`(" := "))
        assertThat(renderValue(), `is`("size = 3"))
        assertThat(hasChildren, `is`(true))
    }
}
