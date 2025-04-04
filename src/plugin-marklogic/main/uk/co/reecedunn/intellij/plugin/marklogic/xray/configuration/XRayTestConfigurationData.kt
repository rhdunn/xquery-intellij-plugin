// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration

import com.intellij.execution.configurations.RunConfigurationOptions
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlFormat

class XRayTestConfigurationData : RunConfigurationOptions() {
    var processorId: Int by property(-1)
    var database: String? by string()
    var server: String? by string()
    var modulePath: String? by string()
    var testPath: String? by string()
    var modulePattern: String? by string()
    var testPattern: String? by string()
    var outputFormat: String? by string(XRayXmlFormat.id)
    var reformatResults: Boolean by property(false)
}
