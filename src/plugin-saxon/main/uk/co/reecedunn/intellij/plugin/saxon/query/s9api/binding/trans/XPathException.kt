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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.QName
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om.StructuredQName
import javax.xml.transform.SourceLocator
import javax.xml.transform.TransformerException

class XPathException(private val `object`: TransformerException?, private val `class`: Class<*>) :
    TransformerException(`object`?.message) {

    override fun getLocator(): SourceLocator? {
        return `object`?.locator
    }

    override fun getException(): Throwable? {
        return cause
    }

    override val cause: Throwable? = `object`?.cause

    fun getErrorCodeQName(): QName? {
        if (`class`.isInstance(`object`)) {
            val qname = `class`.getMethod("getErrorCodeQName").invoke(`object`)
            return StructuredQName(qname, `class`.classLoader.loadClass("net.sf.saxon.om.StructuredQName"))
        }
        return null
    }
}

fun Throwable.toXPathException(loader: ClassLoader): XPathException? {
    val xpathExceptionClass = loader.loadClass("net.sf.saxon.trans.XPathException")
    return if (this is TransformerException)
        XPathException(this, xpathExceptionClass)
    else if (cause is TransformerException)
        XPathException(cause as TransformerException, xpathExceptionClass)
    else
        null
}
