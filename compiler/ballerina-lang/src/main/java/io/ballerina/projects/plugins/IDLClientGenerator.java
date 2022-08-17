/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.projects.plugins;

import io.ballerina.compiler.syntax.tree.Node;

/**
 * Represent an IDL client analysis task.
 *
 * @since 2.3.0
 */
public abstract class IDLClientGenerator {

    /**
     * Checks whether the plugin can support code generation for the passed client node.
     *
     * @param clientNode the client syntax node
     * @return whether the client can be generated
     */
    public abstract boolean canHandle(Node clientNode);

    /**
     * Performs a IDL generation with the passed context.
     *
     * @param generatorContext IDL client source generator context
     */
    public abstract void perform(IDLSourceGeneratorContext generatorContext);
}
