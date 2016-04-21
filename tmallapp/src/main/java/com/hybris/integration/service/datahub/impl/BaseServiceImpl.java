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
package com.hybris.integration.service.datahub.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hybris.integration.util.RestTemplateUtil;


/**
 * BaseServiceImpl
 */
public abstract class BaseServiceImpl
{

	@Autowired
	protected RestTemplateUtil restTemplateUtil;

	@Value("${datahub.server.url}")
	protected String datahubServerUrl;

	@Value("${tmall.store.custom.suffix}")
	protected String tmallStoreCustomSuffix;

}
