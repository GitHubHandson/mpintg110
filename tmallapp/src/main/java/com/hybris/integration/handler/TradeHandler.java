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
package com.hybris.integration.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hybris.integration.service.tmall.APIService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.vo.request.TradeDownloadRequest;


/**
 * TMALL synchronized within three months of the order. Since the three-month orders than larger, therefore the
 * three-month cut into orders get by day
 */
@Component
public class TradeHandler implements BaseHandler
{

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private ApplicationContext ctx;

	@Autowired
	private APIService apiService;

	private ThreadLocal<TradeDownloadRequest> requestThread = new ThreadLocal<TradeDownloadRequest>();

	public void execute() throws Exception
	{
		TradeDownloadRequest request = getTradeDownloadRequest();
		final List<Map<String, Date>> dateItems = CommonUtils.findDates(CommonUtils.stringToDate(request.getStartCreated()),
				CommonUtils.stringToDate(request.getEndCreated()));

		final JobParametersBuilder paramBuilder = new JobParametersBuilder();

		for (final String status : request.getStatus().split(","))
		{
			paramBuilder.addString("fields", apiService.getSoldGetAPIFields());
			paramBuilder.addString("integrationId", request.getIntegrationId());
			paramBuilder.addLong("batchSize", request.getBatchSize());
			paramBuilder.addString("status", status);
			paramBuilder.addString("currency", request.getCurrency());
			paramBuilder.addString("productCatalogVersion", request.getProductCatalogVersion());

			int i = 1;
			for (final Map<String, Date> item : dateItems)
			{
				paramBuilder.addDate("startCreated", item.get("start"));
				paramBuilder.addDate("endCreated", item.get("end"));
				paramBuilder.addLong("timestamp", System.currentTimeMillis());

				jobLauncher.run((Job) ctx.getBean("tamllTradeJob"), paramBuilder.toJobParameters());
				LOGGER.info("Download Order thread has been started, the current Article " + i + " threads");
				i++;
			}
		}
	}

	/**
	 * @return the request
	 */
	public TradeDownloadRequest getTradeDownloadRequest()
	{
		return requestThread.get();
	}

	/**
	 * @param request
	 *           the request to set
	 */
	public void setRequest(TradeDownloadRequest request)
	{
		this.requestThread.set(request);
	}

}
