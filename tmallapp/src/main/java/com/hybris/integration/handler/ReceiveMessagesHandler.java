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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.handler.invocation.Request;
import com.hybris.integration.handler.items.MessageItem;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.model.TmcMessage;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.service.tmall.TmcMessagesService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.request.TmcMessageRequest;
import com.taobao.api.domain.TmcUser;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;


/**
 * Receiving trades,commodity,returned purchases message from TMALL
 *
 */
@Component("tmall.receiveMessagesHandler")
public class ReceiveMessagesHandler implements BaseHandler
{

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);

	@Resource(name = "tmall.tmcMessagesServiceImpl")
	private TmcMessagesService tmcMessagesService;

	@Autowired
	private AccessTokenService accessTokenService;

	private Map<String, MessageItem> items = new HashMap<String, MessageItem>();

	private static Map<String, TmcClient> clientCollections = new HashMap<String, TmcClient>();;

	/**
	 * @param items
	 */
	@Autowired
	public void setMessageItem(List<MessageItem> items)
	{
		for (MessageItem item : items)
		{
			String messageTopic = item.getMessageTopic();
			this.items.put(messageTopic.toString(), item);
		}
	}

	@Value("${message.server.url}")
	private String messageServerUrl;

	private ThreadLocal<String> integrationIdThread = new ThreadLocal<String>();

	private ThreadLocal<TmcMessageRequest> tmcMessageRequestThread = new ThreadLocal<TmcMessageRequest>();

	@Override
	public void execute() throws Exception
	{
		String integrationId = getIntegrationId();
		//Whether the user opened the message service
		TmcUser tmcUser = tmcMessagesService.getUser(integrationId);

		//If there is no open message service or has expired, open the message service for the user
		if (tmcUser == null || !tmcUser.getIsValid())
		{
			boolean permitFlag = tmcMessagesService.userPermit(integrationId);
			if (!permitFlag)
			{
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), "Users [" + integrationId
						+ "] open a message channel failure");
			}
		}

		//Save the information store listener
		TmcMessage tm = new TmcMessage();
		TmcMessageRequest tmcMessageCriteria = getTmcMessageRequest();
		tm.setIntegrationId(integrationId);
		tm.setStatus(tmcMessageCriteria.getStatus());
		tm.setCurrency(tmcMessageCriteria.getCurrency());
		tm.setProductCatalogVersion(tmcMessageCriteria.getProductCatalogVersion());
		tmcMessagesService.saveTmcMessage(tm);

		//According to the status for the user to open the listener or close the listener
		openMessageServer(tmcMessageCriteria.getStatus());
	}

	private void openMessageServer(String status) throws Exception
	{
		String integrationId = getIntegrationId();
		TmcMessageRequest tmcMessageCriteria = getTmcMessageRequest();
		AccessToken token = accessTokenService.get(integrationId);
		String groupName = tmcMessagesService.getTheUserHasAddedGroup(integrationId, token.getTaobaoUserNick());

		//Whether the user has added group
		if (StringUtils.isEmpty(groupName))
		{
			boolean addedFlag = tmcMessagesService.addGroup(integrationId, token.getIntegrationId(), token.getTaobaoUserNick());
			if (!addedFlag)
			{
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), "User [" + token.getTaobaoUserNick()
						+ "] Add User Group is unsuccessful, start the listener failure");
			}
			groupName = token.getIntegrationId();
		}

		TmcClient client = null;

		if (!clientCollections.containsKey(groupName))
		{
			client = new TmcClient(token.getAppkey(), token.getSecret(), groupName);
			setMessageHandler(client);

			clientCollections.put(groupName, client);
		}
		else
		{
			client = clientCollections.get(groupName);
		}

		try
		{
			LOGGER.trace("Current connection status : " + client.isOnline());
			if ("OFF".equals(tmcMessageCriteria.getStatus()) && client.isOnline())
			{
				client.close();
			}
			else if ("ON".equals(tmcMessageCriteria.getStatus()) && !client.isOnline())
			{
				client.connect();
				// Connection fails, try again
				if (!client.isOnline())
				{
					client.connect();
				}
			}
			LOGGER.trace("Current connection status : " + client.isOnline());
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), e.getMessage());
		}
	}

	private void setMessageHandler(TmcClient client)
	{
		String integrationId = getIntegrationId();
		TmcMessageRequest tmcMessageCriteria = getTmcMessageRequest();
		client.setMessageHandler(new MessageHandler()
		{
			String currentInternalIntegrationId = integrationId;

			@Override
			public void onMessage(Message message, MessageStatus status)
			{
				String jsonMsg = CommonUtils.getGsonByBuilder(false).toJson(message);
				LOGGER.trace("From Taobao message,the message body is:" + jsonMsg);

				String topic = message.getTopic();
				//Get the message processing item according topic
				MessageItem item = items.get(topic);
				if (item == null)
				{
					// Topic prefix
					topic = topic.substring(0, topic.lastIndexOf("_"));
					item = items.get(topic.toString());
				}
				if (item == null)
				{
					return;
				}
				Request request = new Request();
				request.setIntegrationId(currentInternalIntegrationId);
				request.setTopic(topic);
				request.setContent(message.getContent());
				request.setTmcMessageCriteria(tmcMessageCriteria);

				boolean response = item.excute(request);

				if (!response)
				{
					LOGGER.error("Listener processing the message fails: [" + topic + "]" + jsonMsg);
				}
			}
		});
	}

	/**
	 * @return the integrationId
	 */
	public String getIntegrationId()
	{
		return integrationIdThread.get();
	}

	/**
	 * @param integrationId
	 *           the integrationId to set
	 */
	public void setIntegrationId(String integrationId)
	{
		this.integrationIdThread.set(integrationId);
	}

	/**
	 * @return the TmcMessageRequest
	 */
	public TmcMessageRequest getTmcMessageRequest()
	{
		return tmcMessageRequestThread.get();
	}

	/**
	 * @param tmcMessageRequest
	 *           the tmcMessageRequest to set
	 */
	public void setTmcMessageRequest(TmcMessageRequest tmcMessageRequest)
	{
		this.tmcMessageRequestThread.set(tmcMessageRequest);
	}


}
