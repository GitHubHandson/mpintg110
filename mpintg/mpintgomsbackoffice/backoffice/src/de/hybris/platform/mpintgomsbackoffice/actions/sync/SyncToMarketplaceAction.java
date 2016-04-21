/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */
package de.hybris.platform.mpintgomsbackoffice.actions.sync;

import de.hybris.platform.marketplaceintegration.enums.RefundStatus;
import de.hybris.platform.marketplaceintegration.model.TmallOrderEntryModel;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeRestTemplateUtil;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;

import com.hybris.backoffice.model.MarketplaceCarrierModel;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;


/**
 * This action confirms shipping by creating shipped entries for the requested consignment entries.
 */
public class SyncToMarketplaceAction implements CockpitAction<ConsignmentModel, ConsignmentModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SyncToMarketplaceAction.class);
	public static final String MARKETPLACE_CONSIGNMENT_CONFIRM_PATH = "marketplace.consignment.confirm.path";
	public static final String MARKETPLACE_CONSIGNMENT_CONFIRM_STATUS = "WAIT_SELLER_SEND_GOODS";
	public static final String MARKETPLACE_CONSIGNMENT_PART_STATUS = "SELLER_CONSIGNED_PART";

	@Value("${datahub.trade.request.url}")
	private String requestUrl;

	@Resource
	private ModelService modelService;

	@Autowired
	private MarketplaceintegrationbackofficeRestTemplateUtil marketplaceHttpUtil;

	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	@Override
	public boolean canPerform(final ActionContext<ConsignmentModel> actionContext)
	{
		final Object data = actionContext.getData();

		ConsignmentModel consignment = null;
		if (data instanceof ConsignmentModel)
		{
			consignment = (ConsignmentModel) data;
		}

		// A consignment is not shippable when it is a pickup order or when there is no quantity pending
		if (Objects.isNull(consignment)
				|| Objects.isNull(consignment.getConsignmentEntries())
				|| consignment.getConsignmentEntries().stream().filter(entry -> Objects.nonNull(entry.getQuantityPending()))
						.mapToLong(ConsignmentEntryModel::getQuantityPending).sum() == 0)
		{
			return false;
		}

		if (!checkflag(consignment))
		{
			return false;
		}

		return true;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<ConsignmentModel> actionContext)
	{
		return null;
	}

	@Override
	public boolean needsConfirmation(final ActionContext<ConsignmentModel> actionContext)
	{
		return false;
	}

	@Override
	public ActionResult<ConsignmentModel> perform(final ActionContext<ConsignmentModel> actionContext)
	{

		String result = StringUtils.EMPTY;
		final ConsignmentModel consignment = actionContext.getData();
		final TmallOrderModel order = (TmallOrderModel) consignment.getOrder();
		if (order == null)
		{
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.integrity.error"),
					NotificationEvent.Type.FAILURE, "");
			LOG.warn("Can not find corresponding order for this consignment: " + consignment.getCode());
			return actionResult;
		}

		final Set<ConsignmentEntryModel> consignmentEntries = consignment.getConsignmentEntries();
		final Set<ConsignmentModel> consignments = order.getConsignments();
		String subTid = "";
		String urlStr = "";
		String marketplaceLogId = "";

		getModelService().save(consignment);

		if (StringUtils.isBlank(order.getMarketplaceStore().getIntegrationId()))
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.request.url.error", new Object[]
			{ order.getMarketplaceStore().getName() }), NotificationEvent.Type.WARNING, "");
			LOG.warn("authorization is expired!");
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			return actionResult;
		}
		else if (StringUtils.isBlank(consignment.getMarketplaceTrackingID()))
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.trackingid.empty"),
					NotificationEvent.Type.WARNING, "");
			LOG.warn("Confirm failed due to trackingid is empty for model " + consignment);
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			return actionResult;
		}
		else if (null == consignment.getStandardCarrier())
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.carrier.null"), NotificationEvent.Type.WARNING,
					"");
			LOG.warn("Confirm failed due to carrier is null for model " + consignment);
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			return actionResult;
		}

		if (!MARKETPLACE_CONSIGNMENT_CONFIRM_STATUS.equals(order.getTmallOrderStatus().getCode()))
		{
			if (!MARKETPLACE_CONSIGNMENT_PART_STATUS.equals(order.getTmallOrderStatus().getCode()))
			{
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.status.fail"),
						NotificationEvent.Type.WARNING, "");
				LOG.warn("Confirm failed due to order status is not correct!");
				result = ActionResult.ERROR;
				final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
				actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
				return actionResult;
			}
		}

		for (final ConsignmentEntryModel consignmententry : consignmentEntries)
		{
			final TmallOrderEntryModel tmallOrderEntry = (TmallOrderEntryModel) consignmententry.getOrderEntry();
			subTid = subTid + tmallOrderEntry.getOid() + ",";
			if (RefundStatus.WAIT_SELLER_AGREE.equals(tmallOrderEntry.getRefundStatus())
					|| RefundStatus.WAIT_BUYER_RETURN_GOODS.equals(tmallOrderEntry.getRefundStatus())
					|| RefundStatus.WAIT_SELLER_CONFIRM_GOODS.equals(tmallOrderEntry.getRefundStatus())
					|| RefundStatus.SUCCESS.equals(tmallOrderEntry.getRefundStatus()))
			{
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.status.fail"),
						NotificationEvent.Type.WARNING, "");
				LOG.warn("Confirm failed due to refund status is not correct!");
				result = ActionResult.ERROR;
				final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
				actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
				return actionResult;
			}
		}

		try
		{
			marketplaceLogId = marketplaceHttpUtil.getUUID();

			logUtil.addMarketplaceLog("PENDING", consignment.getCode(), Labels.getLabel("mpintgomsbackoffice.consignment.action"),
					consignment.getItemtype(), order.getMarketplaceStore().getMarketplace(), consignment, marketplaceLogId);

			urlStr = requestUrl + Config.getParameter(MARKETPLACE_CONSIGNMENT_CONFIRM_PATH);
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("integrationId", order.getMarketplaceStore().getIntegrationId());
			//Whether splited
			if (consignments.size() > 1)
			{
				final String isSplit = "1";
				jsonObj.put("isSplit", isSplit);
				jsonObj.put("subTid", subTid);
			}
			jsonObj.put("tmallOrderId", order.getTmallOrderId());
			jsonObj.put("trackingId", consignment.getMarketplaceTrackingID());
			//jsonObj.put("companyCode", consignment.getMarketplaceCarrier().getCode());

			final String standardCarrierPK = consignment.getStandardCarrier().getPk().getLongValueAsString();
			final String marketplacePK = order.getMarketplaceStore().getMarketplace().getPk().getLongValueAsString();

			final String queryString = "SELECT {" + MarketplaceCarrierModel.CODE + "} FROM {" + MarketplaceCarrierModel._TYPECODE
					+ "} " + "WHERE {" + MarketplaceCarrierModel.MARKETPLACE + "}=?marketplacePK" + " and {"
					+ MarketplaceCarrierModel.STANDARDCARRIER + "}=?standardCarrierPK";
			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
			query.addQueryParameter("marketplacePK", marketplacePK);
			query.addQueryParameter("standardCarrierPK", standardCarrierPK);
			final SearchResult<String> companyCode = flexibleSearchService.<String> search(query);
			jsonObj.put("companyCode", companyCode);

			jsonObj.put("marketplaceLogId", marketplaceLogId);
			JSONObject results = new JSONObject();
			results = marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());

			final String msg = results.toJSONString();
			final String responseCode = results.get("code").toString();

			if ("401".equals(responseCode))
			{
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.fail"),
						NotificationEvent.Type.FAILURE, "");
				LOG.warn("Authentication was failed, please re-authenticate again!");
				result = ActionResult.ERROR;
			}
			else if (!("0".equals(responseCode)))
			{
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.tmallapp.known.issues", new Object[]
				{ msg }), NotificationEvent.Type.FAILURE, "");
				LOG.warn("A known issue occurs in tmall, error details :" + msg);
				result = ActionResult.ERROR;
			}

		}
		catch (final HttpClientErrorException httpError)
		{
			if (httpError.getStatusCode().is4xxClientError())
			{
				LOG.error("=========================================================================");
				LOG.error("Order consignment upload request post to Datahub failed!");
				LOG.error("Consignment Code: " + consignment.getCode());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + urlStr);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("Requested Datahub service URL is not correct!");
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("=========================================================================");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.request.post.error"),
						NotificationEvent.Type.FAILURE, "");
			}
			if (httpError.getStatusCode().is5xxServerError())
			{
				LOG.error("=========================================================================");
				LOG.error("Order consignment upload request post to Datahub failed!");
				LOG.error("Consignment Code: " + consignment.getCode());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + urlStr);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("Requested Json Ojbect is not correct!");
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("=========================================================================");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.server.process.error"),
						NotificationEvent.Type.FAILURE, "");
			}
			LOG.error(httpError.toString());
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			return actionResult;
		}
		catch (final ResourceAccessException raError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order consignment upload request post to Datahub failed!");
			LOG.error("Consignment Code: " + consignment.getCode());
			LOG.error("Request path: " + urlStr);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Marketplace order consignment upload request server access failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + raError.getMessage());
			LOG.error("=========================================================================");
			//LOG.error(raError.toString());
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.server.access.error"),
					NotificationEvent.Type.FAILURE, "");
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			return actionResult;
		}
		catch (final HttpServerErrorException serverError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order consignment upload request post to Datahub failed!");
			LOG.error("Consignment Code: " + consignment.getCode());
			LOG.error("Request path: " + urlStr);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Marketplace order consignment upload request server process failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + serverError.getMessage());
			LOG.error("=========================================================================");
			//LOG.error(raError.toString());
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.server.process.error"),
					NotificationEvent.Type.FAILURE, "");
			result = ActionResult.ERROR;
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			return actionResult;
		}

		catch (final Exception e)
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.upload.fail"), NotificationEvent.Type.FAILURE,
					"");
			result = ActionResult.ERROR;
			LOG.error("=========================================================================");
			LOG.error("Order consignment failed!");
			LOG.error("Marketplacestore Code: " + consignment.getCode());
			LOG.error("Request path: " + requestUrl);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + e.getMessage());
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("=========================================================================");
			final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
			return actionResult;
		}
		LOG.info("=========================================================================");
		LOG.info("Order consignment upload request post to Datahub suceessfully!");
		LOG.info("Consignment Code: " + consignment.getCode());
		LOG.info("Request path: " + urlStr);
		LOG.info("=========================================================================");
		NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.upload.success"), NotificationEvent.Type.SUCCESS,
				"");
		result = ActionResult.SUCCESS;
		setFlag(consignment);
		final ActionResult<ConsignmentModel> actionResult = new ActionResult<ConsignmentModel>(result);
		actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
		return actionResult;
	}

	private boolean checkflag(final ConsignmentModel consignment)
	{
		boolean flag = false;
		final Set<ConsignmentEntryModel> consignmentEntries = consignment.getConsignmentEntries();
		for (final ConsignmentEntryModel consignmentEntry : consignmentEntries)
		{
			final TmallOrderEntryModel orderEntry = (TmallOrderEntryModel) consignmentEntry.getOrderEntry();
			if (!orderEntry.getWaitMarketPlaceResponse())
			{
				flag = true;
			}
		}
		return flag;
	}

	private void setFlag(final ConsignmentModel consignment)
	{

		final Set<ConsignmentEntryModel> consignmentEntries = consignment.getConsignmentEntries();
		for (final ConsignmentEntryModel consignmentEntry : consignmentEntries)
		{
			final TmallOrderEntryModel orderEntry = (TmallOrderEntryModel) consignmentEntry.getOrderEntry();
			orderEntry.setWaitMarketPlaceResponse(Boolean.TRUE);
			modelService.save(orderEntry);
			modelService.save(consignmentEntry);
		}
		modelService.save(consignment);
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
