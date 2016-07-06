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
package uk.co.reecedunn.intellij.plugin.xquery.settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryDialect;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryImplementation;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import java.io.File;

@State(
    name = "XQueryProjectSettings",
    storages = { @Storage(StoragePathMacros.WORKSPACE_FILE), @Storage("xquery_config.xml") }
)
public class XQueryProjectSettings implements PersistentStateComponent<XQueryProjectSettings>, ExportableComponent {
    private XQueryImplementation XQUERY_IMPLEMENTATION = XQueryImplementation.W3C;
    private XQueryVersion XQUERY_VERSION = XQueryVersion.XQUERY_3_0;
    private XQueryDialect XQUERY_1_0_DIALECT = XQueryDialect.XQUERY_1_0_2ED_W3C;
    private XQueryDialect XQUERY_3_0_DIALECT = XQueryDialect.XQUERY_3_0_W3C;
    private XQueryDialect XQUERY_3_1_DIALECT = XQueryDialect.XQUERY_3_1_W3C;
    private XQueryDialect XQUERY_MARKLOGIC_DIALECT = XQueryDialect.XQUERY_1_0_MARKLOGIC_8;

    public static XQueryProjectSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, XQueryProjectSettings.class);
    }

    public XQueryProjectSettings getState() {
        return this;
    }

    @Override
    public void loadState(XQueryProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @NotNull
    @Override
    public File[] getExportFiles() {
        return new File[]{ PathManager.getOptionsFile("xquery_project_settings") };
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return XQueryBundle.message("xquery.settings.project.title");
    }

    public XQueryImplementation getXQueryImplementation() {
        return XQUERY_IMPLEMENTATION;
    }

    public void setXQueryImplementation(XQueryImplementation implementation) {
        XQUERY_IMPLEMENTATION = implementation;
    }

    public XQueryVersion getXQueryVersion() {
        return XQUERY_VERSION;
    }

    public void setXQueryVersion(XQueryVersion version) {
        XQUERY_VERSION = version;
    }

    public XQueryDialect getXQueryDialectForVersion(XQueryVersion version) {
        if (version == XQueryVersion.XQUERY_0_9_MARKLOGIC) return XQueryDialect.XQUERY_0_9_MARKLOGIC_3_2; // No other dialect.
        if (version == XQueryVersion.XQUERY_1_0) return XQUERY_1_0_DIALECT;
        if (version == XQueryVersion.XQUERY_1_0_MARKLOGIC) return XQUERY_MARKLOGIC_DIALECT;
        if (version == XQueryVersion.XQUERY_3_0) return XQUERY_3_0_DIALECT;
        if (version == XQueryVersion.XQUERY_3_1) return XQUERY_3_1_DIALECT;
        throw new AssertionError("Unknown XQuery version: " + version);
    }

    public void setXQueryDialectForVersion(XQueryVersion version, XQueryDialect dialect) {
        if (version == XQueryVersion.XQUERY_1_0) XQUERY_1_0_DIALECT = dialect;
        if (version == XQueryVersion.XQUERY_1_0_MARKLOGIC) XQUERY_MARKLOGIC_DIALECT = dialect;
        if (version == XQueryVersion.XQUERY_3_0) XQUERY_3_0_DIALECT = dialect;
        if (version == XQueryVersion.XQUERY_3_1) XQUERY_3_1_DIALECT = dialect;
    }
}
