/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleRunnerLayoutUiBuilder
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.RunnableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.QueryConsoleView
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.results.QueryTextConsoleView
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.ui.FlatProfileTableView
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQueryProvider

class QueryProcessorRunState(private val environment: ExecutionEnvironment) : RunProfileStateEx {
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = createProcessHandler(createQuery())
        val console = createConsole(executor!!)
        console.attachToProcess(processHandler)
        return DefaultExecutionResult(console, processHandler)
    }

    override fun createQuery(): Query {
        val configuration = environment.runProfile as QueryProcessorRunConfiguration

        val query = createQuery(environment.executor, configuration)
        query.rdfOutputFormat = configuration.rdfOutputFormat
        query.updating = configuration.updating
        query.xpathSubset = configuration.xpathSubset
        query.database = configuration.database ?: ""
        query.server = configuration.server ?: ""
        query.modulePath = configuration.modulePath ?: ""
        configuration.contextItem?.let { query.bindContextItem(it, null) }
        return query
    }

    private fun createQuery(executor: Executor, configuration: QueryProcessorRunConfiguration): Query {
        val source = configuration.scriptFile
            ?: throw ExecutionException("Unsupported query file: " + (configuration.scriptFilePath ?: ""))
        val session = configuration.processor!!.session
        return when (executor.id) {
            DefaultRunExecutor.EXECUTOR_ID -> {
                (session as RunnableQueryProvider).createRunnableQuery(source, configuration.language)
            }
            DefaultDebugExecutor.EXECUTOR_ID -> {
                (session as DebuggableQueryProvider).createDebuggableQuery(source, configuration.language)
            }
            DefaultProfileExecutor.EXECUTOR_ID -> {
                (session as ProfileableQueryProvider).createProfileableQuery(source, configuration.language)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun createProcessHandler(query: Query): QueryProcessHandlerBase {
        val configuration = environment.runProfile as QueryProcessorRunConfiguration
        return when (query) {
            is RunnableQuery -> RunnableQueryProcessHandler(query).reformat(configuration.reformatResults)
            is ProfileableQuery -> ProfileableQueryProcessHandler(query).reformat(configuration.reformatResults)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun createConsole(executor: Executor): ConsoleView = when (executor.id) {
        DefaultRunExecutor.EXECUTOR_ID, DefaultDebugExecutor.EXECUTOR_ID -> {
            QueryConsoleView(environment.project, QueryTextConsoleView(environment.project))
        }
        DefaultProfileExecutor.EXECUTOR_ID -> {
            val console = QueryConsoleView(environment.project, QueryTextConsoleView(environment.project))
            ConsoleRunnerLayoutUiBuilder(console)
                .contentProvider(FlatProfileTableView(environment.project), active = true)
                .consoleView()
        }
        else -> throw UnsupportedOperationException()
    }
}
