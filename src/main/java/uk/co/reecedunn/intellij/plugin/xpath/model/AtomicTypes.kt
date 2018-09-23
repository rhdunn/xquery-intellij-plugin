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
/**
 * XPath 3.1 and XQuery 3.1 Type System Part 3: Atomic Types
 *
 * Immediate descendants of XsAnyAtomicType are the primitive types defined in
 * XPath and XQuery Functions and Operators 3.1 (19.1). This is:
 *   1.  The 19 primitive types from XML Schema 1.1 (3.3);
 *   2.  xs:untypedAtomic;
 *   3.  xs:integer;
 *   4.  xs:yearMonthDuration;
 *   5.  xs:dayTimeDuration.
 *
 * This is for two reasons. The first reason is that it makes it easier to
 * identify primitive types (they directly implement XsAnyAtomicType). The
 * second reason is that each primitive type has a different representation
 * for its data.
 */
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.LightVirtualFileBase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.BigInteger

// region XML Schema 1.1 Part 2 (3.3.1) xs:string

interface XsStringValue : XsAnyAtomicType {
    val data: String
}

data class XsString(
    override val data: String,
    private val reference: WeakReference<PsiElement>?
) : XsStringValue {
    constructor(data: String, element: PsiElement?) : this(data, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.3) xs:decimal

interface XsDecimalValue : XsAnyAtomicType {
    val data: BigDecimal
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.5) xs:double

interface XsDoubleValue : XsAnyAtomicType {
    val data: Double
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.17) xs:anyURI

interface XsAnyUriValue : XsAnyAtomicType {
    val data: String
}

data class XsAnyUri(
    override val data: String,
    private val reference: WeakReference<PsiElement>?
) : XsAnyUriValue {
    constructor(data: String, element: PsiElement?) : this(data, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

private fun resolveFileByPath(parent: VirtualFile?, project: Project, path: String): PsiFile? {
    if (parent == null) {
        return null
    }

    val file = parent.findFileByRelativePath(path)
    if (file != null) {
        return file.toPsiFile(project)
    }

    return resolveFileByPath(parent.parent, project, path)
}

@Suppress("UNCHECKED_CAST")
fun <T : PsiFile> XsAnyUriValue.resolveUri(httpOnly: Boolean = false): T? {
    val path = data
    return when {
        path.isEmpty() -> null
        path.startsWith("res://") && !httpOnly -> {
            ResourceVirtualFile.resolve(path, element!!.project) as? T
        }
        path.startsWith("http://") -> {
            ResourceVirtualFile.resolve("res://${path.substringAfter("http://")}.xqy", element!!.project) as? T
        }
        !path.contains("://") && !httpOnly -> {
            var file = element!!.containingFile.virtualFile
            if (file is LightVirtualFileBase) {
                file = file.originalFile
            }

            return resolveFileByPath(file, element!!.project, path) as? T
        }
        else -> null
    }
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.18) xs:QName

interface XsQNameValue : XsAnyAtomicType {
    val namespace: XsAnyUriValue?
    val prefix: XsNCNameValue?
    val localName: XsNCNameValue?
    val isLexicalQName: Boolean
}

data class XsQName(
    override val namespace: XsAnyUriValue?,
    override val prefix: XsNCNameValue?,
    override val localName: XsNCNameValue?,
    override val isLexicalQName: Boolean,
    private val reference: WeakReference<PsiElement>?
) : XsQNameValue {
    constructor(
        namespace: XsAnyUriValue?,
        prefix: XsNCNameValue?,
        localName: XsNCNameValue?,
        isLexicalQName: Boolean,
        element: PsiElement?
    ) : this(namespace, prefix, localName, isLexicalQName, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.4.1) xs:normalizedString

interface XsNormalizedStringValue : XsStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.2) xs:token

interface XsTokenValue : XsNormalizedStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.6) xs:Name

interface XsNameValue : XsTokenValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.7) xs:NCName

interface XsNCNameValue : XsNameValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.13) xs:integer

interface XsIntegerValue : XsAnyAtomicType {
    val data: BigInteger
}

fun XsIntegerValue.toInt(): Int = data.toInt()

// endregion
// region XQuery IntelliJ Plugin (2.2.3) xdm:wildcard

interface XdmWildcardValue : XsNCNameValue

// endregion
