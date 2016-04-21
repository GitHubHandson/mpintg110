/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.integration.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.integration.util.HttpHelper;


/**
 * Record client access information
 *
 */
public class AccessLogFilter implements Filter
{

	private static Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

	protected FilterConfig filterConfig;

	@Override
	public void destroy()
	{
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws ServletException, IOException
	{

		ServletRequest requestWrapper = arg0;
		try
		{
			final HttpServletRequest request = (HttpServletRequest) arg0;

			final String username = request.getRemoteUser();

			LOGGER.trace("Username : " + username);

			final String ip = HttpHelper.getIpAddress(request);

			LOGGER.trace("Client ip is : " + ip);

			final StringBuffer URL = request.getRequestURL();

			LOGGER.trace("URL of the client request is : " + URL.toString());

			requestWrapper = new FilterRequestWrapper(request);

			String body = IOUtils.toString(requestWrapper.getInputStream());

			LOGGER.trace("Request body is : " + body);
		}
		catch (Exception e)
		{
			LOGGER.error("An error occurred inside AccessLogFilter:" + e.getMessage());
		}

		chain.doFilter(requestWrapper, arg1);
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
		this.filterConfig = config;
	}

}
