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
package de.hybris.platform.mpintgproductcockpit.productcockpit.util.impl;



import de.hybris.platform.mpintgproductcockpit.productcockpit.util.MarketplaceintegrationHttpUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;


/**
 * HTTP utility
 */
public class MarketplaceintegrationHttpUtilImpl implements MarketplaceintegrationHttpUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceintegrationHttpUtilImpl.class);
	private URL httpURL;
	private Map<String, String> httpHeaders = new HashMap<>();
	private String baseAuthUser;
	private String baseAuthPassword;

	/**
	 * Constructor
	 */
	public MarketplaceintegrationHttpUtilImpl()
	{
		try
		{
			trustAllSSLCerts();
		}
		catch (KeyManagementException | NoSuchAlgorithmException e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Initialize BASE Authentication
	 */
	public void initBaseAuth(final String baseAuthUser, final String baseAuthPassword)
	{
		this.baseAuthUser = baseAuthUser;
		this.baseAuthPassword = baseAuthPassword;
	}

	/**
	 * Add header
	 *
	 * @param header
	 * @param value
	 */
	public void addHeader(final String header, final String value)
	{
		httpHeaders.put(header, value);
	}

	/**
	 * Post data
	 *
	 * @param url
	 * @param data
	 * @return boolean
	 * @throws IOException
	 */
	@Override
	public boolean post(final String url, final String data) throws IOException
	{
		httpURL = new URL(url);
		final HttpURLConnection conn = (HttpURLConnection) httpURL.openConnection();
		conn.setDoOutput(true);
		initConnection(conn);
		OutputStreamWriter writer = null;
		try
		{
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (final IOException ex)
				{
					LOG.error(ex.getMessage(), ex);
				}
			}
		}
		final int status = conn.getResponseCode();
		conn.disconnect();
		return status == HttpURLConnection.HTTP_OK;
	}

	private void initConnection(final HttpURLConnection conn)
	{
		if (!StringUtils.isEmpty(baseAuthUser) && !StringUtils.isEmpty(baseAuthPassword))
		{
			final String encoding = Base64.getEncoder().encodeToString(
					String.format("%s:%s", baseAuthUser, baseAuthPassword).getBytes());

			conn.setRequestProperty("Authorization", "Basic " + encoding);
		}
		for (final Entry<String, String> entry : httpHeaders.entrySet())
		{
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}


	private void trustAllSSLCerts() throws NoSuchAlgorithmException, KeyManagementException
	{
		final TrustManager[] trustAllCerts =
		{ new X509TrustManager()
		{
			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] certs, final String authType)
			{
				//
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] certs, final String authType)
			{
				//
			}
		} };
		final SSLContext sc = SSLContext.getInstance("SSL");
		final HostnameVerifier hv = new HostnameVerifier()
		{
			@Override
			public boolean verify(final String arg0, final SSLSession arg1)
			{
				return true;
			}
		};
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	@Required
	public void setBaseAuthUser(final String baseAuthUser)
	{
		this.baseAuthUser = baseAuthUser;
	}

	@Required
	public void setBaseAuthPassword(final String baseAuthPassword)
	{
		this.baseAuthPassword = baseAuthPassword;
	}

	public void setHttpHeaders(final Map<String, String> httpHeaders)
	{
		this.httpHeaders = httpHeaders;
	}
}
