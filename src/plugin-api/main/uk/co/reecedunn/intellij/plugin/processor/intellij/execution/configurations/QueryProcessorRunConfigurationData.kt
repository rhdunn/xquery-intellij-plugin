/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations

import com.intellij.execution.configurations.RunConfigurationOptions
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset

data class QueryProcessorRunConfigurationData(
    var processorId: Int? = null,
    var rdfOutputFormat: String? = null,
    var updating: Boolean = false,
    var xpathSubset: XPathSubset = XPathSubset.XPath,
    var database: String? = null,
    var server: String? = null,
    var modulePath: String? = null,
    var scriptFile: String? = null,
    var scriptSource: QueryProcessorDataSourceType = QueryProcessorDataSourceType.LocalFile,
    var contextItem: String? = null,
    var contextItemSource: QueryProcessorDataSourceType? = null,
    var reformatResults: Boolean = false
) : RunConfigurationOptions()
