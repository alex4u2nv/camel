/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.springboot.commands.crsh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.commands.AbstractLocalCamelController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;

public class CamelControllerImpl extends AbstractLocalCamelController {

    private static final Logger LOG = LoggerFactory.getLogger(CamelControllerImpl.class);

    private ListableBeanFactory beanFactory;

    CamelControllerImpl(ListableBeanFactory factory) {
        beanFactory = factory;
    }

    public List<CamelContext> getLocalCamelContexts() {
        List<CamelContext> camelContexts = new ArrayList<CamelContext>();
        try {
            camelContexts.addAll(beanFactory.getBeansOfType(CamelContext.class).values());
        } catch (Exception e) {
            LOG.warn("Cannot retrieve the list of Camel contexts.", e);
        }

        Collections.sort(camelContexts, new Comparator<CamelContext>() {
            public int compare(CamelContext o1, CamelContext o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return camelContexts;
    }

    public List<Map<String, String>> getCamelContexts() throws Exception {
        List<Map<String, String>> answer = new ArrayList<Map<String, String>>();

        List<CamelContext> camelContexts = getLocalCamelContexts();
        for (CamelContext camelContext : camelContexts) {
            Map<String, String> row = new LinkedHashMap<String, String>();
            row.put("name", camelContext.getName());
            row.put("state", camelContext.getStatus().name());
            row.put("uptime", camelContext.getUptime());
            if (camelContext.getManagedCamelContext() != null) {
                row.put("exchangesTotal", "" + camelContext.getManagedCamelContext().getExchangesTotal());
                row.put("exchangesInflight", "" + camelContext.getManagedCamelContext().getExchangesInflight());
                row.put("exchangesFailed", "" + camelContext.getManagedCamelContext().getExchangesFailed());
            } else {
                row.put("exchangesTotal", "0");
                row.put("exchangesInflight", "0");
                row.put("exchangesFailed", "0");
            }
            answer.add(row);
        }

        return answer;
    }

}
