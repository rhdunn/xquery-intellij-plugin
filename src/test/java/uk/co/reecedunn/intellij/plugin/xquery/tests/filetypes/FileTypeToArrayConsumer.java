/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.filetypes;

import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class FileTypeToArrayConsumer implements FileTypeConsumer {
    final List<Pair<FileType, String>> fileTypes = new ArrayList<>();
    final List<Pair<FileType, FileNameMatcher>> fileMatchers = new ArrayList<>();

    @Override
    public void consume(@NotNull FileType fileType) {
        fileTypes.add(Pair.create(fileType, null));
    }

    @Override
    public void consume(@NotNull FileType fileType, @NonNls String extensions) {
        fileTypes.add(Pair.create(fileType, extensions));
    }

    @Override
    public void consume(@NotNull FileType fileType, @NotNull FileNameMatcher... matchers) {
        for (FileNameMatcher matcher : matchers) {
            fileMatchers.add(Pair.create(fileType, matcher));
        }
    }

    @Nullable
    @Override
    public FileType getStandardFileTypeByName(@NonNls @NotNull String name) {
        return null;
    }
}
