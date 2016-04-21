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
package de.hybris.platform.marketplaceintegrationbackoffice.renderer;

import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeRestTemplateUtil;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceSellerModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaPanelRenderer;


public class MarketplaceIntegrationOrderIncrementalRenderer extends AbstractEditorAreaPanelRenderer<MarketplaceStoreModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceIntegrationOrderIncrementalRenderer.class);

	public static final String MARKETPLACE_ORDER_REALTIME_SYNC_PATH = "marketplace.order.realtime.sync.path";
	public static final String MARKETPLACE_ORDER_SYCHRONIZE_MIDDLE_PATH = "marketplace.order.sychronize.middle.path";
	public static final String MARKETPLACE_ORDER_SYCHRONIZE_LOGUUID = "marketplace.path.parameter.marketplaceloguuid";

	private ModelService modelService;
	private CockpitProperties cockpitProperties;
	private PermissionFacadeStrategy permissionFacadeStrategy;
	private ObjectFacade objectFacade;
	private WidgetInstanceManager widgetInstanceManager;
	@Autowired
	private MarketplaceintegrationbackofficeRestTemplateUtil marketplaceHttpUtil;
	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Override
	public void render(final Component parent, final AbstractPanel panel, final MarketplaceStoreModel data, final DataType type,
			final WidgetInstanceManager widgetInstanceManager)
	{
		this.widgetInstanceManager = widgetInstanceManager;
		final Button loadBtnEnable = new Button(Labels.getLabel("backoffice.button.marketplacestore.subscribeorder.enable"));
		final Button loadBtnDisable = new Button(Labels.getLabel("backoffice.button.marketplacestore.subscribeorder.disable"));

		loadBtnEnable.setSclass("initial-load-box");
		loadBtnEnable.addEventListener(Events.ON_CLICK,
				event -> {
					if (incrementalOrderDownload(data, "ON"))
					{
						data.setSubscribeorder(Boolean.TRUE);
						this.modelService.save(data);
						parent.removeChild(loadBtnEnable);
						parent.appendChild(loadBtnDisable);
					}
					else
					{
						NotificationUtils.notifyUserVia(Labels.getLabel("subscribe.order.enable.status"),
								NotificationEvent.Type.WARNING, "");
						LOG.warn("enable order subscription failed. reason: connection error!");
						return;
					}
				});
		loadBtnDisable.setSclass("initial-load-box");
		loadBtnDisable.addEventListener(Events.ON_CLICK,
				event -> {
					if (incrementalOrderDownload(data, "OFF"))
					{
						data.setSubscribeorder(Boolean.FALSE);
						this.modelService.save(data);
						parent.removeChild(loadBtnDisable);
						parent.appendChild(loadBtnEnable);
					}
					else
					{
						NotificationUtils.notifyUserVia(Labels.getLabel("subscribe.order.disable.status"),
								NotificationEvent.Type.WARNING, "");
						LOG.warn("disable order subscription failed. reason: connection error!");
						return;
					}
				});
		if (data.getSubscribeorder() == Boolean.FALSE)
		{
			parent.appendChild(loadBtnEnable);
		}
		else if (data.getSubscribeorder() == Boolean.TRUE)
		{
			parent.appendChild(loadBtnDisable);
		}

	}

	private boolean incrementalOrderDownload(final MarketplaceStoreModel model, final String status)
	{
		boolean flag = false;
		final String logUUID = logUtil.getUUID();
		final MarketplaceSellerModel seller = model.getMarketplaceSeller();
		final MarketplaceModel marketPlace = seller.getMarketplace();
		modelService.refresh(seller);

		final String requestUrl = marketPlace.getAdapterUrl() + Config.getParameter(MARKETPLACE_ORDER_REALTIME_SYNC_PATH)
				+ Config.getParameter(MARKETPLACE_ORDER_SYCHRONIZE_MIDDLE_PATH) + model.getIntegrationId()
				+ Config.getParameter(MARKETPLACE_ORDER_SYCHRONIZE_LOGUUID) + logUUID;
		final JSONObject jsonObj = new JSONObject();
		jsonObj.put("currency", model.getCurrency().getIsocode());
		jsonObj.put("productCatalogVersion", model.getCatalogVersion().getCatalog().getId() + ":"
				+ model.getCatalogVersion().getVersion());
		jsonObj.put("status", status);

		try
		{
			this.saveMarketplaceLog(status, model, logUUID);

			final JSONObject results = marketplaceHttpUtil.post(requestUrl, jsonObj.toJSONString());
			final String msg = results.toJSONString();
			final String responseCode = results.get("code").toString();

			if ("401".equals(responseCode))
			{
				LOG.error("=========================================================================");
				LOG.error("Order incremental download request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + responseCode);
				LOG.error("Request path: " + requestUrl);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("Authentication was failed, please re-authenticate again!");
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("=========================================================================");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.fail"),
						NotificationEvent.Type.FAILURE, "");
				LOG.warn("Authentication was failed, please re-authenticate again!");
			}
			else if (!("0".equals(responseCode)))
			{
				LOG.error("=========================================================================");
				LOG.error("Order incremental download request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + responseCode);
				LOG.error("Request path: " + requestUrl);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("A known issue occurs in tmall, error details :" + msg);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("=========================================================================");
				/*
				 * NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.tmallapp.known.issues", new Object[] { msg
				 * }), NotificationEvent.Type.FAILURE, "");
				 */
				LOG.warn("A known issue occurs in tmall, error details :" + msg);
			}
			else if ("0".equals(responseCode))
			{
				LOG.debug("Open listen sucessfully");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.incremental.order.success"),
						NotificationEvent.Type.SUCCESS, "");
				flag = true;
				LOG.info("=========================================================================");
				LOG.info("Order incremental download request post to Tmall suceessfully!");
				LOG.info("-------------------------------------------------------------------------");
				LOG.info("Marketplacestore Code: " + model.getName());
				LOG.info("Request path: " + requestUrl);
				LOG.info("=========================================================================");

			}
		}
		catch (final HttpClientErrorException httpError)
		{
			if (httpError.getStatusCode().is4xxClientError())
			{
				LOG.error("=========================================================================");
				LOG.error("Order incremental download request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + requestUrl);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("Requested Tmall service URL is not correct!");
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Detail error info: " + httpError.getMessage());
				LOG.error("=========================================================================");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.request.post.error"),
						NotificationEvent.Type.FAILURE, "");

			}
			if (httpError.getStatusCode().is5xxServerError())
			{
				LOG.error("=========================================================================");
				LOG.error("Order incremental download request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + requestUrl);
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Failed Reason:");
				LOG.error("Requested Json Ojbect is not correct!");
				LOG.error("-------------------------------------------------------------------------");
				LOG.error("Detail error info: " + httpError.getMessage());
				LOG.error("=========================================================================");
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.process.error"),
						NotificationEvent.Type.FAILURE, "");
			}
			LOG.error(httpError.toString());
			return flag;
		}
		catch (final ResourceAccessException raError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order incremental download request post to Tmall failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + requestUrl);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Marketplace order download request server access failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + raError.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.access.error"),
					NotificationEvent.Type.FAILURE, "");
			return flag;
		}
		catch (final HttpServerErrorException serverError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order incremental download request post to Tmall failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + requestUrl);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Marketplace order download request server process failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + serverError.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.process.error"),
					NotificationEvent.Type.FAILURE, "");
			return flag;
		}
		catch (final Exception e)
		{
			final String errorMsg = e.getClass().toString() + ":" + e.getMessage();
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.runtime.issues", new Object[]
			{ errorMsg }), NotificationEvent.Type.FAILURE, "");
			LOG.error("=========================================================================");
			LOG.error("Order incremental download request failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + requestUrl);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + e.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.process.error"),
					NotificationEvent.Type.FAILURE, "");
			LOG.warn(e.getMessage() + e.getStackTrace());
			return flag;
		}
		return flag;
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

	private void saveMarketplaceLog(final String status, final MarketplaceStoreModel model, final String logUUID)
	{
		if (StringUtils.equals("ON", status))
		{
			logUtil.addMarketplaceLog("PENDING", model.getIntegrationId(),
					Labels.getLabel("backoffice.button.marketplacestore.subscribeorder.enable"), model.getItemtype(),
					model.getMarketplace(), model, logUUID);
		}
		else if (StringUtils.equals("OFF", status))
		{
			logUtil.addMarketplaceLog("PENDING", model.getIntegrationId(),
					Labels.getLabel("backoffice.button.marketplacestore.subscribeorder.disable"), model.getItemtype(),
					model.getMarketplace(), model, logUUID);
		}
	}
}
