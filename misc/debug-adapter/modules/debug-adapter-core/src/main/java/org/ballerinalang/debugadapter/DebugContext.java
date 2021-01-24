/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.ballerinalang.debugadapter;

import io.ballerina.projects.Project;
import org.ballerinalang.debugadapter.jdi.VirtualMachineProxyImpl;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

/**
 * Debugging session context.
 */
public class DebugContext {

    private Process launchedProcess;
    private IDebugProtocolClient client;
    private final JBallerinaDebugServer adapter;
    private VirtualMachineProxyImpl debuggee;
    private Project sourceProject;

    DebugContext(JBallerinaDebugServer adapter) {
        this.adapter = adapter;
    }

    public Process getLaunchedProcess() {
        return launchedProcess;
    }

    public void setLaunchedProcess(Process launchedProcess) {
        this.launchedProcess = launchedProcess;
    }

    public IDebugProtocolClient getClient() {
        return client;
    }

    public void setClient(IDebugProtocolClient client) {
        this.client = client;
    }

    public JBallerinaDebugServer getAdapter() {
        return adapter;
    }

    public VirtualMachineProxyImpl getDebuggee() {
        return debuggee;
    }

    public void setDebuggee(VirtualMachineProxyImpl debuggee) {
        this.debuggee = debuggee;
    }

    public Project getSourceProject() {
        return sourceProject;
    }

    public void setSourceProject(Project sourceProject) {
        this.sourceProject = sourceProject;
    }
}
