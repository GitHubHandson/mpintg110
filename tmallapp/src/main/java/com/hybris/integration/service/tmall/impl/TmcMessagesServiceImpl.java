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
package com.hybris.integration.service.tmall.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.model.TmcMessage;
import com.hybris.integration.service.tmall.TmcMessagesService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.Constants;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.util.security.AttributeEncryptionStrategy;
import com.taobao.api.ApiException;
import com.taobao.api.domain.TmcGroup;
import com.taobao.api.domain.TmcUser;
import com.taobao.api.request.TmcGroupAddRequest;
import com.taobao.api.request.TmcGroupDeleteRequest;
import com.taobao.api.request.TmcGroupsGetRequest;
import com.taobao.api.request.TmcUserGetRequest;
import com.taobao.api.request.TmcUserPermitRequest;
import com.taobao.api.response.TmcGroupAddResponse;
import com.taobao.api.response.TmcGroupDeleteResponse;
import com.taobao.api.response.TmcGroupsGetResponse;
import com.taobao.api.response.TmcUserGetResponse;
import com.taobao.api.response.TmcUserPermitResponse;


/**
 * TmcMessagesServiceImpl
 */
@Service("tmall.tmcMessagesServiceImpl")
public class TmcMessagesServiceImpl extends BaseServiceImpl implements TmcMessagesService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TmcMessagesServiceImpl.class);
	@Value("${persistence.url}")
	private String persistenceUrl;

	@Autowired
	private AttributeEncryptionStrategy encryptionTools;


	@Override
	public TmcUser getUser(final String integrationId) throws TmallAppException, ApiException
	{
		final AccessToken token = get(integrationId);
		final TmcUserGetRequest req = new TmcUserGetRequest();
		req.setFields("user_nick,topics,user_id,is_valid,created,modified");
		req.setNick(token.getTaobaoUserNick());

		TmcUser tmcUser = null;

		TmcUserGetResponse response = getClient(integrationId).execute(req);

		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			tmcUser = response.getTmcUser();
		}
		else
		{
			response = getClient(integrationId).execute(req);
			if (response != null && StringUtils.isEmpty(response.getErrorCode()))
			{
				tmcUser = response.getTmcUser();
			}
			else
			{
				LOGGER.error("Tmall API taobao.tmc.user failed request:" + response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return tmcUser;

	}

	@Override
	public boolean userPermit(final String integrationId) throws TmallAppException, ApiException
	{
		final TmcUserPermitRequest req = new TmcUserPermitRequest();
		final TmcUserPermitResponse response = getClient(integrationId).execute(req, getToken(integrationId));
		boolean flag = false;
		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			flag = response.getIsSuccess();
		}
		else
		{
			LOGGER.error("Tmall API tmc.user.permit failed request:" + response.getBody());
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
		}
		return flag;
	}

	@Override
	public List<TmcGroup> getGroups(final String integrationId, final String groupNames) throws ApiException, TmallAppException
	{
		final TmcGroupsGetRequest req = new TmcGroupsGetRequest();
		req.setGroupNames(groupNames);
		req.setPageNo(1L);
		req.setPageSize(40L);
		final TmcGroupsGetResponse rsp = getClient(integrationId).execute(req);
		List<TmcGroup> groups = null;

		if (rsp != null && StringUtils.isEmpty(rsp.getErrorCode()))
		{
			groups = rsp.getGroups();
		}
		else
		{
			LOGGER.error("Tmall API tmc.groups.get failed request:" + rsp.getBody());
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), rsp.getSubMsg());
		}
		return groups;
	}

	@Override
	public boolean deleteGroup(final String integrationId, final String groupName, final String userNicks)
			throws TmallAppException, ApiException
	{
		final TmcGroupDeleteRequest req = new TmcGroupDeleteRequest();
		req.setGroupName(groupName);
		req.setNicks(userNicks);
		req.setUserPlatform("tbUIC");
		final TmcGroupDeleteResponse rsp = getClient(integrationId).execute(req);

		return rsp.isSuccess();
	}

	@Override
	public boolean addGroup(final String integrationId, final String groupName, final String userNicks) throws TmallAppException,
			ApiException
	{
		final TmcGroupAddRequest req = new TmcGroupAddRequest();
		req.setGroupName(groupName);
		req.setNicks(userNicks);
		req.setUserPlatform("tbUIC");
		final TmcGroupAddResponse rsp = getClient(integrationId).execute(req);

		if (rsp != null && StringUtils.isEmpty(rsp.getErrorCode()))
		{
			return true;
		}
		else
		{
			LOGGER.error("Tmall API tmc.group.add failed request:" + rsp.getBody());
			return false;
		}
	}

	@Override
	public String getTheUserHasAddedGroup(final String integrationId, final String userNick) throws TmallAppException,
			ApiException
	{
		String groupName = "";

		//Query all groups
		final List<TmcGroup> groups = getGroups(integrationId, null);

		if (CollectionUtils.isNotEmpty(groups))
		{
			String groupNames = "";
			for (final TmcGroup groupItem : groups)
			{
				if (!"default".equals(groupItem.getName()))
				{
					groupNames = groupNames + groupItem.getName() + ",";
				}

			}

			if (StringUtils.isEmpty(groupNames))
			{
				return groupNames;
			}

			//Query the user for all groups
			final List<TmcGroup> groupUsers = getGroups(integrationId, groupNames.substring(0, groupNames.length() - 1));

			if (CollectionUtils.isNotEmpty(groupUsers))
			{
				for (final TmcGroup groupUser : groupUsers)
				{
					if (CollectionUtils.isNotEmpty(groupUser.getUsers()))
					{
						for (final String groupUserNick : groupUser.getUsers())
						{
							if (groupUserNick.equals(userNick))
							{
								groupName = groupUser.getName();
							}
						}
					}
				}
			}
		}
		return groupName;
	}

	@Override
	public void saveTmcMessage(final TmcMessage tm) throws TmallAppException
	{

		final String persistence = persistenceUrl + Constants.MESSAGE_FILE_PREFIX.getValue() + tm.getIntegrationId();

		String content = CommonUtils.getGsonByBuilder().toJson(tm);

		try
		{
			content = encryptionTools.encrypt(content);
		}
		catch (final Exception e)
		{
			throw new TmallAppException(ResponseCode.AES_ENCRYPTION_ERROR.getCode(), "AES encryption failure:" + e.getMessage());
		}

		CommonUtils.writeFile(persistence, content);
	}

	@Override
	public TmcMessage getTmcMessage(final String integrationId) throws TmallAppException
	{
		String tmcJson = CommonUtils.readFile(persistenceUrl + "TMCMESSAGESINFORMATION" + integrationId + ".json");
		TmcMessage tmc = null;

		if (StringUtils.isNoneEmpty(tmcJson))
		{

			try
			{
				tmcJson = encryptionTools.decrypt(tmcJson);
			}
			catch (final Exception e)
			{
				throw new TmallAppException(ResponseCode.AES_DECRYPTION_ERROR.getCode(), "AES decryption failure:" + e.getMessage());
			}

			tmc = CommonUtils.getGsonByBuilder().fromJson(tmcJson, TmcMessage.class);
		}
		else
		{
			throw new TmallAppException(ResponseCode.OBJECT_NOT_FOUND.getCode(), "The [" + integrationId + "] does not exist");
		}

		return tmc;
	}

}
