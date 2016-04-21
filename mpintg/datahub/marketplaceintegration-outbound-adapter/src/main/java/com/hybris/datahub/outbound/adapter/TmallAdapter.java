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
package com.hybris.datahub.outbound.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hybris.datahub.adapter.AdapterService;
import com.hybris.datahub.api.publication.PublicationException;
import com.hybris.datahub.domain.TargetItemMetadata;
import com.hybris.datahub.dto.item.ErrorData;
import com.hybris.datahub.dto.publication.PublicationResult;
import com.hybris.datahub.model.TargetItem;
import com.hybris.datahub.outbound.utils.CommonUtils;
import com.hybris.datahub.outbound.utils.RestTemplateUtil;
import com.hybris.datahub.paging.DataHubIdBasedPageable;
import com.hybris.datahub.paging.DataHubPage;
import com.hybris.datahub.paging.DefaultDataHubIdBasedPageRequest;
import com.hybris.datahub.runtime.domain.PublicationActionStatusType;
import com.hybris.datahub.runtime.domain.TargetSystemPublication;
import com.hybris.datahub.service.PublicationActionService;


public class TmallAdapter implements AdapterService
{
	private static final Logger LOG = LoggerFactory.getLogger(TmallAdapter.class);
	public static final String EXPORT_ITEM_CODE = "exportItemCode";
	public static final String EXPORT_LOG_PATH = "marketplaceintegration/log/marketplace-log";
	private PublicationActionService publicationActionService;
	private PlatformTransactionManager transactionManager;
	private String targetSystemType;
	private int pageSize = 1000;

	@Value("${datahub.server.url}")
	protected String datahubServerUrl;

	@Autowired
	private RestTemplateUtil httpUtil;

	@Override
	public String getTargetSystemType()
	{
		return targetSystemType;
	}


	@Override
	public void publish(TargetSystemPublication targetSystemPublication, String serverURL) throws PublicationException
	{
		Preconditions.checkArgument(targetSystemPublication != null
				&& targetSystemPublication.getTargetSystem().getTargetSystemType().equals(getTargetSystemType()));
		Preconditions.checkArgument(StringUtils.isNotEmpty(serverURL), "Server " + targetSystemType + " url must be provided.");


		final List<ErrorData> errors = new ArrayList<>();
		try
		{
			for (final TargetItemMetadata targetItemMetadata : targetSystemPublication.getTargetSystem().getTargetItemMetadata())
			{
				errors.addAll(processItems(targetSystemPublication, targetItemMetadata));
			}
			completePublication(targetSystemPublication, errors);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			targetSystemPublication.setStatus(PublicationActionStatusType.FAILURE);
			throw new PublicationException("Failed to send publication " + targetSystemPublication.getPublicationId() + " data to "
					+ targetSystemPublication.getTargetSystem().getTargetSystemName(), e);
		}
	}


	private void completePublication(final TargetSystemPublication targetSystemPublication, final List<ErrorData> errors)
	{
		final PublicationResult publicationResult = new PublicationResult();
		publicationResult.setExportErrorDatas(errors);
		publicationActionService.completeTargetSystemPublication(targetSystemPublication.getPublicationId(), publicationResult);
	}

	/**
	 * Gets the ItemPage.
	 *
	 * @param targetItemMetadata
	 *           The targetItemMetadata.
	 * @param targetSystemPublication
	 *           The targetSystemPublication.
	 * @param pageable
	 *           The datahub pageable.
	 * @return The datahubPage.
	 */
	@SuppressWarnings("deprecation")
	public DataHubPage<TargetItem> getItemPage(final TargetItemMetadata targetItemMetadata,
			final TargetSystemPublication targetSystemPublication, final DataHubIdBasedPageable pageable)
	{
		return publicationActionService.findByPublication(targetSystemPublication.getPublicationId(),
				targetItemMetadata.getItemType(), pageable);
	}


	private ErrorData buildPublicationError(final TargetItem targetItem, final Exception e)
	{
		final ErrorData errorData = new ErrorData();
		errorData.setCanonicalItemId(targetItem.getCanonicalItem().getId());
		errorData.setCode(e.getMessage());
		errorData.setMessage(e.getMessage());
		return errorData;
	}

	private ErrorData buildPublicationError(final TargetItem targetItem, final JsonObject results)
	{
		final ErrorData errorData = new ErrorData();
		errorData.setCanonicalItemId(targetItem.getCanonicalItem().getId());

		final String responseCode = results.get("code").getAsString();
		final String msg = results.get("msg").getAsString();
		errorData.setCode(responseCode);
		errorData.setMessage(msg);
		return errorData;
	}

	private List<ErrorData> doPublish(final TargetItem targetItem, final TargetSystemPublication targetSystemPublication)
	{
		String content = null;
		final List<ErrorData> errors = Lists.newArrayList();
		String url = targetSystemPublication.getTargetSystem().getExportURL() + "/" + targetItem.getExportCode()
				+ "?marketplaceLogUUID=";
		//		String url = targetSystemPublication.getTargetSystem().getExportURL();
		try
		{
			LOG.info("begin to process target item:" + targetItem.getExportCode());
			content = buildJsonFromItem(targetItem);
			final JsonObject results = httpUtil.post(url + targetItem.getField("marketplaceLogId"), content);
			final String responseCode = results.get("code").getAsString();
			final String responseMsg = results.get("msg").getAsString();

			/*
			 * if ("401".equals(responseCode)) { errors.add(buildPublicationError(targetItem, results));
			 * LOG.warn("Authentication was failed, please re-authenticate again!"); return errors; } else if
			 * (!("0".equals(responseCode))) { errors.add(buildPublicationError(targetItem, results));
			 * LOG.warn("Authentication was failed, please re-authenticate again!"); }
			 */
			LOG.info("Response Code: " + responseCode);
			LOG.info("Response Msg: " + responseMsg);
			LOG.info("post json value {}", content);
		}
		catch (final Exception e)
		{
			errors.add(buildPublicationError(targetItem, e));
			LOG.error("process item error " + content, e);
			exprotLogToHybris(true, targetItem.getField("marketplaceLogId").toString(), e.toString(), targetSystemPublication
					.getTargetSystem().getExportURL());
		}
		return errors;
	}


	private String buildJsonFromItem(final TargetItem targetItem)
	{

		Gson gson = CommonUtils.getGsonByBuilder(false);

		String exportCode = targetItem.getExportCode();
		Map<String, Object> fieldExportMap = new HashMap<>();
		for (Entry<String, Object> field : targetItem.getFields().entrySet())
		{
			String fieldKey = field.getKey();
			if (StringUtils.isNotBlank(fieldKey))
			{
				String exportAttr = targetItem.getExportCodeAttributeByName(fieldKey).get();
				//change key to export name
				fieldExportMap.put(exportAttr, field.getValue());
			}
		}
		fieldExportMap.put(EXPORT_ITEM_CODE, exportCode);
		return gson.toJson(fieldExportMap);
	}

	private List<ErrorData> processItems(final TargetSystemPublication targetSystemPublication,
			final TargetItemMetadata targetItemMetadata)
	{
		final List<ErrorData> errors = Lists.newArrayList();
		final TransactionTemplate template = new TransactionTemplate(transactionManager);
		Pair<Integer, Long> elementsAndLastId = new ImmutablePair<>(0, 0L);
		do
		{
			final Long lastProcessedId = elementsAndLastId.getRight();
			elementsAndLastId = template.execute(status -> {
				final List<? extends TargetItem> items = getItems(targetItemMetadata, targetSystemPublication,
						makePageable(lastProcessedId));
				final Pair<Integer, Long> pageElementsAndLastId;
				if (!CollectionUtils.isEmpty(items))
				{
					for (final TargetItem targetItem : items)
					{
						errors.addAll(doPublish(targetItem, targetSystemPublication));
					}
					pageElementsAndLastId = new ImmutablePair<>(items.size(), getLastProcessedId(lastProcessedId, items));
				}
				else
				{
					pageElementsAndLastId = new ImmutablePair<>(0, 0L);
				}
				return pageElementsAndLastId;
			});
		}
		while (elementsAndLastId.getRight() > 0);
		return errors;
	}

	private Long getLastProcessedId(final Long lastProcessedId, final List<? extends TargetItem> items)
	{
		return !CollectionUtils.isEmpty(items) ? items.get(items.size() - 1).getId() : lastProcessedId;
	}

	private List<? extends TargetItem> getItems(final TargetItemMetadata targetItemMetadata,
			final TargetSystemPublication targetSystemPublication, final DataHubIdBasedPageable pageable)
	{
		return publicationActionService.findTargetItemsByPublication(targetSystemPublication.getPublicationId(),
				targetItemMetadata.getItemType(), pageable);
	}

	private DefaultDataHubIdBasedPageRequest makePageable(final Long pageNumber)
	{
		return new DefaultDataHubIdBasedPageRequest(pageSize, pageNumber);
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @param pageSize
	 *           the pageSize to set
	 */
	@Required
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * @param targetSystemType
	 *           the targetSystemType to set
	 */
	@Required
	public void setTargetSystemType(String targetSystemType)
	{
		this.targetSystemType = targetSystemType;
	}

	@Required
	public void setTransactionManager(PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	/**
	 * Needed by datahub.
	 *
	 * @param publicationActionService
	 *           Publication Service needed to complete the target publication.
	 */
	public PublicationActionService getPublicationActionService()
	{
		return publicationActionService;
	}

	/**
	 * @param publicationActionService
	 *           the publicationActionService to set
	 */
	@Required
	public void setPublicationActionService(PublicationActionService publicationActionService)
	{
		this.publicationActionService = publicationActionService;
	}


	/**
	 * @param httpUtil
	 *           the httpUtil to set
	 */
	public void setHttpUtil(RestTemplateUtil httpUtil)
	{
		this.httpUtil = httpUtil;
	}

	private void exprotLogToHybris(boolean isError, String actionUUID, String message, String targetUrl)
	{
		String url = datahubServerUrl + "/" + EXPORT_LOG_PATH;
		final JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("actionuuid", actionUUID);
		jsonObj.addProperty("actionstatus", (isError ? "ERROR" : "SUCCESS"));
		jsonObj.addProperty("message", message);
		jsonObj.addProperty("messagesource", "DH");

		final JsonObject results = httpUtil.post(url, jsonObj.toString());
		final String responseCode = results.get("code").getAsString();
	}

}
