/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger

import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.debug.DebugSession
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResults
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.run.StoppableQuery
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonRunner

internal class SaxonQueryDebugger(val runner: RunnableQuery) : DebuggableQuery, StoppableQuery {
    // region Query

    override var rdfOutputFormat: Language?
        get() = runner.rdfOutputFormat
        set(value) {
            runner.rdfOutputFormat = value
        }

    override var updating: Boolean
        get() = runner.updating
        set(value) {
            runner.updating = value
        }

    override var xpathSubset: XPathSubset
        get() = runner.xpathSubset
        set(value) {
            runner.xpathSubset = value
        }

    override var server: String
        get() = runner.server
        set(value) {
            runner.server = value
        }

    override var database: String
        get() = runner.database
        set(value) {
            runner.database = value
        }

    override var modulePath: String
        get() = runner.modulePath
        set(value) {
            runner.modulePath = value
        }

    override fun bindVariable(name: String, value: Any?, type: String?) {
        runner.bindVariable(name, value, type)
    }

    override fun bindContextItem(value: Any?, type: String?) {
        runner.bindContextItem(value, type)
    }

    // endregion
    // region RunnableQuery

    override fun run(): QueryResults = runner.run()

    // endregion
    // region StoppableQuery

    override fun stop() {
        (runner as SaxonRunner).traceListener?.stop()
    }

    // endregion
    // region DebuggableQuery

    override val session: DebugSession
        get() = (runner as SaxonRunner).traceListener as DebugSession

    // endregion
    // region Closeable

    override fun close() {
        runner.close()
    }

    // endregion
}
