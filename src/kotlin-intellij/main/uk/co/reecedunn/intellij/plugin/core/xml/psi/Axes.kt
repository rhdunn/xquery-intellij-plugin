/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml.psi

import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree

// region XPath Selectors :: descendant

fun XmlTag.descendants(namespace: String, localName: String): Sequence<XmlTag> {
    return walkTree().filterIsInstance<XmlTag>().self(namespace, localName)
}

fun XmlTag.descendants(namespace: String, localName: Set<String>): Sequence<XmlTag> {
    return walkTree().filterIsInstance<XmlTag>().self(namespace, localName)
}

// endregion
// region XPath Selectors :: ancestor

fun XmlTag.ancestors(namespace: String, localName: String): Sequence<XmlTag> {
    return generateSequence(parentTag) { it.parentTag }.self(namespace, localName)
}

@Suppress("unused")
fun XmlTag.ancestors(namespace: String, localName: Set<String>): Sequence<XmlTag> {
    return generateSequence(parentTag) { it.parentTag }.self(namespace, localName)
}

// endregion
// region XPath Selectors :: self

private fun Sequence<XmlTag>.self(namespace: String, localName: String): Sequence<XmlTag> = filter {
    it.namespace == namespace && it.localName == localName
}

private fun Sequence<XmlTag>.self(namespace: String, localName: Set<String>): Sequence<XmlTag> = filter {
    it.namespace == namespace && localName.contains(it.localName)
}

// endregion
