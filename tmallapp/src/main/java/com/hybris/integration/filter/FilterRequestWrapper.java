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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;


public class FilterRequestWrapper extends HttpServletRequestWrapper
{

	private final String body;

	public FilterRequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
		body = IOUtils.toString(request.getInputStream());
	}

	public String getRequestBody()
	{
		return this.body;
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{

		final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8")));

		return new ServletInputStream()
		{

			@Override
			public int read() throws IOException
			{
				return bais.read();
			}

			@Override
			public boolean isFinished()
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isReady()
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setReadListener(ReadListener arg0)
			{
				// TODO Auto-generated method stub

			}


		};
	}
}
