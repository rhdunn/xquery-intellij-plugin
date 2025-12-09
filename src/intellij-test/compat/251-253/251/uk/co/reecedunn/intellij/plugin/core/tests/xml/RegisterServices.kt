// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.xml

import com.intellij.lang.xml.BackendXmlElementFactory
import com.intellij.lang.xml.BasicXmlElementFactory
import com.intellij.mock.MockComponentManager
import com.intellij.openapi.application.Application

fun Application.registerBasicXmlElementFactory() {
    (this as MockComponentManager).registerService(BasicXmlElementFactory::class.java, BackendXmlElementFactory())
}
