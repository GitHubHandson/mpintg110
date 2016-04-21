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
package de.hybris.platform.marketplaceintegrationbackoffice.utils;

import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;


/**
 * RestTemplateUtil
 */
public class MarketplaceintegrationbackofficeRestTemplateUtil
{

	private static final Logger LOGGER = LoggerFactory.getLogger(MarketplaceintegrationbackofficeRestTemplateUtil.class);

	private static RestTemplate restTemplate;

	private String baseAuthUser;

	private String baseAuthPwd;

	private String defaultContentType;

	public MarketplaceintegrationbackofficeRestTemplateUtil()
	{
		initializationTemplate();
	}

	/**
	 * @param url
	 * @param content
	 *           It must be json format data
	 * @return Results <br>
	 *         code : http status <br>
	 *         responseBody : http response body
	 */
	public JSONObject post(final String url, final String content)
	{
		LOGGER.debug("Post url:" + url);

		final HttpEntity<String> request = new HttpEntity<String>(content, createHeaders());
		final ResponseEntity<String> entity = restTemplate.postForEntity(url, request, String.class);

		LOGGER.debug("Post content :" + content);
		LOGGER.debug("Response body:" + entity.getBody());

		JSONObject responseObject = new JSONObject();
		if (entity.getStatusCode().value() == 200)
		{
			final String responseBody = entity.getBody();
			final JSONParser parser = new JSONParser();
			responseObject = (JSONObject) parser.parse(responseBody);
		}
		else
		{
			responseObject.put("code", entity.getStatusCode().toString());
			responseObject.put("msg", entity.getBody());
		}
		return responseObject;
	}

	/**
	 * @return RestTemplate Instance
	 */
	public RestTemplate getRestTemplateInstance()
	{
		return restTemplate;
	}

	private RestTemplate initializationTemplate()
	{
		final ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName("UTF-8")).build();
		final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
		final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
		final ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);

		registryBuilder.register("https", setUpSSL());

		final Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
		connManager.setDefaultConnectionConfig(connConfig);
		connManager.setDefaultSocketConfig(socketConfig);
		connManager.setMaxTotal(1000);
		connManager.setDefaultMaxPerRoute(1000);
		final HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

		final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		clientHttpRequestFactory.setConnectTimeout(5000);
		clientHttpRequestFactory.setReadTimeout(5000);
		clientHttpRequestFactory.setConnectionRequestTimeout(200);

		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate;
	}

	public String getUUID()
	{
		final UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}

	/**
	 * @return HttpHeaders
	 */
	public HttpHeaders createHeaders()
	{
		final String encoding = Base64.getEncoder().encodeToString(String.format("%s:%s", baseAuthUser, baseAuthPwd).getBytes());
		final String authHeader = "Basic " + encoding;
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add("Content-Type", defaultContentType);
		headers.add("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36");
		headers.add("Accept-Encoding", "gzip,deflate");
		headers.add("Accept-Language", "zh-CN");
		headers.add("Connection", "Keep-Alive");
		return headers;
	}

	private LayeredConnectionSocketFactory setUpSSL()
	{
		LayeredConnectionSocketFactory sslSF = null;
		try
		{
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			final SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy())
					.build();
			sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return sslSF;
	}

	class AnyTrustStrategy implements TrustStrategy
	{

		@Override
		public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException
		{
			return true;
		}

	}

	/**
	 * @param defaultContentType
	 *           the defaultContentType to set
	 */
	@Required
	public void setDefaultContentType(final String defaultContentType)
	{
		this.defaultContentType = defaultContentType;
	}

	/**
	 * @param baseAuthUser
	 *           the baseAuthUser to set
	 */
	public void setBaseAuthUser(final String baseAuthUser)
	{
		this.baseAuthUser = baseAuthUser;
	}

	/**
	 * @param baseAuthPwd
	 *           the baseAuthPwd to set
	 */
	public void setBaseAuthPwd(final String baseAuthPwd)
	{
		this.baseAuthPwd = baseAuthPwd;
	}
}
