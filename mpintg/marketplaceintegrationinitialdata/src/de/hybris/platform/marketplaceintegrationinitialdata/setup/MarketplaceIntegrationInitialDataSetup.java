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
package de.hybris.platform.marketplaceintegrationinitialdata.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.marketplaceintegrationinitialdata.constants.MarketplaceintegrationinitialdataConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = MarketplaceintegrationinitialdataConstants.EXTENSIONNAME)
public class MarketplaceIntegrationInitialDataSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(MarketplaceIntegrationInitialDataSetup.class);

	// common info
	private static final String ESSENTIAL_DATA = "/marketplaceintegrationinitialdata/import/coredata/essential-data.impex";
	// marketplace info
	private static final String MARKETPLACE_DATA = "/marketplaceintegrationinitialdata/import/sampledata/marketplace-data.impex";

	private static final String IMPORT_CHINA_MARKETPLACE_DATA = "importChinaMarketplaceData";

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{

		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CHINA_MARKETPLACE_DATA, "Import China Marketplace Data", true));

		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * An example usage of this method is to create required cronjobs or modifying the type system (setting e.g some
	 * default values)
	 *
	 * @param params
	 *           the parameters provided by user for creation of objects for the extension
	 * @param jspc
	 *           the jsp context; you can use it to write progress information to the jsp page during creation
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		if (this.getBooleanSystemSetupParameter(context, IMPORT_CHINA_MARKETPLACE_DATA))
		{
			importEssentialData(context);
		}
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 * @param systemSetup
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		if (this.getBooleanSystemSetupParameter(context, IMPORT_CHINA_MARKETPLACE_DATA))
		{
			importSampleData(context);
		}
	}

	/**
	 * import marketplace essential data
	 *
	 * @param context
	 */
	void importEssentialData(final SystemSetupContext context)
	{
		importChinaMarketData(context, ESSENTIAL_DATA);
	}

	/**
	 * import marketplace sample data
	 *
	 * @param context
	 */
	void importSampleData(final SystemSetupContext context)
	{
		importChinaMarketData(context, MARKETPLACE_DATA);
	}

	void importChinaMarketData(final SystemSetupContext context, final String dataLocation)
	{
		//this.logInfo(context, String.format("Begin importing data for [%s]", context.getExtensionName()));
		getSetupImpexService().importImpexFile(String.format(dataLocation, context.getExtensionName()), false);
	}

}
