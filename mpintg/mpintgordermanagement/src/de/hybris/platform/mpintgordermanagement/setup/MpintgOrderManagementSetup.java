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
package de.hybris.platform.mpintgordermanagement.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.mpintgordermanagement.constants.MpintgordermanagementConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = MpintgordermanagementConstants.EXTENSIONNAME)
public class MpintgOrderManagementSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(MpintgOrderManagementSetup.class);

	private static final String PROCESS_DATA = "/mpintgordermanagement/import/sampledata/return-process.impex";
	private static final String CARRIER_DATA = "/mpintgordermanagement/import/sampledata/marketplace-carrier-data.impex";

	private static final String IMPORT_MARKETPLACE_OMS_DATA = "importMarketplaceOMSData";

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{

		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_MARKETPLACE_OMS_DATA, "Import Marketplace OMS Data", true));

		return params;
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
		if (this.getBooleanSystemSetupParameter(context, IMPORT_MARKETPLACE_OMS_DATA))
		{
			importSampleDataProcess(context);
		}
	}

	/**
	 * import marketplace sample data
	 *
	 * @param context
	 */	
	void importSampleDataProcess(final SystemSetupContext context)
	{
		importMarketCarrierData(context, PROCESS_DATA);
		importMarketCarrierData(context, CARRIER_DATA);
	}

	void importMarketCarrierData(final SystemSetupContext context, final String dataLocation)
	{
		//this.logInfo(context, String.format("Begin importing data for [%s]", context.getExtensionName()));
		getSetupImpexService().importImpexFile(String.format(dataLocation, context.getExtensionName()), false);
	}

}
