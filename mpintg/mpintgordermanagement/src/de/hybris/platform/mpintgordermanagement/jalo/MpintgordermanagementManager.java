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
package de.hybris.platform.mpintgordermanagement.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.mpintgordermanagement.constants.MpintgordermanagementConstants;
import org.apache.log4j.Logger;


@SuppressWarnings("PMD")
public class MpintgordermanagementManager extends GeneratedMpintgordermanagementManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(MpintgordermanagementManager.class.getName());

	public static final MpintgordermanagementManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MpintgordermanagementManager) em.getExtension(MpintgordermanagementConstants.EXTENSIONNAME);
	}

}
