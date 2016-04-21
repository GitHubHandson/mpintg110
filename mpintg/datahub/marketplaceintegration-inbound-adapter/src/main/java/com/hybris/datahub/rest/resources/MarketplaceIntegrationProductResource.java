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
package com.hybris.datahub.rest.resources;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.dto.item.ResultData;
import com.hybris.datahub.facade.MarketplaceIntegrationFacade;
import com.hybris.datahub.util.CommonUtils;
import com.hybris.datahub.vo.response.DataHubResponse;

/**
 * MarketplaceIntegrationResource
 */
@Path("/marketplaceintegration/product/{type}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MarketplaceIntegrationProductResource {
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceIntegrationProductResource.class);
	private MarketplaceIntegrationFacade marketplaceIntegrationFacade;

	/**
	 * Initialize instance
	 */
	public void initialize() {
		LOG.info("MarketplaceIntegrationProductResource initialized: " + new Date().toString());
	}

	/**
	 * @param type
	 * @param json
	 * @return Response
	 */
	@POST
	public DataHubResponse process(@PathParam("type") final String type, final String json) {
		LOG.debug("Received marketplaceintegration request, type: product/" + type + " json: " + json);

		ResultData result = null;
		DataHubResponse datahubResponse = new DataHubResponse();

		result = marketplaceIntegrationFacade.process("product/" + type, json);
		String jsonContent = null;

		if (result != null) {
			jsonContent = CommonUtils.getGsonByBuilder(false).toJson(result);

		}

		datahubResponse.setBody(jsonContent);
		return datahubResponse;
	}

	/**
	 * @param marketplaceIntegrationFacade
	 *            the marketplaceIntegrationFacade to set
	 */
	public void setMarketplaceIntegrationFacade(final MarketplaceIntegrationFacade marketplaceIntegrationFacade) {
		this.marketplaceIntegrationFacade = marketplaceIntegrationFacade;
	}
}
