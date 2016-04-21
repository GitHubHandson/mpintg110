package de.hybris.platform.mpintgordermanagement.service;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.warehousing.data.shipping.ShippedEntry;
import de.hybris.platform.warehousing.process.BusinessProcessException;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.shipping.ShippingService;
import de.hybris.platform.warehousing.shipping.impl.ShippingException;


public class ConfirmShipService
{

	private static final Logger LOG = LoggerFactory.getLogger(ConfirmShipService.class);

	protected static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	protected static final String SHIP_CONSIGNMENT_CHOICE = "confirmShipConsignment";

	@Autowired
	private WarehousingBusinessProcessService<ConsignmentModel> warehousingBusinessProcessService;

	@Autowired
	private ShippingService shippingService;

	public boolean confirmShip(ConsignmentEntryModel consignmentEntry)
	{
		boolean flag = false;
		ConsignmentModel consignment = consignmentEntry.getConsignment();
		if (canPerform(consignment))
		{
			final Collection<ShippedEntry> shippedEntries = shippingService.createShippedEntries(consignment);
			try
			{
				LOG.debug("Confirming shippment of the consigment " + consignment.getCode());
				warehousingBusinessProcessService.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME,
						SHIP_CONSIGNMENT_CHOICE);
				shippingService.confirmShippedConsignmentEntries(shippedEntries);
				flag = true;
			}
			catch (final BusinessProcessException | ShippingException e)
			{
				LOG.error("There is error during confirming shippment", e);
			}
		}
		else
		{
			LOG.warn("The consigment cannot be confirmed shippment, please check!");
		}
		return flag;
	}

	public boolean canPerform(final ConsignmentModel consignment)
	{
		// A consignment is not shippable when it is a pickup order or when
		// there is no quantity pending
		if (Objects.isNull(consignment) || (consignment.getDeliveryMode() instanceof PickUpDeliveryModeModel)
				|| Objects.isNull(consignment.getConsignmentEntries())
				|| consignment.getConsignmentEntries().stream().filter(entry -> Objects.nonNull(entry.getQuantityPending()))
						.mapToLong(ConsignmentEntryModel::getQuantityPending).sum() == 0)
		{
			return false;
		}
		return true;
	}

}
