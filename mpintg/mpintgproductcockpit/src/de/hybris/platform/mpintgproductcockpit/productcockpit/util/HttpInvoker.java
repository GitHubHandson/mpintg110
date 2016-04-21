/*
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package de.hybris.platform.mpintgproductcockpit.productcockpit.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


/**
 * @author Hybris Localization
 *
 */
public class HttpInvoker
{

	private static HttpInvoker invoker = null;

	/**
	 * @return
	 */
	public static HttpInvoker getSingletInstance()
	{
		return invoker == null ? invoker = new HttpInvoker() : invoker;
	}

	/**
	 * @return
	 */
	public static HttpInvoker getNotSingletInstance()
	{
		return new HttpInvoker();
	}

	/**
	 * @param reqURL
	 * @param reqStr
	 * @param charset
	 * @param timeout
	 * @return
	 */
	public String invoke(final String reqURL, final String reqStr, final String charset, final int timeout)
	{
		return getSingletInstance().post(reqURL, reqStr, charset, timeout);
	}


	public String post(final String reqURL, final String reqStr, final String charset, final int timeout)
	{
		// Prepare HTTP post
		final HttpPost post = new HttpPost(reqURL);
		post.setHeader("Content-Type", "application/json");

		// set content type as json and charset
		final StringEntity reqEntity = new StringEntity(reqStr, ContentType.create("application/json", charset));
		post.setEntity(reqEntity);

		// Create HTTP client
		final HttpClient httpClient = new DefaultHttpClient();

		// set connection over time
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(timeout / 2));
		// set data loading over time
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(timeout / 2));

		// Execute request
		try
		{
			final HttpResponse response = httpClient.execute(post);
			// get response and return as String
			final HttpEntity responseEntity = response.getEntity();
			return EntityUtils.toString(responseEntity);
		}
		catch (final Exception e)
		{
			return null;
		}
		finally
		{
			post.releaseConnection();
		}
	}


	public static void main(final String args[])
	{
		final HttpInvoker invoker = new HttpInvoker();

		final String response = invoker.invoke("http://10.207.206.63:8081//tmallapp/publishItem/schemajson/162116", "", "UTF-8",
				100000);
		System.out.println(response);
	}
}
