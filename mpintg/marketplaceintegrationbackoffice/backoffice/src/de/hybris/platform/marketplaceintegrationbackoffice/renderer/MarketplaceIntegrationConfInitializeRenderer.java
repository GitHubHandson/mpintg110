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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceSellerModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaPanelRenderer;


public class MarketplaceIntegrationConfInitializeRenderer extends AbstractEditorAreaPanelRenderer<MarketplaceStoreModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceIntegrationConfInitializeRenderer.class);

	public static final String MARKETPLACE_AUTHENTICATE_PATH = "marketplace.authenticate.path";
	public static final String MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_STOREID = "marketplace.authenticate.path.parameter.storeid";
	public static final String MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_STORENAME = "marketplace.authenticate.path.parameter.storename";
	public static final String MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_SELLERNAME = "marketplace.authenticate.path.parameter.sellername";
	public static final String MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_SUFFIX = "marketplace.authenticate.path.parameter.suffix";
	public static final String MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_LOGUUID = "marketplace.path.parameter.marketplaceloguuid";

	private ModelService modelService;
	private CockpitProperties cockpitProperties;
	private PermissionFacadeStrategy permissionFacadeStrategy;
	private ObjectFacade objectFacade;
	private WidgetInstanceManager widgetInstanceManager;
	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Override
	public void render(final Component parent, final AbstractPanel panel, final MarketplaceStoreModel data, final DataType type,
			final WidgetInstanceManager widgetInstanceManager)
	{
		this.widgetInstanceManager = widgetInstanceManager;
		final String logUUID = logUtil.getUUID();
		final MarketplaceSellerModel seller = data.getMarketplaceSeller();
		final MarketplaceModel model = seller.getMarketplace();

		final Button loadBtn = new Button(Labels.getLabel("backoffice.button.marketplace.authenticate"));
		loadBtn.setSclass("initial-load-btn");
		loadBtn.setTarget("_blank");
		loadBtn.addEventListener(
				Events.ON_MOUSE_OVER,
				event -> {
					final String requestUrl = model.getAdapterUrl() + Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH)
							+ Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_STOREID) + data.getPk()
							+ Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_STORENAME) + data.getName()
							+ Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_SELLERNAME) + seller.getName()
							+ Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_SUFFIX) + data.getMarketplace().getName()
							+ Config.getParameter(MARKETPLACE_AUTHENTICATE_PATH_PARAMETER_LOGUUID) + logUUID;
					loadBtn.setHref(requestUrl);

					//					logUtil.addMarketplaceLog("PENDING", model.getCode(), Labels.getLabel("marketplace.order.authorization.action"),
					//							Labels.getLabel("marketplace.order.authorization.object.type"), data.getMarketplace(), model, logUUID);

				});
		parent.appendChild(loadBtn);
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
