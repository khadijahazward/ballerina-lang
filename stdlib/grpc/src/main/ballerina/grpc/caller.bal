// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

# Provides the gRPC actions for interacting with caller.
#
# + instanceId - The connection id
# + remoteAddress - The remote address
# + localAddress - The local address
public type Caller client object {

    public Remote remoteDetails = {};
    public Local local = {};

    private int instanceId = -1;

    public function getId() returns int {
        return self.instanceId;
    }

    # Sends outbound response to the caller.
    #
    # + res - - The outbound response message.
    # + headers - - Optional headers parameter. Passes header value if needed. Default sets to nil.
    # + return - - Returns an error if encounters an error while sending the response, returns nil otherwise.
    public remote extern function send(any res, Headers? headers = ()) returns error?;

    # Informs the caller, server finished sending messages.
    #
    # + return - Returns an error if encounters an error while sending the response, returns nil otherwise.
    public remote extern function complete() returns error?;

    # Checks whether the connection is closed by the caller.
    #
    # + return - Returns true, if caller already closed the connection. false otherwise.
    public remote extern function isCancelled() returns boolean;

    # Sends server error to the caller.
    #
    # + statusCode - Error status code.
    # + message - Error message.
    # + headers - Optional headers parameter. Passes header value if needed. Default sets to nil.
    # + return - Returns an error if encounters an error while sending the response, returns nil otherwise.
    public remote extern function sendError(int statusCode, string message, Headers? headers = ()) returns error?;
};

# Presents a read-only view of the remote address.
#
# + host - The remote host name/IP
# + port - The remote port
public type Remote record {
    string host = "";
    int port = 0;
    !...
};

# Presents a read-only view of the local address.
#
# + host - The local host name/IP
# + port - The local port
public type Local record {
    string host = "";
    int port = 0;
    !...
};
