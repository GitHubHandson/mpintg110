/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */
package de.hybris.platform.marketplaceintegrationbackoffice.renderer;

import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeRestTemplateUtil;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

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


/**
 *
 */
public class MarketplaceIntegrationOrderRequestRenderer extends AbstractEditorAreaPanelRenderer<MarketplaceStoreModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceIntegrationOrderRequestRenderer.class);
	private final Integer BATCH_SIZE = new Integer(200);

	public static final String MARKETPLACE_REFUND_SYCHRONIZE_PATH = "marketplace.refund.sychronize.path";
	public static final String MARKETPLACE_REFUND_REQUEST_PATH = "marketplace.refund.request.path";
	public static final String BACKOFFICE_FORMAT_DATEFORMAT = "backoffice.format.dateformat";
	public static final String MARKETPLACE_REFUND_REQUEST_LOGUUID = "marketplace.path.parameter.marketplaceloguuid";

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

		final Button loadBtn = new Button(Labels.getLabel("backoffice.button.marketplacestore.order.request"));

		loadBtn.setSclass("initial-load-btn");
		loadBtn.addEventListener(Events.ON_CLICK, event -> {
			orderRequestDownload(data);
		});
		LOG.debug("The new added renderer MarketplaceIntegrationOrderRequestRenderer has bean loaded successfully");
		parent.appendChild(loadBtn);

	}

	private void orderRequestDownload(final MarketplaceStoreModel model)
	{
		if (null == model.getRequestStartTime())
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("type.Marketplacestore." + MarketplaceStoreModel.REQUESTSTARTTIME + ".name") + " "
							+ Labels.getLabel("backoffice.field.notfilled"), NotificationEvent.Type.WARNING, "");
			LOG.warn("Order request start time is not filled!");
			return;
		}
		else if (null == model.getRequestEndTime())
		{
			NotificationUtils.notifyUserVia(
					Localization.getLocalizedString("type.Marketplacestore." + MarketplaceStoreModel.REQUESTENDTIME + ".name") + " "
							+ Labels.getLabel("backoffice.field.notfilled"), NotificationEvent.Type.WARNING, "");
			LOG.warn("Order request end time is not filled!");
			return;
		}
		else if (model.getRequestStartTime().after(model.getRequestEndTime()))
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("backoffice.field.timerange.error"), NotificationEvent.Type.WARNING, "");
			LOG.warn("start time is greater than end time!");
			return;
		}

		if (!StringUtils.isBlank(model.getIntegrationId()) && !model.getAuthorized().booleanValue())
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.fail"), NotificationEvent.Type.WARNING,
					"");
			LOG.warn("authorization is expired!");
			return;
		}
		//in order to avoid this value out of date, we only get it from database
		final Boolean isAuth = ((MarketplaceStoreModel) modelService.get(model.getPk())).getAuthorized();
		final String integrationId = ((MarketplaceStoreModel) modelService.get(model.getPk())).getIntegrationId();
		model.setIntegrationId(integrationId);
		model.setAuthorized(isAuth);
		if (null == isAuth || !isAuth.booleanValue())
		{
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.refund.requestorder.unauthed"),
					NotificationEvent.Type.WARNING, "");
			LOG.warn("marketplace store do not authorized, download refund/return order failed!");
			return;
		}

		String urlStr = "";
		final String logUUID = logUtil.getUUID();
		final MarketplaceSellerModel seller = model.getMarketplaceSeller();
		final MarketplaceModel marketPlace = seller.getMarketplace();

		try
		{
			// Configure and open a connection to the site you will send the
			urlStr = marketPlace.getAdapterUrl() + Config.getParameter(MARKETPLACE_REFUND_SYCHRONIZE_PATH)
					+ Config.getParameter(MARKETPLACE_REFUND_REQUEST_PATH) + integrationId
					+ Config.getParameter(MARKETPLACE_REFUND_REQUEST_LOGUUID) + logUUID;
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("batchSize", BATCH_SIZE);

			//set the correct timezone
			final String configTimezone = model.getMarketplace().getTimezone();
			boolean isValidTimezone = false;
			for (final String vaildTimezone : TimeZone.getAvailableIDs())
			{
				if (vaildTimezone.equals(configTimezone))
				{
					isValidTimezone = true;
					break;
				}
			}

			if (!isValidTimezone)
			{
				final String[] para =
				{ configTimezone == null ? "" : configTimezone, model.getMarketplace().getName() };
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.initorder.wrongtimezone", para),
						NotificationEvent.Type.WARNING, "");
				LOG.warn("wrong timezone or missing timezone configed in market:" + model.getMarketplace().getName());
				return;
			}

			final SimpleDateFormat format = new SimpleDateFormat(Config.getParameter(BACKOFFICE_FORMAT_DATEFORMAT));
			format.setTimeZone(TimeZone.getTimeZone(configTimezone));

			final String startTimeWithCorrectZone = format.format(model.getRequestStartTime()).toString();
			final String endTimeWithCorrectZone = format.format(model.getRequestEndTime()).toString();

			logUtil.addMarketplaceLog("PENDING", integrationId, Labels.getLabel("marketplace.order.requestorder.action"),
					model.getItemtype(), marketPlace, model, logUUID);

			jsonObj.put("startCreated", startTimeWithCorrectZone);
			jsonObj.put("endCreated", endTimeWithCorrectZone);
			jsonObj.put("productCatalogVersion", model.getCatalogVersion().getCatalog().getId() + ":"
					+ model.getCatalogVersion().getVersion());
			jsonObj.put("currency", model.getCurrency().getIsocode());
			marketplaceHttpUtil.post(urlStr, jsonObj.toJSONString());
		}
		catch (final HttpClientErrorException httpError)
		{
			if (httpError.getStatusCode().is4xxClientError())
			{
				LOG.error("=========================================================================");
				LOG.error("Order Request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + urlStr);
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
				LOG.error("Order Request post to Tmall failed!");
				LOG.error("Marketplacestore Code: " + model.getName());
				LOG.error("Error Status Code: " + httpError.getStatusCode().toString());
				LOG.error("Request path: " + urlStr);
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
			return;
		}
		catch (final ResourceAccessException raError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order Request post to Tmall failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + urlStr);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Order Request server access failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + raError.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.access.error"),
					NotificationEvent.Type.FAILURE, "");
			return;
		}
		catch (final HttpServerErrorException serverError)
		{
			LOG.error("=========================================================================");
			LOG.error("Order Request post to Tmall failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + urlStr);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Order Request server process failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + serverError.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.error.server.process.error"),
					NotificationEvent.Type.FAILURE, "");
			return;
		}
		catch (final Exception e)
		{
			LOG.error("=========================================================================");
			LOG.error("Order Request failed!");
			LOG.error("Marketplacestore Code: " + model.getName());
			LOG.error("Request path: " + urlStr);
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Failed Reason:");
			LOG.error("Order Request server process failed!");
			LOG.error("-------------------------------------------------------------------------");
			LOG.error("Detail error info: " + e.getMessage());
			LOG.error("=========================================================================");
			NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.refund.requestorder.fail"), NotificationEvent.Type.FAILURE,
					"");
			return;
		}
		NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.refund.requestorder.success"), NotificationEvent.Type.SUCCESS,
				"");
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
