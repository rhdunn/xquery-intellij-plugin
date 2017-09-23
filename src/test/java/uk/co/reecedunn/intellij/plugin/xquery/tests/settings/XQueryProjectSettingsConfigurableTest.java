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

import com.intellij.ide.ui.UISettings;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettingsConfigurable;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import javax.swing.*;
import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XQueryProjectSettingsConfigurableTest extends ParserTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerApplicationService(UISettings.class, new UISettings());
    }

    private Component getComponentByName(JComponent component, String name) {
        for (Component child : component.getComponents()) {
            if (child.getName() != null && child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    private Object getSelectedItem(JComponent component, String name) {
        JComboBox comboBox = (JComboBox)getComponentByName(component, name);
        if (comboBox != null) {
            return comboBox.getSelectedItem();
        }
        return null;
    }

    private void setSelectedItem(JComponent component, String name, Object item) {
        JComboBox comboBox = (JComboBox)getComponentByName(component, name);
        if (comboBox != null) {
            comboBox.setSelectedItem(item);
        }
    }

    public void testDisplayName() {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        assertThat(configurable.getDisplayName(), is("XQuery"));
    }

    public void testHelpTopic() {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        assertThat(configurable.getHelpTopic(), is(nullValue()));
    }

    /*
    @SuppressWarnings("ConstantConditions")
    public void testCreateComponent() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();
        assertThat(component.getClass().getName(), is(JPanel.class.getName()));

        assertThat(getComponentByName(component, "Implementation"), is(notNullValue()));
        assertThat(getComponentByName(component, "ImplementationVersion"), is(notNullValue()));
        assertThat(getComponentByName(component, "XQueryVersion"), is(notNullValue()));
        assertThat(getComponentByName(component, "DialectForXQuery1.0"), is(notNullValue()));
        assertThat(getComponentByName(component, "DialectForXQuery3.0"), is(notNullValue()));
        assertThat(getComponentByName(component, "DialectForXQuery3.1"), is(notNullValue()));

        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "ImplementationVersion").toString(), is("Specification"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0").toString(), is("XQuery"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1").toString(), is("XQuery"));

        assertThat(configurable.isModified(), is(false));
    }

    @SuppressWarnings("ConstantConditions")
    public void testApply() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        setSelectedItem(component, "Implementation", Implementations.getItemById("marklogic"));

        assertThat(getSelectedItem(component, "Implementation").toString(), is("MarkLogic"));
        assertThat(getSelectedItem(component, "ImplementationVersion").toString(), is("MarkLogic 8"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0").toString(), is("Not Supported"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1").toString(), is("Not Supported"));

        assertThat(configurable.isModified(), is(true));

        configurable.apply();
        XQueryProjectSettings settings = XQueryProjectSettings.Companion.getInstance(myProject);
        assertThat(settings.getImplementationVersion(), is("marklogic/v8"));
        assertThat(settings.getXQueryVersion(), is(XQueryVersion.VERSION_1_0));
        assertThat(settings.getXQuery10Dialect(), is("marklogic/v8/1.0"));
        assertThat(settings.getXQuery30Dialect(), is(nullValue()));
        assertThat(settings.getXQuery31Dialect(), is(nullValue()));

        configurable.disposeUIResources();
    }

    @SuppressWarnings("ConstantConditions")
    public void testReset() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        setSelectedItem(component, "Implementation", Implementations.getItemById("marklogic"));
        configurable.reset();

        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "ImplementationVersion").toString(), is("Specification"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0").toString(), is("XQuery"));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1").toString(), is("XQuery"));

        assertThat(configurable.isModified(), is(false));

        configurable.disposeUIResources();
    }

    @SuppressWarnings("ConstantConditions")
    public void testPreservingXQueryVersionSelection() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));

        setSelectedItem(component, "Implementation", Implementations.getItemById("marklogic"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("MarkLogic"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));

        setSelectedItem(component, "XQueryVersion", XQueryVersion.VERSION_1_0);
        assertThat(getSelectedItem(component, "Implementation").toString(), is("MarkLogic"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));

        setSelectedItem(component, "Implementation", Implementations.getItemById("w3c"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQueryVersion.VERSION_1_0));

        configurable.disposeUIResources();
    }

    @SuppressWarnings("ConstantConditions")
    public void testPreservingXQueryDialectSelection() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));

        setSelectedItem(component, "Implementation", Implementations.getItemById("saxon/EE"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("Saxon Enterprise Edition"));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));

        setSelectedItem(component, "ImplementationVersion", Implementations.getItemById("saxon/EE/v9.7"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("Saxon Enterprise Edition"));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery"));

        setSelectedItem(component, "DialectForXQuery1.0", Implementations.getItemById("saxon/EE/v9.7/1.0-update"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("Saxon Enterprise Edition"));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery Update Facility 1.0"));

        setSelectedItem(component, "Implementation", Implementations.getItemById("w3c"));
        assertThat(getSelectedItem(component, "Implementation").toString(), is("W3C"));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0").toString(), is("XQuery Update Facility 1.0"));

        configurable.disposeUIResources();
    }
    */
}
