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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.runner

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.ui.ConsoleView
import uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration.XRayTestConfiguration
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestService
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.QueryErrorStackTraceFilter
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations.RunProfileStateEx
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.ProfileableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.RunnableQueryProcessHandler
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQueryProvider
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery

class XRayTestRunState(private val environment: ExecutionEnvironment) : RunProfileStateEx {
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        val configuration = environment.runProfile as XRayTestConfiguration

        val processHandler = createProcessHandler(createQuery())
        val console = createConsole(executor!!)
        console.addMessageFilter(QueryErrorStackTraceFilter(environment.project))
        console.attachToProcess(processHandler)
        processHandler.addQueryResultListener(XRayTestProcessListener(processHandler, configuration.outputFormat))
        return DefaultExecutionResult(console, processHandler)
    }

    override fun createQuery(): Query {
        val configuration = environment.runProfile as XRayTestConfiguration

        val query = createQuery(environment.executor, configuration)
        query.updating = false
        query.database = configuration.database ?: ""
        query.server = configuration.server ?: ""
        query.modulePath = configuration.modulePath ?: ""

        configuration.testPath?.let {
            val modulePath = XpmProjectConfigurations.getInstance(environment.project).toModulePath(it)
            query.bindVariable("test-dir", modulePath, "xs:string")
        }
        configuration.modulePattern?.let { query.bindVariable("module-pattern", it, "xs:string") }
        configuration.testPattern?.let { query.bindVariable("test-pattern", it, "xs:string") }
        query.bindVariable("format", configuration.outputFormat.id, "xs:string")
        return query
    }

    private fun createQuery(executor: Executor, configuration: XRayTestConfiguration): Query {
        val source = XRayTestService.getInstance(environment.project).runTestsQuery!!
        val session = configuration.processor!!.session
        return when (executor.id) {
            DefaultRunExecutor.EXECUTOR_ID -> {
                (session as RunnableQueryProvider).createRunnableQuery(source, XQuery)
            }
            DefaultProfileExecutor.EXECUTOR_ID -> {
                (session as ProfileableQueryProvider).createProfileableQuery(source, XQuery)
            }
            DefaultDebugExecutor.EXECUTOR_ID -> {
                (session as DebuggableQueryProvider).createDebuggableQuery(source, XQuery)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun createProcessHandler(query: Query): QueryProcessHandlerBase {
        val configuration = environment.runProfile as XRayTestConfiguration
        return when (query) {
            is RunnableQuery -> RunnableQueryProcessHandler(query).reformat(configuration.reformatResults)
            is ProfileableQuery -> ProfileableQueryProcessHandler(query).reformat(configuration.reformatResults)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun createConsole(executor: Executor): ConsoleView {
        val configuration = environment.runProfile as XRayTestConfiguration
        val testConsoleProperties = configuration.createTestConsoleProperties(executor)
        return SMTestRunnerConnectionUtil.createConsole(testConsoleProperties)
    }
}
