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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.AnAction
import uk.co.reecedunn.intellij.plugin.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.RunnableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.ProfileReportTableView
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.QueryConsoleView
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.results.QueryTextConsoleView
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQueryProvider

class QueryProcessorRunState(private val environment: ExecutionEnvironment) : RunProfileState {
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        val processHandler = startProcess()
        val console = createConsole(executor!!)
        console?.attachToProcess(processHandler)
        return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
    }

    private fun startProcess(): ProcessHandler {
        val configuration = environment.runProfile as QueryProcessorRunConfiguration
        val source = configuration.scriptFile
            ?: throw ExecutionException("Unsupported query file: " + (configuration.scriptFilePath ?: ""))

        val session = configuration.processor!!.session
        return when (environment.executor.id) {
            DefaultRunExecutor.EXECUTOR_ID -> {
                val query = (session as RunnableQueryProvider).createRunnableQuery(source, configuration.language)
                query.rdfOutputFormat = configuration.rdfOutputFormat
                query.updating = configuration.updating
                query.xpathSubset = configuration.xpathSubset
                query.database = configuration.database ?: ""
                query.server = configuration.server ?: ""
                query.modulePath = configuration.modulePath ?: ""
                configuration.contextItem?.let { query.bindContextItem(it, null) }
                RunnableQueryProcessHandler(query)
            }
            DefaultProfileExecutor.EXECUTOR_ID -> {
                val query = (session as ProfileableQueryProvider).createProfileableQuery(source, configuration.language)
                query.rdfOutputFormat = configuration.rdfOutputFormat
                query.updating = configuration.updating
                query.xpathSubset = configuration.xpathSubset
                query.database = configuration.database ?: ""
                query.server = configuration.server ?: ""
                query.modulePath = configuration.modulePath ?: ""
                configuration.contextItem?.let { query.bindContextItem(it, null) }
                ProfileableQueryProcessHandler(query)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    private fun createConsole(executor: Executor): ConsoleView? {
        val consoleView = QueryConsoleView(environment.project)
        when (executor.id) {
            DefaultRunExecutor.EXECUTOR_ID -> {
                consoleView.addContentProvider(QueryTextConsoleView(environment.project))
            }
            DefaultProfileExecutor.EXECUTOR_ID -> {
                consoleView.addContentProvider(QueryTextConsoleView(environment.project))
                consoleView.addContentProvider(ProfileReportTableView(environment.project))
            }
            else -> throw UnsupportedOperationException()
        }
        return consoleView
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createActions(
        console: ConsoleView?,
        processHandler: ProcessHandler,
        executor: Executor?
    ): Array<AnAction> {
        return AnAction.EMPTY_ARRAY
    }
}
