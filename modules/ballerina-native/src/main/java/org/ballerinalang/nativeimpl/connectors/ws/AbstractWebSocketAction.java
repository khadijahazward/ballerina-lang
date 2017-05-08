/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.nativeimpl.connectors.ws;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.natives.connectors.AbstractNativeAction;
import org.ballerinalang.natives.connectors.BallerinaConnectorManager;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.exceptions.ClientConnectorException;

import javax.websocket.Session;

/**
 * Abstract class for WebSocket native actions.
 */
public abstract class AbstractWebSocketAction extends AbstractNativeAction {

    protected String getClientID(Context context, BConnector bconnector) {
        WebSocketClientConnector connector = (WebSocketClientConnector) bconnector.value();
        Session session = (Session) context.getCarbonMessage().getProperty(
                org.ballerinalang.services.dispatchers.ws.Constants.WEBSOCKET_SESSION);
        return connector.getConnectionID(session);
    }

    protected void pushMessage(CarbonMessage carbonMessage) {
        org.wso2.carbon.messaging.ClientConnector clientConnector =
                BallerinaConnectorManager.getInstance().getClientConnector(Constants.PROTOCOL_WEBSOCKET);
        try {
            clientConnector.send(carbonMessage, null);
        } catch (ClientConnectorException e) {
            throw new BallerinaException("Error occurred when pushing the message");
        }
    }
}
