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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hybris.integration.service.tmall.APIService;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;


/**
 * APIServiceImpl
 */
@Service("tmall.APIServiceImpl")
public class APIServiceImpl implements APIService
{

	private static Map<String, String> fields = new HashMap<String, String>();

	public String getTmallDomainFieldByClassType(final Class<?> classType, final String prefix)
	{
		if (fields.containsKey(classType.getTypeName()))
		{
			return fields.get(classType.getTypeName());
		}
		else
		{
			return startSearch(classType, prefix);
		}


	}

	public String getSoldGetAPIFields()
	{
		return "tid";
	}

	private String startSearch(final Class<?> classType, String prefix)
	{
		final Field[] objectFields = classType.getDeclaredFields();

		final StringBuffer sb = new StringBuffer();
		String fieldsSeparatedByCommas = null;

		prefix = prefix == null ? "" : prefix + ".";

		for (final Field item : objectFields)
		{
			if ("serialVersionUID".equals(item.getName()))
			{
				continue;
			}
			final ApiListField apiListField = item.getAnnotation(ApiListField.class);
			if (apiListField != null)
			{
				continue;
			}
			final ApiField annotation = item.getAnnotation(ApiField.class);

			sb.append(prefix + annotation.value() + ",");
		}
		if (StringUtils.isNotEmpty(sb))
		{
			fieldsSeparatedByCommas = sb.toString().substring(0, sb.toString().length() - 1);

			fields.put(classType.getTypeName(), fieldsSeparatedByCommas);
		}
		return fieldsSeparatedByCommas;
	}
}
