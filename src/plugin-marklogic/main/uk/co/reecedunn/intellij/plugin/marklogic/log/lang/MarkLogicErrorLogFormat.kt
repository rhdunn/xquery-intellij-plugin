// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.log.lang

class MarkLogicErrorLogFormat internal constructor(
    val haveServer: Boolean
) {
    companion object {
        val MARKLOGIC_8 = MarkLogicErrorLogFormat(haveServer = true)
        val MARKLOGIC_9 = MarkLogicErrorLogFormat(haveServer = false)
    }
}
