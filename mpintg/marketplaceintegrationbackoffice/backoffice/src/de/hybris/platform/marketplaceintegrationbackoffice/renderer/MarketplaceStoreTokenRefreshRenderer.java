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


import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceIntegrationBackofficeLogger;
import de.hybris.platform.marketplaceintegrationbackoffice.utils.MarketplaceintegrationbackofficeHttpClient;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


public class MarketplaceStoreTokenRefreshRenderer extends AbstractEditorAreaPanelRenderer<MarketplaceStoreModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceStoreModel.class);

	private ModelService modelService;
	private CockpitProperties cockpitProperties;
	private PermissionFacadeStrategy permissionFacadeStrategy;
	private ObjectFacade objectFacade;
	private WidgetInstanceManager widgetInstanceManager;
	private MarketplaceIntegrationBackofficeLogger marketplaceLogger;
	private MarketplaceintegrationbackofficeHttpClient marketplaceClient;

	@Override
	public void render(final Component parent, final AbstractPanel panel, final MarketplaceStoreModel data, final DataType type,
			final WidgetInstanceManager widgetInstanceManager)
	{
		this.widgetInstanceManager = widgetInstanceManager;

		final Button loadBtn = new Button(Labels.getLabel("backoffice.button.marketplace.Refresh"));

		loadBtn.setSclass("initial-load-btn");
		loadBtn.addEventListener(Events.ON_CLICK, event -> {
			// trigger refresh order load
				refreshOrderDownload(data);
			});
		parent.appendChild(loadBtn);
	}

	private void refreshOrderDownload(final MarketplaceStoreModel model)
	{
		final MarketplaceSellerModel seller = model.getMarketplaceSeller();
		final MarketplaceModel marketplace = seller.getMarketplace();
		modelService.refresh(seller);

		final String requestUrl = marketplace.getAdapterUrl() + Labels.getLabel("marketplace.refresh.token.path")
				+ Labels.getLabel("marketplace.refresh.token.path.parameter") + model.getIntegrationId();
		try
		{
			marketplaceClient.redirectRequest(requestUrl);
		}
		catch (final IOException e)
		{
			LOG.error(e.toString());
			// YTODO log process need to be implemented
			marketplaceLogger.log(e.toString());
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

	@Required
	public void setMarketplaceLogger(final MarketplaceIntegrationBackofficeLogger marketplaceLogger)
	{
		this.marketplaceLogger = marketplaceLogger;
	}

	@Required
	public void setMarketplaceClient(final MarketplaceintegrationbackofficeHttpClient marketplaceClient)
	{
		this.marketplaceClient = marketplaceClient;
	}
}
