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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResults
import uk.co.reecedunn.intellij.plugin.processor.run.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.run.StoppableQuery
import uk.co.reecedunn.intellij.plugin.processor.validate.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.SaxonErrorListener
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.SaxonQueryResultIterator
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.*
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.check
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_parse
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri

internal class SaxonXQueryRunner(
    val processor: Processor,
    private val query: VirtualFile
) : RunnableQuery, ValidatableQuery, StoppableQuery, SaxonRunner {
    // region XQuery Runner

    private val errorListener = SaxonErrorListener(query, processor.classLoader)

    private val compiler by lazy {
        if (traceListener == null) {
            traceListener = SaxonTraceListener()
        }
        processor.setTraceListener(traceListener)

        val ret = processor.newXQueryCompiler()
        ret.setErrorListener(errorListener)
        ret
    }

    private val executable by lazy { compiler.compile(query.decode()!!) }

    private val evaluator by lazy { executable.load() }

    // endregion
    // region Query

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean
        get() = compiler.updatingEnabled
        set(value) {
            compiler.updatingEnabled = value
        }

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private var context: XdmItem? = null

    override fun bindVariable(name: String, value: Any?, type: String?) = check(query, processor.classLoader) {
        val qname = qname_parse(name, SAXON_NAMESPACES).toQName(processor.classLoader)
        evaluator.setExternalVariable(qname, XdmValue.newInstance(value, type ?: "xs:string", processor))
    }

    @Suppress("DuplicatedCode")
    override fun bindContextItem(value: Any?, type: String?): Unit = check(query, processor.classLoader) {
        context = when (value) {
            is XpmModuleUri -> XdmItem.newInstance(value.path, type ?: "xs:string", processor)
            is VirtualFile -> XdmItem.newInstance(value.decode()!!, type ?: "xs:string", processor)
            else -> XdmItem.newInstance(value, type ?: "xs:string", processor)
        }
    }

    // endregion
    // region SaxonRunner

    override var traceListener: SaxonTraceListener? = null

    override fun asSequence(): Sequence<QueryResult> = check(query, processor.classLoader, errorListener) {
        context?.let { evaluator.setContextItem(it) }

        val destination = RawDestination(processor.classLoader)
        evaluator.setDestination(destination)

        evaluator.run()
        val result = destination.getXdmValue()

        SaxonQueryResultIterator(result.iterator(), processor).asSequence()
    }

    // endregion
    // region RunnableQuery

    override fun run(): QueryResults {
        val start = System.nanoTime()
        val results = asSequence().toList()
        val end = System.nanoTime()
        return QueryResults(QueryResults.OK, results, XsDuration.ns(end - start))
    }

    // endregion
    // region ValidatableQuery

    override fun validate(): QueryError? {
        return try {
            check(query, processor.classLoader) { executable } // Compile the query.
            null
        } catch (e: QueryError) {
            e
        }
    }

    // endregion
    // region StoppableQuery

    override fun stop() {
        traceListener?.stop()
    }

    // endregion
    // region Closeable

    override fun close() {
        processor.setTraceListener(null)
    }

    // endregion
}
