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
package com.hybris.integration.service.tmall;

import java.util.List;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.TmcMessage;
import com.taobao.api.ApiException;
import com.taobao.api.domain.TmcGroup;
import com.taobao.api.domain.TmcUser;


/**
 * TmcMessagesService
 */
public interface TmcMessagesService
{

	/**
	 * @param integrationId
	 * @return TmcUser
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public TmcUser getUser(String integrationId) throws TmallAppException, ApiException;

	/**
	 * @param integrationId
	 * @return Permit status
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public boolean userPermit(String integrationId) throws TmallAppException, ApiException;


	/**
	 * To query the name of the grouping, a plurality of packets are separated with commas, do not pass the information
	 * on behalf of a query for all groups, but the group does not return the following user information. If the
	 * application does not set the packet is returned empty.
	 *
	 * @param integrationId
	 * @param groupNames
	 * @return List<TmcGroup>
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public List<TmcGroup> getGroups(String integrationId, String groupNames) throws TmallAppException, ApiException;

	/**
	 * @param integrationId
	 * @param groupName
	 * @param userNicks
	 * @return boolean
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public boolean deleteGroup(String integrationId, String groupName, String userNicks) throws TmallAppException, ApiException;


	/**
	 * @param integrationId
	 * @param groupName
	 * @param userNicks
	 * @return Is success
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public boolean addGroup(String integrationId, String groupName, String userNicks) throws TmallAppException, ApiException;

	/**
	 * @param integrationId
	 * @param userNick
	 * @return Group name
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public String getTheUserHasAddedGroup(String integrationId, String userNick) throws TmallAppException, ApiException;

	/**
	 * @param tm
	 * @throws TmallAppException
	 */
	public void saveTmcMessage(TmcMessage tm) throws TmallAppException;

	/**
	 * @param integrationId
	 * @return TmcMessage
	 * @throws TmallAppException
	 */
	public TmcMessage getTmcMessage(String integrationId) throws TmallAppException;

}
