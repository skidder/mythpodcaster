/*
 * GWTController.java
 * 
 * Created: Jun 22, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author scottkidder
 * 
 */
public class GWTController extends RemoteServiceServlet implements Controller, ServletContextAware {

  private static final long serialVersionUID = -1377657993129646093L;

  private static final Logger LOGGER = Logger.getLogger(GWTController.class);

  private transient ServletContext servletContext;

  private transient RemoteService remoteService;

  private Class<? extends RemoteService> remoteServiceClass;

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    super.doPost(request, response);
    return null;
  }

  public String processCall(String payload) throws SerializationException {
    try {

      RPCRequest rpcRequest = RPC.decodeRequest(payload, this.remoteServiceClass);

      return RPC.invokeAndEncodeResponse(this.remoteService, rpcRequest.getMethod(),
          rpcRequest.getParameters());
    } catch (IncompatibleRemoteServiceException ex) {
      LOGGER.warn("An IncompatibleRemoteServiceException was thrown while processing this call.",
          ex);
      return RPC.encodeResponseForFailure(null, ex);
    }
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  public void setRemoteService(RemoteService remoteService) {
    this.remoteService = remoteService;
    this.remoteServiceClass = this.remoteService.getClass();
  }
}
