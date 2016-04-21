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
package de.hybris.platform.mpintgomsbackoffice.actions.reject;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeRestTemplateUtil;
import de.hybris.platform.mpintgordermanagement.enums.RefundAction;
import de.hybris.platform.mpintgordermanagement.enums.RefundType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;

import com.hybris.backoffice.model.TmallRefundRequestModel;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;


/**
 * This action confirms shipping by creating shipped entries for the requested consignment entries.
 */
public class ConfirmRejectAction implements CockpitAction<TmallRefundRequestModel, TmallRefundRequestModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfirmRejectAction.class);
	public static final String MARKETPLACE_REFUNDREQUEST_REJECT_PATH = "marketplace.refundrequest.reject.path";

	@Value("${datahub.refund.request.url}")
	private String requestUrl;

	@Resource
	private ModelService modelService;

	@Autowired
	private MarketplaceintegrationbackofficeRestTemplateUtil marketplaceHttpUtil;
	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Override
	public boolean canPerform(final ActionContext<TmallRefundRequestModel> actionContext)
	{
		final Object data = actionContext.getData();

		TmallRefundRequestModel tmallRefundRequest = null;
		if (data instanceof TmallRefundRequestModel)
		{
			tmallRefundRequest = (TmallRefundRequestModel) data;
		}

		if (Objects.isNull(tmallRefundRequest) || Boolean.TRUE.equals(tmallRefundRequest.getWaitMarketPlaceResponse()))
		{
			return false;
		}

		//reject for return type at first step
		//currently refuse refund on second step(refund step) is not support
		if (RefundAction.AWAITING_APPROVAL.equals(tmallRefundRequest.getRefundAction())
				&& RefundType.RETURN_REFUND.equals(tmallRefundRequest.getRefundType()))
		{
			return true;
		}
		//reject for refund type only when the goods are shipped
		if (RefundAction.AWAITING_APPROVAL.equals(tmallRefundRequest.getRefundAction())
				&& RefundType.REFUND_ONLY.equals(tmallRefundRequest.getRefundType()))
		{
			if (tmallRefundRequest.getConsignment() != null
					&& ConsignmentStatus.SHIPPED.equals(tmallRefundRequest.getConsignment().getStatus().getCode()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<TmallRefundRequestModel> actionContext)
	{
		return null;
	}

	@Override
	public boolean needsConfirmation(final ActionContext<TmallRefundRequestModel> actionContext)
	{
		return false;
	}

	@Override
	public ActionResult<TmallRefundRequestModel> perform(final ActionContext<TmallRefundRequestModel> actionContext)
	{
		final TmallRefundRequestModel refundRequest = actionContext.getData();
		final TmallOrderModel order = refundRequest.getTmallOrder();
		String result = StringUtils.EMPTY;
		String urlStr = "";
		String marketplaceLogId = "";

		final String refundId = refundRequest.getRefundId();
		final String refuseMessage = refundRequest.getRefundComments();
		getModelService().save(refundRequest);

		if (StringUtils.isBlank(order.getMarketplaceStore().getIntegrationId()))
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.request.url.error", new Object[]
			{ order.getMarketplaceStore().getName() }), NotificationEvent.Type.WARNING, "");
			LOG.warn("authorization is expired!");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			return actionResult;
		}
		else if (StringUtils.isBlank(refundRequest.getRefundComments()))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.rejectrefund.requiredcomment"),
					NotificationEvent.Type.WARNING, "");

			LOG.warn("Reject failed due to comments is empty for model " + refundRequest);
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
			return actionResult;
		}

		try
		{
			marketplaceLogId = marketplaceHttpUtil.getUUID();
			urlStr = requestUrl + Config.getParameter(MARKETPLACE_REFUNDREQUEST_REJECT_PATH);
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("integrationId", order.getMarketplaceStore().getIntegrationId());
			jsonObj.put("refundId", refundId);
			jsonObj.put("refuseMessage", refuseMessage);
			jsonObj.put("marketplaceLogId", marketplaceLogId);
			logUtil.addMarketplaceLog("PENDING", refundRequest.getRefundId(), Labels
					.getLabel("mpintgomsbackoffice.refund.reject.action"), "TmallRefundRequest", order.getMarketplaceStore()
					.getMarketplace(), refundRequest, marketplaceLogId);
			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());

			result = ActionResult.SUCCESS;

			refundRequest.setWaitMarketPlaceResponse(Boolean.TRUE);
			getModelService().save(refundRequest);
			NotificationUtils.notifyUserVia(Localization.getLocalizedString("mpintgomsbackoffice.refund.button.rejectrefund.succ"),
					NotificationEvent.Type.SUCCESS, "");
		}
		catch (final Exception e)
		{
			LOG.error(e.toString());
			NotificationUtils.notifyUserVia(Localization.getLocalizedString("mpintgomsbackoffice.refund.button.rejectrefund.fail"),
					NotificationEvent.Type.FAILURE, "");
			result = ActionResult.ERROR;
		}

		final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
		actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
		return actionResult;
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
