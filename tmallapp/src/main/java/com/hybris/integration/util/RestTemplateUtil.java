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
package com.hybris.integration.util;

import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.annotation.PostConstruct;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hybris.integration.exception.TmallAppException;


/**
 * RestTemplateUtil
 */
@Component
public class RestTemplateUtil
{
	private static Logger LOGGER = LoggerFactory.getLogger(RestTemplateUtil.class);

	private static RestTemplate restTemplate;

	@Value("${datahub.auth.username}")
	private String datahubAuthUsername;

	@Value("${datahub.auth.password}")
	private String datahubAuthPwd;

	private String defaultContentType = "application/json;charset=UTF-8";

	/**
	 * @param url
	 * @param content
	 *           It must be json format data
	 * @return Results <br>
	 *         code : http status <br>
	 *         responseBody : http response body
	 */
	public JsonObject post(String url, String content) throws TmallAppException
	{
		LOGGER.trace("Post url:" + url);
		HttpEntity<String> request = new HttpEntity<String>(content, createHeaders());
		ResponseEntity<String> entity = null;
		try
		{
			entity = restTemplate.postForEntity(url, request, String.class);
		}
		catch (RestClientException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new TmallAppException(ResponseCode.CONNECTION_REFUSED.getCode(), "Connection refused:unknown URL content:" + url);
		}

		LOGGER.trace("Post data :" + content);
		LOGGER.trace("Response status:" + entity.getStatusCode().value());
		LOGGER.trace("Response body:" + entity.getBody());

		JsonObject responseObject = new JsonObject();
		if (entity.getStatusCode().value() == 200)
		{
			String responseBody = entity.getBody();
			JsonParser parser = new JsonParser();
			responseObject = parser.parse(responseBody).getAsJsonObject();
		}
		else
		{
			responseObject.addProperty("code", entity.getStatusCode().toString());
			responseObject.addProperty("msg", entity.getBody());
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

	@PostConstruct
	private RestTemplate initializationTemplate()
	{
		ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName("UTF-8")).build();
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);

		registryBuilder.register("https", setUpSSL());

		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
		connManager.setDefaultConnectionConfig(connConfig);
		connManager.setDefaultSocketConfig(socketConfig);
		connManager.setMaxTotal(1000);
		connManager.setDefaultMaxPerRoute(1000);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		clientHttpRequestFactory.setConnectTimeout(30000);
		clientHttpRequestFactory.setReadTimeout(30000);
		clientHttpRequestFactory.setConnectionRequestTimeout(30000);

		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate;
	}

	/**
	 * @return HttpHeaders
	 */
	public HttpHeaders createHeaders()
	{
		String encoding = Base64.getEncoder()
				.encodeToString(String.format("%s:%s", datahubAuthUsername, datahubAuthPwd).getBytes());
		String authHeader = "Basic " + encoding;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add("Content-Type", defaultContentType);
		headers.add("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36");
		headers.add("Accept-Encoding", "gzip,deflate");
		headers.add("Accept-Language", "zh-CN");
		headers.add("Connection", "Keep-Alive");
		return headers;
	}

	/**
	 * @param defaultContentType
	 *           the defaultContentType to set
	 */
	public void setDefaultContentType(String defaultContentType)
	{
		this.defaultContentType = defaultContentType;
	}

	private LayeredConnectionSocketFactory setUpSSL()
	{
		LayeredConnectionSocketFactory sslSF = null;
		try
		{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
			sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return sslSF;
	}

	class AnyTrustStrategy implements TrustStrategy
	{

		@Override
		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
			return true;
		}

	}
}
