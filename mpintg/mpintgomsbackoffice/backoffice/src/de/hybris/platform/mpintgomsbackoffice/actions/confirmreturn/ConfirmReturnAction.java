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
package de.hybris.platform.mpintgomsbackoffice.actions.confirmreturn;

import de.hybris.platform.core.model.user.AddressModel;
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
public class ConfirmReturnAction implements CockpitAction<TmallRefundRequestModel, TmallRefundRequestModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfirmReturnAction.class);
	public static final String MARKETPLACE_REFUNDREQUEST_CONFIRMRETURN_PATH = "marketplace.refundrequest.returngoodsagree.path";

	@Value("${datahub.refund.request.url}")
	private String requestUrl;

	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Resource
	private ModelService modelService;

	@Autowired
	private MarketplaceintegrationbackofficeRestTemplateUtil marketplaceHttpUtil;

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

		//return only at the first step
		if (RefundType.RETURN_REFUND.equals(tmallRefundRequest.getRefundType())
				&& RefundAction.AWAITING_APPROVAL.equals(tmallRefundRequest.getRefundAction()))
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

		final String refundId = refundRequest.getRefundId();
		final String returnRemark = refundRequest.getReturnRemark() == null ? "" : refundRequest.getReturnRemark();
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

		final AddressModel returnAddress = refundRequest.getReturnAddress();
		if (null == returnAddress)
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.requiredaddress"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}


		final Boolean isShippingAddress = returnAddress.getShippingAddress();
		if (isShippingAddress.equals(Boolean.FALSE))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.noshippingaddress"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		final String lastname = returnAddress.getLastname() == null ? "" : returnAddress.getLastname();
		final String firstname = returnAddress.getFirstname() == null ? "" : returnAddress.getFirstname();

		final String name = lastname + firstname;
		if ("".equals(name))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.nameempty"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		String country = "";
		if (returnAddress.getCountry() != null)
		{
			country = returnAddress.getCountry().getName() == null ? "" : returnAddress.getCountry().getName();
		}

		String region = "";
		if (returnAddress.getRegion() != null)
		{
			region = returnAddress.getRegion().getName() == null ? "" : returnAddress.getRegion().getName();
		}

		String city = "";
		if (returnAddress.getCity() != null)
		{
			city = returnAddress.getCity().getName() == null ? "" : returnAddress.getCity().getName();
		}

		final String district = returnAddress.getDistrict() == null ? "" : returnAddress.getDistrict();
		final String streetName = returnAddress.getStreetname() == null ? "" : returnAddress.getStreetname();
		final String streetNumber = returnAddress.getStreetnumber() == null ? "" : returnAddress.getStreetnumber();

		final String fullAddress = country + region + city + district + streetName + streetNumber;
		if ("".equals(fullAddress))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.fulladdressempty"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		final String post = returnAddress.getPostalcode() == null ? "" : returnAddress.getPostalcode();
		if ("".equals(post))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.postempty"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		final String tel = returnAddress.getPhone1() == null ? "" : returnAddress.getPhone1();
		if ("".equals(tel))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.telempty"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		final String mobile = returnAddress.getCellphone() == null ? "" : returnAddress.getCellphone();
		if ("".equals(mobile))
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("mpintgomsbackoffice.refund.button.approverereturn.mobileempty"),
					NotificationEvent.Type.WARNING, "");
			result = ActionResult.ERROR;
			final ActionResult<TmallRefundRequestModel> actionResult = new ActionResult<TmallRefundRequestModel>(result);
			return actionResult;
		}

		try
		{
			final String marketplaceLogId = marketplaceHttpUtil.getUUID();

			urlStr = requestUrl + Config.getParameter(MARKETPLACE_REFUNDREQUEST_CONFIRMRETURN_PATH);
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("integrationId", order.getMarketplaceStore().getIntegrationId());
			jsonObj.put("marketplaceLogId", marketplaceLogId);
			jsonObj.put("refundId", refundId);
			jsonObj.put("name", name);
			jsonObj.put("address", fullAddress);
			jsonObj.put("post", post);
			jsonObj.put("tel", tel);
			jsonObj.put("mobile", mobile);
			jsonObj.put("remark", returnRemark);

			result = ActionResult.SUCCESS;
			logUtil.addMarketplaceLog("PENDING", refundRequest.getRefundId(), Labels
					.getLabel("mpintgomsbackoffice.refund.approvereturn.action"), "TmallRefundRequest", order.getMarketplaceStore()
					.getMarketplace(), refundRequest, marketplaceLogId);
			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());

			refundRequest.setWaitMarketPlaceResponse(Boolean.TRUE);
			getModelService().save(refundRequest);

			NotificationUtils.notifyUserVia(Localization.getLocalizedString("mpintgomsbackoffice.refund.action.approvereturn.succ"),
					NotificationEvent.Type.SUCCESS, "");

			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());
			result = ActionResult.SUCCESS;
		}
		catch (final Exception e)
		{
			LOG.error(e.toString());
			NotificationUtils.notifyUserVia(Labels.getLabel("mpintgomsbackoffice.refund.action.approvereturn.fail"),
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
