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
package uk.co.reecedunn.intellij.plugin.intellij.lang

enum class XPathSubset(val displayName: String) {
    // Reference: https://www.w3.org/TR/xpath-31/
    XPath("XPath"),
    // Reference: https://www.w3.org/TR/xslt20/#pattern-syntax
    // Reference: https://www.w3.org/TR/xslt-30/#pattern-syntax
    XsltPattern("XSLT Pattern"),
}
