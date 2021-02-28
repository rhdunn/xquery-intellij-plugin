/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.completion.lookup

import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import uk.co.reecedunn.intellij.plugin.core.editor.completeString

data class XPathInsertText(
    val beforeCaret: String,
    val hint: String?,
    val afterCaret: String?,
    val postHint: String? = null
) {
    companion object {
        val AXIS_MARKER: XPathInsertText = XPathInsertText("::", null, null)
        val QNAME_PREFIX: XPathInsertText = XPathInsertText(":", null, null)

        val EMPTY_PARAMS: XPathInsertText = XPathInsertText("()", null, null)
        val PARAMS: XPathInsertText = XPathInsertText("(", null, ")")

        // SequenceType, ItemType, and KindTest parameters
        val PARAMS_FIELD_DECLS: XPathInsertText = XPathInsertText("(", "field-declaration, field-declaration...", ")")
        val PARAMS_KEY_VALUE_TYPE: XPathInsertText = XPathInsertText("(", "key-type, value-type", ")")
        val PARAMS_KEYNAME: XPathInsertText = XPathInsertText("(", "key-name", ")")
        val PARAMS_NAME: XPathInsertText = XPathInsertText("(", "name", ")")
        val PARAMS_NAME_AND_TYPE: XPathInsertText = XPathInsertText("(", "name, type", ")")
        val PARAMS_NAME_STRING: XPathInsertText = XPathInsertText("(\"", "name", "\")")
        val PARAMS_ROOT_ELEMENT: XPathInsertText = XPathInsertText("(", "root-element", ")")
        val PARAMS_SCHEMA_CONTEXT: XPathInsertText = XPathInsertText("(", "schema-context-or-name?, type?", ")")
        val PARAMS_TYPE: XPathInsertText = XPathInsertText("(", "type", ")")
        val PARAMS_TYPES: XPathInsertText = XPathInsertText("(", "type, type...", ")")
        val PARAMS_VALUES: XPathInsertText = XPathInsertText("(", "\"value\", \"value\"...", ")")
        val PARAMS_WILDCARD: XPathInsertText = XPathInsertText("(*)", null, null)
        val PARAMS_WILDCARD_AND_TYPE: XPathInsertText = XPathInsertText("(*, ", "type", ")")
        val TYPED_FUNCTION: XPathInsertText = XPathInsertText("(", "sequence-types", ")", " as sequence-type")
    }

    val tailText: String = listOf(beforeCaret, hint ?: "", afterCaret ?: "", postHint ?: "").joinToString("")

    fun completeText(document: Document, offset: Int) {
        document.completeString(offset, beforeCaret)
        if (afterCaret != null) {
            document.completeString(offset + beforeCaret.length, afterCaret)
        }
    }

    fun moveCaret(caretModel: CaretModel) {
        caretModel.moveToOffset(caretModel.offset + beforeCaret.length)
    }
}
