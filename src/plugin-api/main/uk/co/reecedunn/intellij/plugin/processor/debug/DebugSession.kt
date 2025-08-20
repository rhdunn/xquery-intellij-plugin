// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.debug

import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext

interface DebugSession {
    fun getBreakpointHandlers(language: Language, project: Project): Array<XBreakpointHandler<*>>

    var listener: DebugSessionListener?

    fun suspend()

    fun resume()

    fun step(action: StepAction)

    val stackFrames: List<XStackFrame>

    val suspendContext: XSuspendContext
}
