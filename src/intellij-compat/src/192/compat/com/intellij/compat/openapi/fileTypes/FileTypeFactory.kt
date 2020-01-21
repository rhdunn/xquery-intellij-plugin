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
package com.intellij.compat.openapi.fileTypes

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileTypes.FileType

class FileTypeFactory : com.intellij.openapi.fileTypes.FileTypeFactory() {
    companion object {
        val EP_NAME = ExtensionPointName.create<FileTypeBean>("com.intellij.fileType")
    }

    override fun createFileTypes(consumer: FileTypeConsumer) {
        EP_NAME.extensions.forEach { bean ->
            val type = bean.instantiate<FileType>(
                bean.implementationClass,
                ApplicationManager.getApplication().picoContainer
            )
            consumer.consume(type, bean.extensions)
        }
    }
}
