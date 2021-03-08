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
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.execution.configurations.type

import com.intellij.execution.configurations.ConfigurationFactory
import uk.co.reecedunn.intellij.plugin.core.execution.configurations.ConfigurationTypeEx
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations.QueryProcessorConfigurationFactory
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.SPARQLQuery
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.SPARQLUpdate
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import javax.swing.Icon

class SPARQLConfigurationType : ConfigurationTypeEx {
    override fun getIcon(): Icon = MarkLogicIcons.SPARQL.RunConfiguration

    override fun getConfigurationTypeDescription(): String = displayName

    override fun getId(): String = "XIJPSPARQLProcessorConfiguration"

    override fun getDisplayName(): String = "SPARQL"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(QueryProcessorConfigurationFactory(this, SPARQLQuery, SPARQLUpdate))
    }

    override val factoryId: String = "SPARQL"
}
