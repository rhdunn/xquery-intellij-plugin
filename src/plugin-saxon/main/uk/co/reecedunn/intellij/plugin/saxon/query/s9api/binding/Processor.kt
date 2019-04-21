/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.TraceListener
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.proxy
import javax.xml.transform.Source

class Processor {
    private val `object`: Any
    private val `class`: Class<*>

    constructor(classLoader: ClassLoader, licensedEdition: Boolean) {
        `class` = classLoader.loadClass("net.sf.saxon.s9api.Processor")
        `object` = `class`.getConstructor(Boolean::class.java).newInstance(licensedEdition)

        // Using the Saxon EE optimizer can generate a NoClassDefFoundError
        // resolving com/saxonica/ee/bytecode/GeneratedCode. This appears to
        // be due to the way the Saxon processor modifies the Java class loader.
        setConfigurationProperty("OPTIMIZATION_LEVEL", "0")
    }

    constructor(classLoader: ClassLoader, configuration: Source) {
        `class` = classLoader.loadClass("net.sf.saxon.s9api.Processor")
        `object` = `class`.getConstructor(Source::class.java).newInstance(configuration)

        // Using the Saxon EE optimizer can generate a NoClassDefFoundError
        // resolving com/saxonica/ee/bytecode/GeneratedCode. This appears to
        // be due to the way the Saxon processor modifies the Java class loader.
        setConfigurationProperty("OPTIMIZATION_LEVEL", "0")
    }

    val classLoader: ClassLoader get() = `class`.classLoader

    val saxonEdition: String? get() = `class`.getMethodOrNull("getSaxonEdition")?.invoke(`object`) as? String

    val saxonProductVersion: String get() = `class`.getMethod("getSaxonProductVersion").invoke(`object`) as String

    val version: String get() = saxonEdition?.let { "$it $saxonProductVersion" } ?: saxonProductVersion

    fun setTraceListener(listener: TraceListener) {
        val configurationClass = `class`.classLoader.loadClass("net.sf.saxon.Configuration")
        val listenerClass = `class`.classLoader.loadClass("net.sf.saxon.lib.TraceListener")
        val listener2Class = `class`.classLoader.loadClassOrNull("net.sf.saxon.lib.TraceListener2")
        val proxy = listener2Class?.let { listener.proxy(listenerClass, it) } ?: listener.proxy(listenerClass)

        // Call getDefaultStaticQueryContext to ensure the TraceCodeInjector is set,
        // so the TraceListener events get called the first time the query is run.
        val configuration = `class`.getMethod("getUnderlyingConfiguration").invoke(`object`)
        configurationClass.getMethod("getDefaultStaticQueryContext").invoke(configuration)
        configurationClass.getMethod("setTraceListener", listenerClass).invoke(configuration, proxy)
    }

    fun setConfigurationProperty(name: String, value: Any) {
        val configurationClass = `class`.classLoader.loadClass("net.sf.saxon.Configuration")
        val configuration = `class`.getMethod("getUnderlyingConfiguration").invoke(`object`)

        val featureClass = `class`.classLoader.loadClassOrNull("net.sf.saxon.lib.Feature")
        if (featureClass == null) { // Saxon <= 9.7
            val featureKeysClass = `class`.classLoader.loadClass("net.sf.saxon.lib.FeatureKeys")
            configurationClass.getMethod("setConfigurationProperty", String::class.java, Any::class.java)
                .invoke(configuration, featureKeysClass.getField(name).get(null), value)
        } else { // Saxon >= 9.8
            configurationClass.getMethod("setConfigurationProperty", featureClass, Any::class.java)
                .invoke(configuration, featureClass.getField(name).get(null), value)
        }
    }

    fun newXPathCompiler(): XPathCompiler {
        val compiler = `class`.getMethod("newXPathCompiler").invoke(`object`)
        return XPathCompiler(compiler, `class`.classLoader.loadClass("net.sf.saxon.s9api.XPathCompiler"))
    }

    fun newXQueryCompiler(): XQueryCompiler {
        val compiler = `class`.getMethod("newXQueryCompiler").invoke(`object`)
        return XQueryCompiler(compiler, `class`.classLoader.loadClass("net.sf.saxon.s9api.XQueryCompiler"))
    }

    fun newXsltCompiler(): XsltCompiler {
        val compiler = `class`.getMethod("newXsltCompiler").invoke(`object`)
        return XsltCompiler(compiler, `class`.classLoader.loadClass("net.sf.saxon.s9api.XsltCompiler"))
    }
}
