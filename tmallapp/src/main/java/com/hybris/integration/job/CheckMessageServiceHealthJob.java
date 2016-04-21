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
package com.hybris.integration.job;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.handler.ReceiveMessagesHandler;
import com.hybris.integration.model.TmcMessage;
import com.hybris.integration.service.tmall.TmcMessagesService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.Constants;
import com.hybris.integration.vo.request.TmcMessageRequest;


/**
 * Check Message Service Health Job
 */
@Component
public class CheckMessageServiceHealthJob
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckMessageServiceHealthJob.class);

	@Value("${persistence.url}")
	private String persistenceUrl;

	@Autowired
	private ReceiveMessagesHandler messagesHandler;

	@Autowired
	private TmcMessagesService messagesService;

	/**
	 * Executed once when the container starts
	 */
	@Scheduled(fixedRate = 1000 * 3600)
	public void executeCheck()
	{
		LOGGER.info("Timing task starts running...");
		String messageFilePrefix = Constants.MESSAGE_FILE_PREFIX.getValue();
		final Map<String, String> fileContents = CommonUtils.readFileContentOfTheFloder(persistenceUrl, messageFilePrefix);

		if (!fileContents.isEmpty())
		{
			for (final Entry<String, String> item : fileContents.entrySet())
			{

				final String key = item.getKey();
				final String integrationId = key.substring(messageFilePrefix.length(), key.indexOf("."));

				try
				{
					final TmcMessage tmcMessage = messagesService.getTmcMessage(integrationId);

					if ("ON".equals(tmcMessage.getStatus()))
					{

						messagesHandler.setIntegrationId(integrationId);
						final TmcMessageRequest request = new TmcMessageRequest();
						request.setCurrency(tmcMessage.getCurrency());
						request.setProductCatalogVersion(tmcMessage.getProductCatalogVersion());
						request.setStatus(tmcMessage.getStatus());
						messagesHandler.setTmcMessageRequest(request);
						messagesHandler.execute();
						LOGGER.info("Opened a connection[" + tmcMessage.getIntegrationId() + "~" + tmcMessage.getStatus() + "]");

					}

				}
				catch (final TmallAppException e)
				{
					LOGGER.error(e.getMessage());
				}
				catch (final Exception e)
				{
					LOGGER.error(e.getMessage());
				}
			}
		}
		LOGGER.info("End timing task runs...");
	}
}
