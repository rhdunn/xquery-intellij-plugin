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
package uk.co.reecedunn.intellij.plugin.core.execution.testframework

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputType
import com.intellij.execution.testframework.sm.ServiceMessageBuilder
import com.intellij.openapi.util.Key
import java.lang.ref.WeakReference

open class TestProcessHandlerEvents private constructor(private val processHandler: WeakReference<ProcessHandler>) {
    constructor(processHandler: ProcessHandler) : this(WeakReference(processHandler))

    fun notifyTextAvailable(text: String, outputType: Key<*>) {
        processHandler.get()?.notifyTextAvailable(text, outputType)
    }

    private fun notifyServiceMessage(builder: ServiceMessageBuilder) {
        notifyTextAvailable("$builder\n", ProcessOutputType.STDOUT)
    }

    fun notifyTestsStarted() {
        notifyServiceMessage(ServiceMessageBuilder("enteredTheMatrix"))
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestSuiteStarted
     */
    fun notifyTestSuiteStarted(name: String) {
        notifyServiceMessage(ServiceMessageBuilder.testSuiteStarted(name))
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestSuiteFinished
     */
    fun notifyTestSuiteFinished(name: String) {
        notifyServiceMessage(ServiceMessageBuilder.testSuiteFinished(name))
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestStarted
     */
    fun notifyTestStarted(name: String, locationHint: String? = null, captureStdOutput: Boolean = false) {
        val builder = ServiceMessageBuilder.testStarted(name)
        locationHint?.let { builder.addAttribute("locationHint", it) }
        if (captureStdOutput) builder.addAttribute("captureStandardOutput", "true")
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestFinished
     */
    fun notifyTestFinished(name: String, duration: Int? = null) {
        val builder = ServiceMessageBuilder.testFinished(name)
        duration?.let { builder.addAttribute("duration", it.toString()) }
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestStdOut
     */
    @Suppress("unused")
    fun notifyTestStdOut(name: String, out: String) {
        val builder = ServiceMessageBuilder.testStdOut(name)
        builder.addAttribute("out", out)
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestStdErr
     */
    @Suppress("unused")
    fun notifyTestStdErr(name: String, out: String) {
        val builder = ServiceMessageBuilder.testStdErr(name)
        builder.addAttribute("out", out)
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestFailed
     */
    fun notifyTestFailed(name: String, message: String, expected: String? = null, actual: String? = null) {
        val builder = ServiceMessageBuilder.testFailed(name)
        builder.addAttribute("message", message)
        if (expected != null && actual != null) {
            builder.addAttribute("expected", expected)
            builder.addAttribute("actual", actual)
            builder.addAttribute("type", "comparisonFailure")
        }
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestFailed
     */
    fun notifyTestError(name: String, message: String, details: String? = null) {
        val builder = ServiceMessageBuilder.testFailed(name)
        builder.addAttribute("message", message)
        details?.let { builder.addAttribute("details", it) }
        builder.addAttribute("error", "true")
        notifyServiceMessage(builder)
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestFailed
     */
    fun notifyTestError(name: String, exception: Throwable) {
        notifyTestError(name, message = "", details = exception.stackTraceToString())
    }

    /**
     * @see jetbrains.buildServer.messages.serviceMessages.TestIgnored
     */
    fun notifyTestIgnored(name: String, message: String? = null) {
        val builder = ServiceMessageBuilder.testIgnored(name)
        message?.let { builder.addAttribute("message", it) }
        notifyServiceMessage(builder)
    }
}
