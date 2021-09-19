/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.xml

import com.intellij.psi.PsiElement

interface XmlAccessors {
    fun attributes(node: Any): Sequence<Any>

    fun attributeValueNode(node: Any): PsiElement?

    fun children(node: Any): Sequence<Any>

    fun nodeKind(node: Any): NodeKind?

    fun namespaceUri(node: Any): String?

    fun localName(node: Any): String?

    fun parent(node: Any): Any?

    fun stringValue(node: Any): String?
}
