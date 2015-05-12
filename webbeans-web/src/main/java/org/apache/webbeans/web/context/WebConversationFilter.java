/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.webbeans.web.context;

import javax.enterprise.context.ConversationScoped;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContextsService;

/**
 * A Filter with the name "CDI Conversation Filter" as required by the spec.
 *
 */
public class WebConversationFilter implements Filter
{
    private ContextsService contextsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        contextsService = WebBeansContext.currentInstance().getContextsService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        // just touch the conversation.
        contextsService.getCurrentContext(ConversationScoped.class, true);

        // otherwise business as usual...
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
        // nothing to do
    }
}
