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
package de.hybris.platform.mpintgproductcockpit.editorAera;

import de.hybris.platform.cockpit.components.sectionpanel.RowlayoutSectionPanelModel;
import de.hybris.platform.cockpit.components.sectionpanel.Section;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanel;
import de.hybris.platform.cockpit.components.sectionpanel.SectionRenderer;
import de.hybris.platform.cockpit.components.sectionpanel.SectionRow;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.ExtendedType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.CustomEditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.impl.DefaultEditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.impl.LastChangesSectionConfiguration;
import de.hybris.platform.cockpit.services.config.impl.PropertyEditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.impl.UnassignedEditorSectionConfiguration;
import de.hybris.platform.cockpit.services.security.UIAccessRightService;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueService;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.session.impl.CustomEditorSection;
import de.hybris.platform.cockpit.session.impl.DefaultEditorSectionPanelModel;
import de.hybris.platform.cockpit.session.impl.EditorArea;
import de.hybris.platform.cockpit.session.impl.EditorPropertyRow;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mpintgproductcockpit.productcockpit.editor.ExternalTaxesSectionConfiguration;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.RadioButton;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Radio;


/**
 * 
 */
public class MyCustomSection extends DefaultEditorSectionConfiguration implements CustomEditorSectionConfiguration
{

	private static final Logger LOG = Logger.getLogger(LastChangesSectionConfiguration.class);
	private ModelService modelService;

	private ValueService valueService;

	/**
	 * @return the valueService
	 */
	public ValueService getValueService()
	{
		if (valueService == null)
		{
			valueService = (ValueService) SpringUtil.getBean("valueService");
		}
		return valueService;
	}


	private final static String CATALOG_SECTION_CONTAINER_CLASS = "catalog_section_container";

	protected final SectionRenderer renderer = new MyCustomSectionRenderer();

	private ObjectType objectType;
	private TypedObject currentObject;

	@Override
	public void allInitialized(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{
		// do nothing
		//		final List<PropertyDescriptor> allDescriptors = getAllPropertyDescriptors();
		//		for (final EditorSectionConfiguration secConf : config.getSections())
		//		{
		//			if (!(secConf instanceof UnassignedEditorSectionConfiguration))
		//			{
		//				for (final EditorRowConfiguration rowConf : secConf.getSectionRows())
		//				{
		//					allDescriptors.remove(rowConf.getPropertyDescriptor());
		//				}
		//			}
		//		}
		//
		//		final List<EditorRowConfiguration> rows = new ArrayList<EditorRowConfiguration>();
		//		BaseType thisBaseType = null;
		//		if (currentObject != null)
		//		{
		//			thisBaseType = currentObject.getType();
		//		}
		//		else if (objectType != null)
		//		{
		//			if (objectType instanceof ObjectTemplate)
		//			{
		//				thisBaseType = ((ObjectTemplate) objectType).getBaseType();
		//			}
		//			else
		//			{
		//				thisBaseType = (BaseType) objectType;
		//			}
		//		}
		//		rows.add(new PropertyEditorRowConfiguration(allDescriptors.get(0), true, true));
		//		for (final PropertyDescriptor pd : allDescriptors)
		//		{
		//			final boolean readable = getUIAccessRightService().isReadable(thisBaseType, pd, this.currentObject == null);
		//			if (readable)
		//			{
		//				rows.add(new PropertyEditorRowConfiguration(pd, true, true));
		//			}
		//		}
		//		setSectionRows(rows);
	}

	protected UIAccessRightService getUIAccessRightService()
	{
		return UISessionUtils.getCurrentSession().getUiAccessRightService();
	}

	@Override
	public List<EditorSectionConfiguration> getAdditionalSections()
	{

		// do nothing
		final List<PropertyDescriptor> allDescriptors = getAllPropertyDescriptors();
		final List<EditorRowConfiguration> rows = new ArrayList<EditorRowConfiguration>();
		BaseType thisBaseType = null;
		if (currentObject != null)
		{
			thisBaseType = currentObject.getType();
		}
		else if (objectType != null)
		{
			if (objectType instanceof ObjectTemplate)
			{
				thisBaseType = ((ObjectTemplate) objectType).getBaseType();
			}
			else
			{
				thisBaseType = (BaseType) objectType;
			}
		}
		rows.add(new PropertyEditorRowConfiguration(allDescriptors.get(0), true, true));

		EditorSectionConfiguration editorSectionConfiguration = new DefaultEditorSectionConfiguration();
		editorSectionConfiguration.setSectionRows(rows);

		List<EditorSectionConfiguration> EditorSectionConfiguration = new ArrayList<EditorSectionConfiguration>();
		EditorSectionConfiguration.add(editorSectionConfiguration);
		return EditorSectionConfiguration;
		//		return Collections.EMPTY_LIST;
	}

	@Override
	public SectionRenderer getCustomRenderer()
	{
		return this.renderer;
		//		return null;
	}



	@Override
	public void initialize(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{
		this.objectType = objectType;
		this.currentObject = object;

		// do nothing
		final List<PropertyDescriptor> allDescriptors = getAllPropertyDescriptors();
		final List<EditorRowConfiguration> rows = new ArrayList<EditorRowConfiguration>();
		BaseType thisBaseType = null;
		if (currentObject != null)
		{
			thisBaseType = currentObject.getType();
		}
		else if (objectType != null)
		{
			if (objectType instanceof ObjectTemplate)
			{
				thisBaseType = ((ObjectTemplate) objectType).getBaseType();
			}
			else
			{
				thisBaseType = (BaseType) objectType;
			}
		}
		rows.add(new PropertyEditorRowConfiguration(allDescriptors.get(0), true, true));
		setSectionRows(rows);
	}

	@Override
	public void loadValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		//do nothing
	}

	@Override
	public void saveValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		// do nothing
	}


	protected List<PropertyDescriptor> getAllPropertyDescriptors()
	{
		final List<PropertyDescriptor> all = new ArrayList<PropertyDescriptor>();

		BaseType baseType = null;
		if (this.currentObject != null)
		{
			baseType = this.currentObject.getType();
		}
		else if (this.objectType != null)
		{
			if (this.objectType instanceof BaseType)
			{
				baseType = (BaseType) this.objectType;
			}
			else if (this.objectType instanceof ObjectTemplate)
			{
				baseType = ((ObjectTemplate) this.objectType).getBaseType();
			}
		}

		if (baseType != null)
		{
			all.addAll(baseType.getPropertyDescriptors());
		}
		if (this.currentObject != null)
		{
			for (final ExtendedType extType : this.currentObject.getExtendedTypes())
			{
				all.addAll(extType.getPropertyDescriptors());
			}
		}
		return all;
	}

	private void renderSection(final SectionPanel panel, final Component parent)
	{

		final Div container = new Div();
		container.setSclass(CATALOG_SECTION_CONTAINER_CLASS);
		parent.appendChild(container);
		Radio audioListing = new Radio();
		audioListing.setContext("Listing");
		audioListing.setLabel("Listing label");
		audioListing.setWidth("50%");
		audioListing.setParent(container);

		Radio audioDelisting = new Radio();
		audioDelisting.setContext("Delisting");
		audioDelisting.setLeft("left");
		audioDelisting.setLabel("Delisting label");
		audioDelisting.setWidth("50%");
		audioDelisting.setParent(container);

		final Div container2 = new Div();
		parent.appendChild(container2);
		final Combobox combo = new Combobox();
		combo.setWidth("100px");
		combo.appendItemApi("Test1");
		combo.appendItemApi("Test2");
		combo.appendItemApi("Test3");
		combo.setParent(container2);

		final Button n2 = new Button("Button 1");
		n2.setWidth("150px");
		n2.setHeight("30px");
		n2.setParent(container2);

		final Button n1 = new Button("Button 2");
		n1.setWidth("150px");
		n1.setHeight("30px");
		n1.setParent(container2);

		n1.addEventListener(Events.ON_CLICK, new EventListener()
		{
			public void onEvent(final org.zkoss.zk.ui.event.Event arg0) throws Exception

			{
				final Messagebox ms = new Messagebox();
				ms.show("Test !!");
				if (panel.getModel() instanceof DefaultEditorSectionPanelModel)
				{
					DefaultEditorSectionPanelModel panelModel = (DefaultEditorSectionPanelModel) panel.getModel();
					ProductModel pModel = (ProductModel) panelModel.getEditorArea().getCurrentObject().getObject();
					pModel.setEan("test");

					if (modelService == null)
					{
						modelService = (ModelService) SpringUtil.getBean("modelService");
						modelService.save(pModel);
					}
				}
			}
		});


	}


	private class MyCustomSectionRenderer implements SectionRenderer
	{
		@Override
		public void render(final SectionPanel panel, final Component parent, final Component captionComponent, final Section section)
		{
			parent.getChildren().clear();
			if (panel.getModel() instanceof DefaultEditorSectionPanelModel)
			{
				renderSection(panel, parent);
			}


			//			if (panel.getModel() instanceof RowlayoutSectionPanelModel)
			//			{
			//				RowlayoutSectionPanelModel model = (RowlayoutSectionPanelModel) panel.getModel();
			//
			//				Div div = new Div();
			//				parent.appendChild(div);
			//				
			//				for (SectionRow sectionRow : model.getRows(section))
			//				{
			//					HtmlBasedComponent rowDiv = panel.createRowComponent(section, sectionRow, div, parent);
			//					rowDiv.setSclass("sectionRowComponent");
			//					rowDiv.setVisible(sectionRow.isVisible());
			//				}
			//
			//				UITools.applyLazyload(div);
			//				
			//				/////////////////
			//			}

			DefaultEditorSectionPanelModel panelModel = (DefaultEditorSectionPanelModel) panel.getModel();
			EditorArea editorArea = (EditorArea) panelModel.getEditorArea();
			ProductModel pModel = (ProductModel) panelModel.getEditorArea().getCurrentObject().getObject();


			objectType = editorArea.getCurrentObjectType();
			currentObject = editorArea.getCurrentObject();

			Div div3 = new Div();
			parent.appendChild(div3);
			final List<PropertyDescriptor> allDescriptors = getAllPropertyDescriptors();
			for (final PropertyDescriptor pd : allDescriptors)
			{
				if (pd.getQualifier().equalsIgnoreCase("Product.onlineDate")
						|| pd.getQualifier().equalsIgnoreCase("Product.offlineDate")
						|| pd.getQualifier().equalsIgnoreCase("Product.ean"))
				{
					EditorRowConfiguration editorRowConfiguration = new PropertyEditorRowConfiguration(pd, true, true);
					EditorPropertyRow editorPropertyRow = new EditorPropertyRow(editorRowConfiguration);
					panel.createRowComponent(section, editorPropertyRow, div3, parent);
				}
			}
			UITools.applyLazyload(div3);


		}
	}

}
