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

import com.intellij.execution.process.ProcessHandler;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

public class MarkLogicRequestHandler extends ProcessHandler {
    private final Session session;
    private final Request request;

    public MarkLogicRequestHandler(Session session, Request request) {
        this.session = session;
        this.request = request;
    }

    @Override
    protected void destroyProcessImpl() {
    }

    @Override
    protected void detachProcessImpl() {
    }

    @Override
    public boolean detachIsDefault() {
        return false;
    }

    @Nullable
    @Override
    public OutputStream getProcessInput() {
        return null;
    }

    @Override
    public void startNotify() {
        super.startNotify();

        ResultSequence results;
        try {
            results = session.submitRequest(request);
        } catch (RequestException e) {
            notifyTextAvailable(e.toString(), null);
            notifyProcessDetached();
            return;
        }

        while (results.hasNext()) {
            ResultItem result = results.next();
            notifyTextAvailable(result.asString(), null);
        }

        results.close();
        notifyProcessDetached();
    }
}
