// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.javaee

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.ResourceRegistrarImpl

fun ResourceRegistrar.addInternalResource(resource: String, fileName: String?, klass: Class<*>) {
    (this as ResourceRegistrarImpl).addInternalResource(resource, fileName, klass.classLoader)
}
