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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
//import org.zkoss.zul.api.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Comboitem;

//import com.sap.acc.cockpit.enums.ListingStatusEnum;
import de.hybris.platform.mpintgproductcockpit.enums.ListingStatusEnum;
import de.hybris.platform.cockpit.components.sectionpanel.RowlayoutSectionPanelModel;
import de.hybris.platform.cockpit.components.sectionpanel.Section;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanel;
import de.hybris.platform.cockpit.components.sectionpanel.SectionRenderer;
import de.hybris.platform.cockpit.events.CockpitEvent;
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
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.session.impl.DefaultEditorSectionPanelModel;
import de.hybris.platform.cockpit.session.impl.EditorArea;
import de.hybris.platform.cockpit.session.impl.EditorPropertyRow;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mpintgproductcockpit.cmscockpit.services.impl.ProductStockServiceImpl;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;


/**
 * 
 */
public class TmallAttributeSection extends DefaultEditorSectionConfiguration implements CustomEditorSectionConfiguration
{

	private static final Logger LOG = Logger.getLogger(LastChangesSectionConfiguration.class);

	protected final SectionRenderer renderer = new MyCustomSectionRenderer();

	private ObjectType objectType;
	private TypedObject currentObject;
	private ModelService modelService;
	private ProductStockServiceImpl productStockServiceImpl;

	public ProductStockServiceImpl getProductStockServiceImpl()
	{
		return productStockServiceImpl;
	}

	public void setProductStockServiceImpl(ProductStockServiceImpl productStockServiceImpl)
	{
		this.productStockServiceImpl = productStockServiceImpl;
	}

	@Override
	public void allInitialized(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{
		// do nothing

	}

	protected UIAccessRightService getUIAccessRightService()
	{
		return UISessionUtils.getCurrentSession().getUiAccessRightService();
	}

	@Override
	public List<EditorSectionConfiguration> getAdditionalSections()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public SectionRenderer getCustomRenderer()
	{
		return this.renderer;
	}

	@Override
	public void initialize(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{
		this.objectType = objectType;
		this.currentObject = object;
	}

	@Override
	public void loadValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		// do nothing
	}

	@Override
	public void saveValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		// do nothing
	}

	private void renderSection(final SectionPanel panel, final Component parent, final ProductModel pModel,
			EditorRowConfiguration quantityRowConfiguration, EditorRowConfiguration startDateRowConfiguration, EditorArea editorArea)
	{
		final Div container1 = new Div();
		parent.appendChild(container1);
		container1.setStyle("text-align: left; height:21px;line-height:13.2px");
		final Label listinglabel = new Label("Listing Status: ");
		listinglabel.setContext("Listing Status");
		listinglabel.setParent(container1);

		final Combobox combo = new Combobox();
		combo.setWidth("100px");

		combo.appendItem("Listing");
		combo.appendItem("DeListing");

		if (pModel.getListingStatus() == ListingStatusEnum.LISTING)
		{
			combo.setSelectedIndex(0);
		}
		else
		{
			quantityRowConfiguration.setVisible(false);
			startDateRowConfiguration.setEditable(false);
			combo.setSelectedIndex(1);
		}

		combo.setParent(container1);
		combo.addEventListener(Events.ON_CHANGE, new EventListener()
		{

			public void onEvent(final org.zkoss.zk.ui.event.Event arg0) throws Exception

			{
				if (combo.getSelectedIndex() == 1)
				{
					quantityRowConfiguration.setVisible(false);
					startDateRowConfiguration.setEditable(false);
					pModel.setListingStatus(ListingStatusEnum.DELISTING);
				}
				else if (combo.getSelectedIndex() == 0)
				{
					pModel.setListingStatus(ListingStatusEnum.LISTING);
				}

				if (modelService == null)
				{
					modelService = (ModelService) SpringUtil.getBean("modelService");
				}
				modelService.save(pModel);

				editorArea.getEditorAreaController().resetSectionPanelModel();
			}
		});
	}

	private class MyCustomSectionRenderer implements SectionRenderer
	{

		final String START_DATE = "Product.tmallOperationStartDate";
		final String QUANTITY = "Product.quantity";
		final String INSTOCK = "Product.inStockQuanity";
		final String TMALL_PRODUCT_ID = "Product.tmallProductId";
		final String TMALL_CATEGORY_ID = "Product.tmallCategoryId";
		EditorRowConfiguration quantityRowConfiguration;
		EditorRowConfiguration startDateRowConfiguration;

		@Override
		public void render(final SectionPanel panel, final Component parent, final Component captionComponent, final Section section)
		{

			DefaultEditorSectionPanelModel panelModel = (DefaultEditorSectionPanelModel) panel.getModel();
			EditorArea editorArea = (EditorArea) panelModel.getEditorArea();
			ProductModel pModel = (ProductModel) panelModel.getEditorArea().getCurrentObject().getObject();

			objectType = editorArea.getCurrentObjectType();
			currentObject = editorArea.getCurrentObject();
			final Set<PropertyDescriptor> allDescriptors = getAllPropertyDescriptors();

			String productPK = pModel.getPk().toString();

			/*
			 * if (productStockServiceImpl == null) { productStockServiceImpl = (ProductStockServiceImpl)
			 * SpringUtil.getBean("productStockServiceImpl"); }
			 */
			BaseStoreModel baseStore = productStockServiceImpl.getBaseStorebyPK(productPK);
			if (baseStore != null)
			{
				int stock = productStockServiceImpl.getAvailableStockbyPK(baseStore, productPK);
				pModel.setInStockQuanity(stock);
			}

			if (pModel.getListingStatus() == null)
			{
				pModel.setListingStatus(ListingStatusEnum.DELISTING);
			}
			if (modelService == null)
			{
				modelService = (ModelService) SpringUtil.getBean("modelService");
				modelService.save(pModel);
			}

			for (final PropertyDescriptor descriptor : allDescriptors)
			{
				if (descriptor.getQualifier().equalsIgnoreCase(QUANTITY))
				{
					Div div1 = new Div();
					parent.appendChild(div1);
					quantityRowConfiguration = new PropertyEditorRowConfiguration(descriptor, true, true);
				}
				if (descriptor.getQualifier().equalsIgnoreCase(START_DATE))
				{
					Div div1 = new Div();
					parent.appendChild(div1);
					startDateRowConfiguration = new PropertyEditorRowConfiguration(descriptor, true, true);
				}
			}
			parent.getChildren().clear();
			if (panel.getModel() instanceof DefaultEditorSectionPanelModel)
			{
				renderSection(panel, parent, pModel, quantityRowConfiguration, startDateRowConfiguration, editorArea);
			}

			if (panel.getModel() instanceof RowlayoutSectionPanelModel)
			{

				for (final PropertyDescriptor descriptor : allDescriptors)
				{

					if (descriptor.getQualifier().equalsIgnoreCase(START_DATE))
					{
						Div div1 = new Div();
						parent.appendChild(div1);
						EditorPropertyRow editorPropertyRow = new EditorPropertyRow(startDateRowConfiguration);
						panel.createRowComponent(section, editorPropertyRow, div1, parent);
						UITools.applyLazyload(div1);
					}
					if (descriptor.getQualifier().equalsIgnoreCase(QUANTITY))
					{
						if (quantityRowConfiguration.isVisible() == true)
						{
							Div div1 = new Div();
							parent.appendChild(div1);
							EditorPropertyRow editorPropertyRow = new EditorPropertyRow(quantityRowConfiguration);
							panel.createRowComponent(section, editorPropertyRow, div1, parent);
							UITools.applyLazyload(div1);
						}
					}

					if (descriptor.getQualifier().equalsIgnoreCase(INSTOCK))
					{
						Div div1 = new Div();
						parent.appendChild(div1);
						EditorRowConfiguration editorRowConfiguration = new PropertyEditorRowConfiguration(descriptor, true, false);
						EditorPropertyRow editorPropertyRow = new EditorPropertyRow(editorRowConfiguration);
						panel.createRowComponent(section, editorPropertyRow, div1, parent);
						UITools.applyLazyload(div1);
					}

					if (descriptor.getQualifier().equalsIgnoreCase(TMALL_PRODUCT_ID))
					{
						Div div1 = new Div();
						parent.appendChild(div1);
						EditorRowConfiguration editorRowConfiguration = new PropertyEditorRowConfiguration(descriptor, true, false);
						EditorPropertyRow editorPropertyRow = new EditorPropertyRow(editorRowConfiguration);
						panel.createRowComponent(section, editorPropertyRow, div1, parent);
						UITools.applyLazyload(div1);
					}

					if (descriptor.getQualifier().equalsIgnoreCase(TMALL_CATEGORY_ID))
					{
						Div div1 = new Div();
						parent.appendChild(div1);
						EditorRowConfiguration editorRowConfiguration = new PropertyEditorRowConfiguration(descriptor, true, false);
						EditorPropertyRow editorPropertyRow = new EditorPropertyRow(editorRowConfiguration);
						panel.createRowComponent(section, editorPropertyRow, div1, parent);
						UITools.applyLazyload(div1);
					}
				}

			}
		}

		protected Set<PropertyDescriptor> getAllPropertyDescriptors()
		{
			final Set<PropertyDescriptor> all = new HashSet<PropertyDescriptor>();

			BaseType baseType = null;
			if (currentObject != null)
			{
				baseType = currentObject.getType();
			}
			else if (objectType != null)
			{
				if (objectType instanceof BaseType)
				{
					baseType = (BaseType) objectType;
				}
				else if (objectType instanceof ObjectTemplate)
				{
					baseType = ((ObjectTemplate) objectType).getBaseType();
				}
			}

			if (baseType != null)
			{
				all.addAll(baseType.getPropertyDescriptors());
			}
			if (currentObject != null)
			{
				for (final ExtendedType extType : currentObject.getExtendedTypes())
				{
					all.addAll(extType.getPropertyDescriptors());
				}
			}
			return all;
		}
	}
}
