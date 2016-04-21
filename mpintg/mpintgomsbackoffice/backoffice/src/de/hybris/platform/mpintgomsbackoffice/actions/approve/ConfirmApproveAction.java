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
package de.hybris.platform.mpintgomsbackoffice.actions.approve;

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
public class ConfirmApproveAction implements CockpitAction<TmallRefundRequestModel, TmallRefundRequestModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfirmApproveAction.class);
	public static final String MARKETPLACE_REFUNDREQUEST_APPROVE_PATH = "marketplace.refundrequest.approve.path";

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

		//refund only at the first step
		if (RefundType.REFUND_ONLY.equals(tmallRefundRequest.getRefundType())
				&& RefundAction.AWAITING_APPROVAL.equals(tmallRefundRequest.getRefundAction()))
		{
			return true;
		}

		//return and refund at the second step(refund step)
		if (RefundType.RETURN_REFUND.equals(tmallRefundRequest.getRefundType())
				&& RefundAction.AWAITING_RETURN_CONFIRMATION.equals(tmallRefundRequest.getRefundAction()))
		{
			return true;
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

		final String verifyCode = refundRequest.getVerifyCode();
		if (null == verifyCode || "".equals(verifyCode))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverefund.requiredcode"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		try
		{
			final String marketplaceLogId = marketplaceHttpUtil.getUUID();

			urlStr = requestUrl + Config.getParameter(MARKETPLACE_REFUNDREQUEST_APPROVE_PATH);
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("integrationId", order.getMarketplaceStore().getIntegrationId());
			jsonObj.put("refundId", refundRequest.getRefundId());
			jsonObj.put("amount", refundRequest.getRefundFee().replace(".", ""));
			//done in tmall app to retreive version
			jsonObj.put("version", "");
			jsonObj.put("phase", "");
			jsonObj.put("code", verifyCode);
			jsonObj.put("marketplaceLogId", marketplaceLogId);

			result = ActionResult.SUCCESS;
			logUtil.addMarketplaceLog("PENDING", refundRequest.getRefundId(), Labels
					.getLabel("mpintgomsbackoffice.refund.approve.action"), "TmallRefundRequest", order.getMarketplaceStore()
					.getMarketplace(), refundRequest, marketplaceLogId);
			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());

			refundRequest.setWaitMarketPlaceResponse(Boolean.TRUE);
			refundRequest.setVerifyCode("");
			getModelService().save(refundRequest);

			NotificationUtils.notifyUserVia(Localization.getLocalizedString("mpintgomsbackoffice.refund.action.approverefund.succ"),
					NotificationEvent.Type.SUCCESS, "");
		}
		catch (final Exception e)
		{
			LOG.error(e.toString());
			NotificationUtils.notifyUserVia(Labels.getLabel("mpintgomsbackoffice.refund.button.approverefund.fail"),
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
