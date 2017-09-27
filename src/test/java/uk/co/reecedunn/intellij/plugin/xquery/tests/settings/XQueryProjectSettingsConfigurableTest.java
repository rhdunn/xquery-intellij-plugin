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
import com.intellij.openapi.options.ConfigurationException;
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic;
import uk.co.reecedunn.intellij.plugin.xquery.lang.W3C;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettingsConfigurable;
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase;

import javax.swing.*;
import java.awt.*;

import static org.hamcrest.CoreMatchers.*;
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

        assertThat(getSelectedItem(component, "Implementation"), is(W3C.INSTANCE.getSPECIFICATIONS()));
        assertThat(getSelectedItem(component, "ImplementationVersion"), is(W3C.INSTANCE.getFIRST_EDITION()));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQuery.INSTANCE.getREC_1_0_20070123()));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), is(XQuery.INSTANCE));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), is(XQuery.INSTANCE));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), is(XQuery.INSTANCE));

        assertThat(configurable.isModified(), is(false));
    }

    @SuppressWarnings("ConstantConditions")
    public void testApply() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        setSelectedItem(component, "Implementation", MarkLogic.INSTANCE.getMARKLOGIC());

        assertThat(getSelectedItem(component, "Implementation"), is(MarkLogic.INSTANCE.getMARKLOGIC()));
        assertThat(getSelectedItem(component, "ImplementationVersion"), is(MarkLogic.INSTANCE.getVERSION_6_0()));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQuery.INSTANCE.getREC_1_0_20070123()));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), is(XQuery.INSTANCE));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), is(nullValue()));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), is(nullValue()));

        assertThat(configurable.isModified(), is(true));

        configurable.apply();
        XQueryProjectSettings settings = XQueryProjectSettings.Companion.getInstance(myProject);
        assertThat(settings.getImplementationVersion(), is("marklogic/v6"));
        assertThat(settings.getXQueryVersion(), is("1.0"));
        assertThat(settings.getXQuery10Dialect(), is("xquery"));
        assertThat(settings.getXQuery30Dialect(), is(nullValue()));
        assertThat(settings.getXQuery31Dialect(), is(nullValue()));

        configurable.disposeUIResources();
    }

    @SuppressWarnings("ConstantConditions")
    public void testReset() throws ConfigurationException {
        XQueryProjectSettingsConfigurable configurable = new XQueryProjectSettingsConfigurable(myProject);
        JComponent component = configurable.createComponent();

        setSelectedItem(component, "Implementation", MarkLogic.INSTANCE.getMARKLOGIC());
        configurable.reset();

        assertThat(getSelectedItem(component, "Implementation"), is(W3C.INSTANCE.getSPECIFICATIONS()));
        assertThat(getSelectedItem(component, "ImplementationVersion"), is(W3C.INSTANCE.getFIRST_EDITION()));
        assertThat(getSelectedItem(component, "XQueryVersion"), is(XQuery.INSTANCE.getREC_1_0_20070123()));
        assertThat(getSelectedItem(component, "DialectForXQuery1.0"), is(XQuery.INSTANCE));
        assertThat(getSelectedItem(component, "DialectForXQuery3.0"), is(XQuery.INSTANCE));
        assertThat(getSelectedItem(component, "DialectForXQuery3.1"), is(XQuery.INSTANCE));

        assertThat(configurable.isModified(), is(false));

        configurable.disposeUIResources();
    }
}
