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
package de.hybris.platform.mpintgomsbackoffice.renderer;


import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.marketplaceintegrationbackoffice.renderer.MarketplaceIntegrationOrderInitialRenderer;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeRestTemplateUtil;
import de.hybris.platform.mpintgordermanagement.enums.RefundAction;
import de.hybris.platform.mpintgordermanagement.enums.RefundType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import com.hybris.backoffice.model.TmallRefundRequestModel;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaPanelRenderer;


public class GetVerifyCodeRenderer extends AbstractEditorAreaPanelRenderer<TmallRefundRequestModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceIntegrationOrderInitialRenderer.class);
	private final Integer BATCH_SIZE = new Integer(200);

	public static final String MARKETPLACE_ORDER_SYCHRONIZE_PATH = "marketplace.order.sychronize.path";
	public static final String MARKETPLACE_ORDER_INITIAL_PATH = "marketplace.order.initial.path";
	public static final String BACKOFFICE_FORMAT_DATEFORMAT = "backoffice.format.dateformat";
	public static final String MARKETPLACE_ORDER_INITIAL_LOGUUID = "marketplace.path.parameter.marketplaceloguuid";

	private ModelService modelService;
	private CockpitProperties cockpitProperties;
	private PermissionFacadeStrategy permissionFacadeStrategy;
	private ObjectFacade objectFacade;
	private WidgetInstanceManager widgetInstanceManager;
	@Autowired
	private MarketplaceintegrationbackofficeRestTemplateUtil marketplaceHttpUtil;
	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	public static final String MARKETPLACE_REFUNDREQUEST_APPROVE_PATH = "marketplace.refundrequest.approve.path";

	@Value("${datahub.refund.request.url}")
	private String requestUrl;

	@Override
	public void render(final Component parent, final AbstractPanel panel, final TmallRefundRequestModel data, final DataType type,
			final WidgetInstanceManager widgetInstanceManager)
	{
		this.widgetInstanceManager = widgetInstanceManager;

		final Button loadBtn = new Button(Labels.getLabel("mpintgomsbackoffice.refund.button.getverifycode"));

		if (Objects.isNull(data) || Boolean.TRUE.equals(data.getWaitMarketPlaceResponse()))
		{
			loadBtn.setDisabled(true);
		}
		else
		{
			loadBtn.setDisabled(true);

			//refund only at the first step
			if (RefundType.REFUND_ONLY.equals(data.getRefundType()) && RefundAction.AWAITING_APPROVAL.equals(data.getRefundAction()))
			{
				loadBtn.setDisabled(false);
			}

			//return and refund at the second step(refund step)
			if (RefundType.RETURN_REFUND.equals(data.getRefundType())
					&& RefundAction.AWAITING_RETURN_CONFIRMATION.equals(data.getRefundAction()))
			{
				loadBtn.setDisabled(false);
			}
		}
		loadBtn.setSclass("initial-load-btn");
		loadBtn.addEventListener(Events.ON_CLICK, event -> {
			// trigger initial order load
				getVerifyCode(data);
			});
		parent.appendChild(loadBtn);

	}

	private void getVerifyCode(final TmallRefundRequestModel model)
	{
		final TmallRefundRequestModel refundRequest = model;
		final TmallOrderModel order = refundRequest.getTmallOrder();
		String urlStr = "";

		if (StringUtils.isBlank(order.getMarketplaceStore().getIntegrationId()))
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.consignment.request.url.error", new Object[]
			{ order.getMarketplaceStore().getName() }), NotificationEvent.Type.WARNING, "");
			LOG.warn("authorization is expired!");
			return;
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
			jsonObj.put("code", "");
			jsonObj.put("marketplaceLogId", marketplaceLogId);

			logUtil.addMarketplaceLog("PENDING", refundRequest.getRefundId(), Labels
					.getLabel("mpintgomsbackoffice.refund.approve.action"), "TmallRefundRequest", order.getMarketplaceStore()
					.getMarketplace(), refundRequest, marketplaceLogId);
			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());

			NotificationUtils.notifyUserVia(Localization.getLocalizedString("mpintgomsbackoffice.refund.button.getverifycode.succ"),
					NotificationEvent.Type.SUCCESS, "");
		}
		catch (final Exception e)
		{
			LOG.error(e.toString());
			NotificationUtils.notifyUserVia(Labels.getLabel("mpintgomsbackoffice.refund.button.getverifycode.fail"),
					NotificationEvent.Type.FAILURE, "");
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CockpitProperties getCockpitProperties()
	{
		return cockpitProperties;
	}

	@Required
	public void setCockpitProperties(final CockpitProperties cockpitProperties)
	{
		this.cockpitProperties = cockpitProperties;
	}

	@Required
	public void setPermissionFacadeStrategy(final PermissionFacadeStrategy permissionFacadeStrategy)
	{
		this.permissionFacadeStrategy = permissionFacadeStrategy;
	}

	public void setObjectFacade(final ObjectFacade objectFacade)
	{
		this.objectFacade = objectFacade;
	}

	private WidgetInstanceManager getWidgetInstanceManager()
	{
		return widgetInstanceManager;
	}
}
