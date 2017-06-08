/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.transport.rest.servlet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.servicecomb.core.Const;
import io.servicecomb.core.Invocation;
import io.servicecomb.core.transport.AbstractTransport;
import io.servicecomb.transport.rest.client.RestTransportClient;
import io.servicecomb.transport.rest.client.RestTransportClientManager;
import io.servicecomb.foundation.common.net.URIEndpointObject;
import io.servicecomb.swagger.invocation.AsyncResponse;

@Component
public class ServletRestTransport extends AbstractTransport {
    @Override
    public String getName() {
        return Const.RESTFUL;
    }

    @Override
    public boolean init() throws Exception {
        String listenAddress = ServletConfig.getLocalServerAddress();
        if (!StringUtils.isEmpty(listenAddress)) {
            setListenAddressWithoutSchema(listenAddress);
        }

        return true;
    }

    @Override
    public void send(Invocation invocation, AsyncResponse asyncResp) throws Exception {
        URIEndpointObject endpoint = (URIEndpointObject) invocation.getEndpoint().getAddress();
        RestTransportClient client =
            RestTransportClientManager.INSTANCE.getRestTransportClient(endpoint.isSslEnabled());
        client.send(invocation, asyncResp);
    }
}
