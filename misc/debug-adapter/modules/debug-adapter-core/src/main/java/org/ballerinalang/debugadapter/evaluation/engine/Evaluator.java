/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.ballerinalang.debugadapter.evaluation.engine;

import com.sun.jdi.Value;
import org.ballerinalang.debugadapter.SuspendedContext;
import org.ballerinalang.debugadapter.evaluation.EvaluationException;

public abstract class Evaluator {

    SuspendedContext context;

    public Evaluator(SuspendedContext context) {
        this.context = context;
    }

    /**
     * @throws EvaluationException
     */
    public abstract Value evaluate() throws EvaluationException;

    /**
     * In order to obtain a modifier the expression must be evaluated first
     *
     * @return a modifier object allowing to set a value in case the expression is lvalue,
     * otherwise null is returned
     */
    Modifier getModifier() {
        return null;
    }
}