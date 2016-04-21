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

import de.hybris.platform.util.localization.Localization;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


public class MarketplaceValueTextEditor implements CockpitEditorRenderer<String>
{
	@Override
	public void render(final Component parent, final EditorContext<String> context, final EditorListener<String> listener)
	{
		// Create UI component
		final Textbox editorView = new Textbox();

		// Set the initial value
		editorView.setValue(context.getInitialValue());

		// Set the editable state
		if (!context.isEditable())
		{
			if (Executions.getCurrent().getUserAgent().contains("MSIE"))
			{
				editorView.setReadonly(true);
			}
			else
			{
				editorView.setDisabled(true);
			}
		}

		// Handle events
		editorView.addEventListener(Events.ON_CHANGE, new EventListener<Event>()
		{
			@Override
			public void onEvent(final Event event) throws Exception // NOPMD
			{
				handleEvent(editorView, event, listener, context);
			}
		});
		editorView.addEventListener(Events.ON_OK, new EventListener<Event>()
		{
			@Override
			public void onEvent(final Event event) throws Exception // NOPMD
			{
				handleEvent(editorView, event, listener, context);
			}
		});

		// Add the UI component to the component tree
		editorView.setParent(parent);
	}

	/**
	 * Handle a view event on the editor view component.
	 *
	 * @param editorView
	 *           the view component
	 * @param event
	 *           the event to be handled
	 * @param listener
	 *           the editor listener to send change notifications to
	 */
	protected void handleEvent(final Textbox editorView, final Event event, final EditorListener<String> listener,
			final EditorContext<String> context)
	{
		final String result = (String) editorView.getRawValue();
		listener.onValueChanged(StringUtils.isEmpty(result) ? "" : result);
		if (Events.ON_OK.equals(event.getName()))
		{
			listener.onEditorEvent(EditorListener.ENTER_PRESSED);
		}
		if (Events.ON_CHANGE.equals(event.getName()))
		{
			if (StringUtils.equals(Localization.getLocalizedString("type.marketplace.url.name"), context.getEditorLabel()))
			{
				final Object[] changedInfo =
				{ Localization.getLocalizedString("type.marketplace.url.name") };
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.warning", changedInfo),
						NotificationEvent.Type.WARNING, "");
			}
			else if (StringUtils.equals(Localization.getLocalizedString("type.marketplaceseller.marketplacestores.name"),
					context.getEditorLabel()))
			{
				final Object[] changedInfo =
				{ Localization.getLocalizedString("type.marketplaceseller.marketplacestores.name") };
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.warning", changedInfo),
						NotificationEvent.Type.WARNING, "");
			}
			else
			{
				NotificationUtils.notifyUserVia(Labels.getLabel("marketplace.order.authorization.warning.default"),
						NotificationEvent.Type.WARNING, "");
			}
		}
	}
}