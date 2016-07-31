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
package uk.co.reecedunn.intellij.plugin.xquery.tests.settings;

import com.intellij.openapi.application.PathManager;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem;
import uk.co.reecedunn.intellij.plugin.xquery.lang.Implementations;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryProjectSettingsTest extends TestCase {
    public void testDefaultValues() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getImplementation().getID(), is("w3c"));
        assertThat(settings.getImplementationVersion().getID(), is("w3c/spec"));
        assertThat(settings.getXQueryVersion(), is(XQueryVersion.XQUERY_3_0));
        assertThat(settings.getXQuery10Dialect().getID(), is("w3c/1.0"));
        assertThat(settings.getXQuery30Dialect().getID(), is("w3c/3.0"));
        assertThat(settings.getXQuery31Dialect().getID(), is("w3c/3.1"));

        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("w3c/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("w3c/3.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("w3c/3.1"));
    }

    public void testGetState() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getState(), is(settings));
    }

    public void testLoadState() {
        ImplementationItem implementation = Implementations.getImplementations().get(0);
        ImplementationItem implementationVersion = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);
        List<ImplementationItem> implementationDialects = implementationVersion.getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        XQueryProjectSettings other = new XQueryProjectSettings();
        other.setImplementation(implementation);
        other.setImplementationVersion(implementationVersion);
        other.setXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC);
        other.setXQuery10Dialect(implementationDialects.get(0));
        other.setXQuery30Dialect(implementationDialects.get(1));
        other.setXQuery31Dialect(implementationDialects.get(2));

        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.loadState(other);
        assertThat(settings.getImplementation().getID(), is("marklogic"));
        assertThat(settings.getImplementationVersion().getID(), is("marklogic/v6"));
        assertThat(settings.getXQueryVersion(), is(XQueryVersion.XQUERY_0_9_MARKLOGIC));
        assertThat(settings.getXQuery10Dialect().getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getXQuery30Dialect().getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getXQuery31Dialect().getID(), is("marklogic/v6/1.0-ml"));

        // Setting dialects via setXQuery**Dialect() updates the corresponding getDialectForXQueryVersion()...
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("marklogic/v6/1.0-ml"));
    }

    public void testExportedFiles() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getExportFiles().length, is(1));
        assertThat(settings.getExportFiles()[0].getParent(), is(PathManager.getOptionsPath()));
        assertThat(settings.getExportFiles()[0].getName(), is("xquery_project_settings.xml"));
    }

    public void testPresentableName() {
        XQueryProjectSettings settings = new XQueryProjectSettings();
        assertThat(settings.getPresentableName(), is("XQuery Project Settings"));
    }

    public void testDialectForXQueryVersion() {
        ImplementationItem implementation = Implementations.getImplementations().get(0);
        ImplementationItem implementationVersion = implementation.getItems(ImplementationItem.IMPLEMENTATION_VERSION).get(0);
        List<ImplementationItem> implementationDialects = implementationVersion.getItems(ImplementationItem.IMPLEMENTATION_DIALECT);

        XQueryProjectSettings settings = new XQueryProjectSettings();
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_1_0, implementationDialects.get(0));
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_0, implementationDialects.get(1));
        settings.setDialectForXQueryVersion(XQueryVersion.XQUERY_3_1, implementationDialects.get(2));

        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_0_9_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0_MARKLOGIC).getID(), is(nullValue()));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_1_0).getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_0).getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getDialectForXQueryVersion(XQueryVersion.XQUERY_3_1).getID(), is("marklogic/v6/1.0-ml"));

        // Setting dialects via setDialectForXQueryVersion updates the corresponding setXQuery**Dialect()...
        assertThat(settings.getXQuery10Dialect().getID(), is("marklogic/v6/0.9-ml"));
        assertThat(settings.getXQuery30Dialect().getID(), is("marklogic/v6/1.0"));
        assertThat(settings.getXQuery31Dialect().getID(), is("marklogic/v6/1.0-ml"));
    }
}
