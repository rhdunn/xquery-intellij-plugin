// Copyright (C) 2018-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations

import com.intellij.execution.configurations.RunConfigurationOptions
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset

class QueryProcessorRunConfigurationData : RunConfigurationOptions() {
    var processorId: Int by property(-1)
    var rdfOutputFormat: String? by string()
    var updating: Boolean by property(false)
    var xpathSubset: XPathSubset by enum(XPathSubset.XPath)
    var database: String? by string()
    var server: String? by string()
    var modulePath: String? by string()
    var scriptFile: String? by string()
    var scriptSource: QueryProcessorDataSourceType by enum(QueryProcessorDataSourceType.LocalFile)
    var contextItem: String? by string()
    var contextItemSource: QueryProcessorDataSourceType? by enum<QueryProcessorDataSourceType>()
    var reformatResults: Boolean by property(false)
}
