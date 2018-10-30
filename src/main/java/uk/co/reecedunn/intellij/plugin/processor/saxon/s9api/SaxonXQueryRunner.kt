/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

internal class SaxonXQueryRunner(val processor: Any, val query: String, val classes: SaxonClasses) : Query {
    private val compiler by lazy {
        classes.processorClass.getMethod("newXQueryCompiler").invoke(processor)
    }

    private val executable by lazy {
        classes.xqueryCompilerClass.getMethod("compile", String::class.java).invoke(compiler, query)
    }

    private val evaluator by lazy {
        classes.xqueryExecutableClass.getMethod("load").invoke(executable)
    }

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check {
        classes.xqueryEvaluatorClass
            .getMethod("setExternalVariable", classes.qnameClass, classes.xdmValueClass)
            .invoke(evaluator, classes.toQName(name), classes.toXdmValue(value, type))
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check {
        classes.xqueryEvaluatorClass
            .getMethod("setContextItem", classes.xdmItemClass)
            .invoke(evaluator, classes.toXdmValue(value, type))
    }

    override fun run(): Sequence<QueryResult> = classes.check {
        val iterator = classes.xqueryEvaluatorClass.getMethod("iterator").invoke(evaluator)
        SaxonQueryResultIterator(iterator, classes).asSequence()
    }

    override fun close() {
    }
}
