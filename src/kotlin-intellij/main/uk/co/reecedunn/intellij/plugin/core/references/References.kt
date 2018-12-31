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
package uk.co.reecedunn.intellij.plugin.core.references

import java.lang.ref.SoftReference
import kotlin.reflect.KProperty

@Suppress("FunctionName")
fun <T> soft_reference(f: () -> T): SoftReference<T> {
    return SoftReference(f())
}

operator fun <T> SoftReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return com.intellij.reference.SoftReference.dereference(this)!!
}
