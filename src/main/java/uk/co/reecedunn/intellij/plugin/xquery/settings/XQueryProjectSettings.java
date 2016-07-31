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
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;

import java.io.File;

@State(
    name = "XQueryProjectSettings",
    storages = { @Storage(StoragePathMacros.WORKSPACE_FILE), @Storage("xquery_config.xml") }
)
public class XQueryProjectSettings implements PersistentStateComponent<XQueryProjectSettings>, ExportableComponent {
    private ImplementationItem IMPLEMENTATION = Implementations.getDefaultImplementation();
    private ImplementationItem IMPLEMENTATION_VERSION = IMPLEMENTATION.getDefaultItem(ImplementationItem.IMPLEMENTATION_VERSION);
    private XQueryVersion XQUERY_VERSION = XQueryVersion.parse(IMPLEMENTATION_VERSION.getDefaultItem(ImplementationItem.XQUERY_VERSION).toString());
    private ImplementationItem XQUERY_1_0_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_1_0);
    private ImplementationItem XQUERY_3_0_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_3_0);
    private ImplementationItem XQUERY_3_1_DIALECT = IMPLEMENTATION_VERSION.getDefaultItemForXQueryVersion(ImplementationItem.IMPLEMENTATION_DIALECT, XQueryVersion.XQUERY_3_1);

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

    public ImplementationItem getImplementation() {
        return IMPLEMENTATION;
    }

    public void setImplementation(ImplementationItem implementation) {
        IMPLEMENTATION = implementation;
    }

    public ImplementationItem getImplementationVersion() {
        return IMPLEMENTATION_VERSION;
    }

    public void setImplementationVersion(ImplementationItem version) {
        IMPLEMENTATION_VERSION = version;
    }

    public XQueryVersion getXQueryVersion() {
        return XQUERY_VERSION;
    }

    public void setXQueryVersion(XQueryVersion version) {
        XQUERY_VERSION = version;
    }

    public ImplementationItem getXQuery10Dialect() {
        return XQUERY_1_0_DIALECT;
    }

    public void setXQuery10Dialect(ImplementationItem dialect) {
        XQUERY_1_0_DIALECT = dialect;
    }

    public ImplementationItem getXQuery30Dialect() {
        return XQUERY_3_0_DIALECT;
    }

    public void setXQuery30Dialect(ImplementationItem dialect) {
        XQUERY_3_0_DIALECT = dialect;
    }

    public ImplementationItem getXQuery31Dialect() {
        return XQUERY_3_1_DIALECT;
    }

    public void setXQuery31Dialect(ImplementationItem dialect) {
        XQUERY_3_1_DIALECT = dialect;
    }

    public ImplementationItem getDialectForXQueryVersion(XQueryVersion version) {
        if (version == XQueryVersion.XQUERY_1_0) {
            return getXQuery10Dialect();
        } else if (version == XQueryVersion.XQUERY_3_0) {
            return getXQuery30Dialect();
        } else if (version == XQueryVersion.XQUERY_3_1) {
            return getXQuery31Dialect();
        }
        return ImplementationItem.NULL_ITEM;
    }

    public void setDialectForXQueryVersion(XQueryVersion version, ImplementationItem dialect) {
        if (version == XQueryVersion.XQUERY_1_0) {
            setXQuery10Dialect(dialect);
        } else if (version == XQueryVersion.XQUERY_3_0) {
            setXQuery30Dialect(dialect);
        } else if (version == XQueryVersion.XQUERY_3_1) {
            setXQuery31Dialect(dialect);
        }
    }
}
