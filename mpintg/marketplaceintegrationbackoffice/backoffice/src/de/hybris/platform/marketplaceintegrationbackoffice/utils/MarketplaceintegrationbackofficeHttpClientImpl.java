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
package de.hybris.platform.marketplaceintegrationbackoffice.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;


public class MarketplaceintegrationbackofficeHttpClientImpl implements MarketplaceintegrationbackofficeHttpClient
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceintegrationbackofficeHttpClientImpl.class);
	private MarketplaceintegrationbackofficeHttpUtil httpUtil;

	@Override
	public boolean sendPostRequest(final String requestUrl, final String requestBody) throws IOException
	{
		return httpUtil.post(requestUrl, requestBody);
	}

	@Override
	public boolean redirectRequest(final String requestUrl) throws IOException
	{

		final boolean result = true;
		final RestTemplate restTemplate = new RestTemplate();

		final java.net.URI uri = java.net.URI.create(requestUrl);
		final java.awt.Desktop dp = java.awt.Desktop.getDesktop();
		if (dp.isSupported(java.awt.Desktop.Action.BROWSE))
		{
			dp.browse(uri);
		}
		restTemplate.execute(requestUrl, HttpMethod.GET, new RequestCallback()
		{
			@Override
			public void doWithRequest(final ClientHttpRequest request) throws IOException
			{
				// empty block should be documented
			}
		}, new ResponseExtractor<Object>()
		{
			@Override
			public Object extractData(final ClientHttpResponse response) throws IOException
			{
				final HttpStatus statusCode = response.getStatusCode();
				LOG.debug("Response status: " + statusCode.toString());
				return response.getStatusCode();
			}
		});
		return result;
	}

	/**
	 * @param httpUtil
	 *           the httpUtil to set
	 */
	public void setHttpUtil(final MarketplaceintegrationbackofficeHttpUtil httpUtil)
	{
		this.httpUtil = httpUtil;
	}
}
