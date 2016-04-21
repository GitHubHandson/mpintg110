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
package de.hybris.platform.marketplaceintegrationbackoffice.widgets;

import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.testing.annotation.InextensibleMethod;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaController;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;


/**
 * Controller for Marketplace Editor Area
 */
public class MarketplaceEditorAreaController extends DefaultEditorAreaController
{

	@WireVariable
	private CockpitEventQueue cockpitEventQueue;

	@Override
	@ViewEvent(componentID = COMP_ID_SAVE_BTN, eventName = Events.ON_CLICK)
	public void saveObject() throws ObjectSavingException
	{
		if (isModelValueChanged())
		{
			try
			{
				final Context ctx = new DefaultContext();
				ctx.addAttribute(ObjectFacade.CTX_DISABLE_CRUD_COCKPIT_EVENT_NOTIFICATION, Boolean.TRUE);
				final Object savedObject = getObjectFacade().save(getCurrentObject(), ctx);
				getModel().setValue(MODEL_CURRENT_OBJECT, savedObject);

				final Map<String, EventListener<Event>> afterSaveListeners = EditorAreaRendererUtils
						.getAfterSaveListeners(getModel());
				if (afterSaveListeners != null)
				{
					for (final EventListener<Event> listener : afterSaveListeners.values())
					{
						try
						{
							listener.onEvent(new Event("afterSave"));
						}
						catch (final Exception e) // NOPMD, zk specific
						{
							throw new ObjectSavingException(String.valueOf(getCurrentObject()), e);
						}
					}
				}
				final Object currentObject = getCurrentObject();
				sendOutput(SOCKET_OUTPUT_OBJECT_SAVED, currentObject);
				handleObjectSavingSuccess(currentObject);
				publishCRUDCockpitEventNotification(ObjectFacade.OBJECT_UPDATED_EVENT, currentObject);

				if (BooleanUtils.isTrue(getValue(MODEL_INPUT_OBJECT_IS_NEW, Boolean.class)))
				{
					setObject(currentObject);
				}
				else
				{
					resetValueChangedState();
					updateWidgetTitle();
				}
			}
			catch (final ObjectSavingException ex)
			{
				handleObjectSavingException(ex);
			}
		}
	}

	@InextensibleMethod
	private void publishCRUDCockpitEventNotification(final String info, final Object obj)
	{
		cockpitEventQueue.publishEvent(new DefaultCockpitEvent(info, obj, null));
	}

	@InextensibleMethod
	private void resetValueChangedState()
	{
		setValue(MODEL_INPUT_OBJECT_IS_NEW, Boolean.FALSE);
		setValue(MODEL_INPUT_OBJECT_IS_MODIFIED, Boolean.FALSE);
		setValue(MODEL_VALUE_CHANGED, Boolean.FALSE);
	}
}
