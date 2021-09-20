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
package uk.co.reecedunn.intellij.plugin.marklogic.configuration.roxy.indices

import uk.co.reecedunn.intellij.plugin.marklogic.configuration.indices.MarkLogicAttributeIndex
import uk.co.reecedunn.intellij.plugin.marklogic.configuration.roxy.RoxyDatabaseConfiguration
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.childStringValue

class RoxyAttributeRangeIndex(private val index: Any, private val accessors: XmlAccessors) :
    MarkLogicAttributeIndex {
    companion object {
        private const val SCALAR_TYPE = "scalar-type"
        private const val PARENT_NAMESPACE_URI = "parent-namespace-uri"
        private const val PARENT_LOCAL_NAME = "parent-localname"
        private const val NAMESPACE_URI = "namespace-uri"
        private const val LOCAL_NAME = "localname"
    }

    override val schemaType: String by lazy {
        accessors.childStringValue(index, RoxyDatabaseConfiguration.NAMESPACE, SCALAR_TYPE) ?: ""
    }

    override val elementNamespaceUri: String by lazy {
        accessors.childStringValue(index, RoxyDatabaseConfiguration.NAMESPACE, PARENT_NAMESPACE_URI) ?: ""
    }

    override val elementLocalName: String by lazy {
        accessors.childStringValue(index, RoxyDatabaseConfiguration.NAMESPACE, PARENT_LOCAL_NAME) ?: ""
    }

    override val namespaceUri: String by lazy {
        accessors.childStringValue(index, RoxyDatabaseConfiguration.NAMESPACE, NAMESPACE_URI) ?: ""
    }

    override val localName: String by lazy {
        accessors.childStringValue(index, RoxyDatabaseConfiguration.NAMESPACE, LOCAL_NAME) ?: ""
    }
}
