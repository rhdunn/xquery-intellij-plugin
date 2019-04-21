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
package uk.co.reecedunn.intellij.plugin.core.reflection

import java.lang.reflect.Constructor
import java.lang.reflect.Method

fun Class<*>.getConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<*>? {
    return try {
        getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.getMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.getAnyMethod(vararg names: String): Method {
    return names.asSequence().map { name -> getMethodOrNull(name) }.filterNotNull().first()
}
