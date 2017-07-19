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
package uk.co.reecedunn.intellij.plugin.execution.marklogic.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.ArrayUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsEditorImpl;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI;
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUIFactory;
import uk.co.reecedunn.intellij.plugin.execution.marklogic.runner.MarkLogicRunProfileState;

public class MarkLogicRunConfiguration extends RunConfigurationBase implements SettingsUIFactory<MarkLogicRunConfiguration> {
    private static final String QUERY_LANGUAGE_JAVASCRIPT = "javascript";
    private static final String QUERY_LANGUAGE_XQUERY = "xquery";

    public static final String[] EXTENSIONS = new String[]{
        "xq", "xqy", "xquery", "xql", "xqu",
        "js",
    };
    private static final String[] QUERY_LANGUAGES = new String[]{
        QUERY_LANGUAGE_XQUERY, QUERY_LANGUAGE_XQUERY, QUERY_LANGUAGE_XQUERY, QUERY_LANGUAGE_XQUERY, QUERY_LANGUAGE_XQUERY,
        QUERY_LANGUAGE_JAVASCRIPT,
    };

    static class ConfigData {
        public String serverHost = "localhost";
        public int serverPort = 8000;
        public String userName = "";
        public String password = "";
        public String mainModulePath = "";
        public String queryLanguage = "";
    }

    private ConfigData data = new ConfigData();

    MarkLogicRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new SettingsEditorImpl<>(this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new MarkLogicRunProfileState(environment);
    }

    @Override
    public SettingsUI<MarkLogicRunConfiguration> createSettingsUI() {
        return new MarkLogicSettingsUI(getProject());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        DefaultJDOMExternalizer.readExternal(data, element);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        DefaultJDOMExternalizer.writeExternal(data, element);
    }

    public String getServerHost() {
        return data.serverHost;
    }

    public void setServerHost(String host) {
        data.serverHost = host;
    }

    public int getServerPort() {
        return data.serverPort;
    }

    public void setServerPort(int port) {
        data.serverPort = port;
    }

    public String getUserName() {
        return data.userName;
    }

    public void setUserName(String userName) {
        this.data.userName = userName;
    }

    public String getPassword() {
        return data.password;
    }

    public void setPassword(String password) {
        this.data.password = password;
    }

    public String getMainModulePath() {
        return data.mainModulePath;
    }

    public void setMainModulePath(@NotNull String mainModulePath) {
        this.data.mainModulePath = mainModulePath;
    }

    public String getQueryLanguageFromExtension(@Nullable String ext) {
        int index = ArrayUtil.indexOf(EXTENSIONS, ext);
        return (index > 0) ? QUERY_LANGUAGES[index] : null;
    }
}
