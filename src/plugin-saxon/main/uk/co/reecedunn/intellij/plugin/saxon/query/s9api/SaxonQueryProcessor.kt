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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQuery
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQueryProvider
import uk.co.reecedunn.intellij.plugin.saxon.resources.SaxonIcons
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.profiler.SaxonProfileTraceListener
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger.SaxonDebugTraceListener
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger.SaxonQueryDebugger
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.profiler.SaxonQueryProfiler
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonRunner
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonXPathRunner
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonXQueryRunner
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonXsltRunner
import java.lang.RuntimeException
import java.lang.reflect.InvocationTargetException
import javax.xml.transform.Source

internal class SaxonQueryProcessor(
    val classLoader: ClassLoader,
    private val source: Source?
) : ProfileableQueryProvider,
    RunnableQueryProvider,
    DebuggableQueryProvider,
    ValidatableQueryProvider {
    // region QueryProcessor

    private val processor by lazy {
        if (source == null)
            Processor(classLoader, true)
        else
            try {
                Processor(classLoader, source)
            } catch (e: InvocationTargetException) {
                when (((e.targetException as? RuntimeException)?.cause as? ClassNotFoundException)?.message) {
                    "com.saxonica.config.EnterpriseConfiguration" -> throw UnsupportedSaxonConfiguration("EE")
                    "com.saxonica.config.ProfessionalConfiguration" -> throw UnsupportedSaxonConfiguration("PE")
                    else -> throw e
                }
            }
    }

    override val presentation: ItemPresentation
        get() = check(null, classLoader) {
            ItemPresentationImpl(SaxonIcons.Product, "Saxon ${processor.version}")
        }

    override val servers: List<QueryServer> = listOf()

    // endregion
    // region ProfileableQueryProvider

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        val runner = createRunnableQuery(query, language)
        (runner as SaxonRunner).traceListener = SaxonProfileTraceListener(processor.version, query)
        return SaxonQueryProfiler(runner)
    }

    // endregion
    // region RunnableQueryProvider

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query)
            XQuery -> SaxonXQueryRunner(processor, queryText, query)
            XSLT -> SaxonXsltRunner(processor, queryText, query)
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region DebuggableQueryProvider

    override fun createDebuggableQuery(query: VirtualFile, language: Language): DebuggableQuery {
        val runner = createRunnableQuery(query, language)
        (runner as SaxonRunner).traceListener = SaxonDebugTraceListener(query, processor)
        return SaxonQueryDebugger(runner)
    }

    // endregion
    // region ValidatableQueryProvider

    override fun createValidatableQuery(query: VirtualFile, language: Language): ValidatableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query)
            XQuery -> SaxonXQueryRunner(processor, queryText, query)
            XSLT -> SaxonXsltRunner(processor, queryText, query)
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region Closeable

    override fun close() {
    }

    // endregion
}
