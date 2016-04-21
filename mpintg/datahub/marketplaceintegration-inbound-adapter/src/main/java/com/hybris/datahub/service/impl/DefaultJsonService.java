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
package com.hybris.datahub.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hybris.datahub.service.JsonService;


/**
 * DefaultJsonService
 */
public class DefaultJsonService implements JsonService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultJsonService.class);

	@Override
	public List<Map<String, String>> parse(final String json)
	{
		final List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		final JsonElement jsonElement = new JsonParser().parse(json);
		if (jsonElement.isJsonArray())
		{
			final Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
			while (iterator.hasNext())
			{
				final JsonElement jsonElementInArray = iterator.next();
				result.add(convertJsonObject(jsonElementInArray.getAsJsonObject()));
			}
		}
		else if (jsonElement.isJsonObject())
		{
			result.add(convertJsonObject(jsonElement.getAsJsonObject()));
		}
		return result;
	}

	private Map<String, String> convertJsonObject(final JsonObject jsonObject)
	{
		LOG.debug("Convert json object: " + jsonObject.toString());

		final Map<String, String> result = new HashMap<String, String>();
		for (final Entry<String, JsonElement> entry : jsonObject.entrySet())
		{
			final JsonElement jsonElement = entry.getValue();
			result.put(entry.getKey(), convertJsonValue(jsonElement));
		}
		return result;
	}

	private String convertJsonValue(final JsonElement jsonElement)
	{
		if (jsonElement.isJsonPrimitive())
		{
			return jsonElement.getAsString();
		}
		else if (jsonElement.isJsonNull())
		{
			return "";
		}
		else
		{
			return jsonElement.toString();
		}
	}
}
