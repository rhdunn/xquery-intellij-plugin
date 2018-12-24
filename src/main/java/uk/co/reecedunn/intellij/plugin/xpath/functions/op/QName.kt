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
package uk.co.reecedunn.intellij.plugin.xpath.functions.op

import uk.co.reecedunn.intellij.plugin.xpath.model.*

// region XPath and XQuery Functions and Operators 3.1 (10.2.1) op:QName-equal

@Suppress("FunctionName")
fun op_qname_equal(arg1: XsQNameValue, arg2: XsQNameValue): Boolean {
    if (arg1.localName?.data?.equals(arg2.localName?.data) == true) {
        return if (arg1.isLexicalQName && arg2.isLexicalQName) {
            (arg1.prefix == null && arg2.prefix == null) || arg1.prefix?.data?.equals(arg2.prefix?.data) == true
        } else {
            (arg1.namespace == null && arg2.namespace == null) || arg1.namespace?.data?.equals(arg2.namespace?.data) == true
        }
    }
    return false
}

// endregion
