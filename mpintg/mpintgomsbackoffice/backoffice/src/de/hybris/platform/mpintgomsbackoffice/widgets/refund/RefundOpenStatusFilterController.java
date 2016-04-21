/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.mpintgomsbackoffice.widgets.refund;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.mpintgordermanagement.enums.RefundAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.hybris.backoffice.model.TmallRefundRequestModel;
import com.hybris.backoffice.widgets.advancedsearch.AbstractInitAdvancedSearchAdapter;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


/**
 * Add conditions to the advance search widget to filter for an RefundRequest having open status.<br/>
 *
 * Open statuses: AWAITING_APPROVAL
 */
public class RefundOpenStatusFilterController extends AbstractInitAdvancedSearchAdapter
{

	private static final long serialVersionUID = 1L;

	public static final String NAVIGATION_NODE_ID = "ordermanagementbackoffice.typenode.request.open";

	private AdvancedSearchData searchData;

	@WireVariable
	private EnumerationService enumerationService;

	@Override
	public void addSearchDataConditions(final AdvancedSearchData searchData)
	{
		if (searchData != null)
		{
			this.searchData = searchData;
			if (CollectionUtils.isNotEmpty(searchData.getConditions(TmallRefundRequestModel.REFUNDACTION)))
			{
				// clear existing delivery mode search data
				searchData.getConditions(TmallRefundRequestModel.REFUNDACTION).clear();
			}

			final FieldType statusFieldType = new FieldType();
			statusFieldType.setDisabled(Boolean.FALSE);
			statusFieldType.setSelected(Boolean.TRUE);
			statusFieldType.setName(TmallRefundRequestModel.REFUNDACTION);

			final List<RefundAction> openStatuses = getEnumerationService().getEnumerationValues(RefundAction.class);
			final List<SearchConditionData> statusConditionsList = new ArrayList<>();
			openStatuses
					.stream()
					.filter(
							status -> !status.equals(RefundAction.COMPLETED) && !status.equals(RefundAction.CLOSED_BY_CUSTOMER)
									&& !status.equals(RefundAction.REJECTED_BY_SELLER))
					.forEach(
							status -> statusConditionsList.add(new SearchConditionData(statusFieldType, status,
									ValueComparisonOperator.EQUALS)));

			final SearchConditionDataList statusSearchConditionsList = SearchConditionDataList.or(statusConditionsList);

			searchData.addConditionList(ValueComparisonOperator.OR, Arrays.asList(statusSearchConditionsList));
		}
	}

	@Override
	public String getNavigationNodeId()
	{
		return NAVIGATION_NODE_ID;
	}

	@Override
	public String getTypeCode()
	{
		return TmallRefundRequestModel._TYPECODE;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	public AdvancedSearchData getAdvancedSearchData()
	{
		return searchData;
	}

}
