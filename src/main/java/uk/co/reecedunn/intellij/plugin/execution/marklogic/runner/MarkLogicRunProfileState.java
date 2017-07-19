/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.execution.marklogic.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PathUtil;
import com.marklogic.xcc.*;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.execution.marklogic.configuration.MarkLogicRunConfiguration;

import java.io.*;

public class MarkLogicRunProfileState extends CommandLineState {
    public MarkLogicRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        MarkLogicRunConfiguration configuration = (MarkLogicRunConfiguration)getEnvironment().getRunProfile();
        ContentSource source = createContentSource(configuration);
        Session session = source.newSession();
        RequestOptions options = createRequestOptions(configuration);
        Request request = session.newAdhocQuery(getQueryFromFile(configuration.getMainModulePath()), options);
        return new MarkLogicRequestHandler(session, request);
    }

    private ContentSource createContentSource(MarkLogicRunConfiguration configuration) {
        if (configuration.getUserName().isEmpty()) {
            return ContentSourceFactory.newContentSource(
                configuration.getServerHost(),
                configuration.getServerPort());
        }

        return ContentSourceFactory.newContentSource(
            configuration.getServerHost(),
            configuration.getServerPort(),
            configuration.getUserName(),
            configuration.getPassword());
    }

    private RequestOptions createRequestOptions(MarkLogicRunConfiguration configuration) {
        final String ext = PathUtil.getFileExtension(configuration.getMainModulePath());

        RequestOptions options = new RequestOptions();
        options.setQueryLanguage(configuration.getQueryLanguageFromExtension(ext));
        return options;
    }

    private String getQueryFromFile(String fileName) {
        final String url = VfsUtil.pathToUrl(fileName.replace(File.separatorChar, '/'));
        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null) {
            try {
                return streamToString(file.getInputStream());
            } catch (IOException e) {
                //
            }
        }
        return null;
    }

    private String streamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        return writer.toString();
    }
}
