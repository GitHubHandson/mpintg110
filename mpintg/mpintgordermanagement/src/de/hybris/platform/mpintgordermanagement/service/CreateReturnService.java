package de.hybris.platform.mpintgordermanagement.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.CreateReturnEvent;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;


public class CreateReturnService
{

	private static final Logger LOG = LoggerFactory.getLogger(CreateReturnService.class);
	private Map<AbstractOrderEntryModel, Long> returnableOrderEntries;

	@Autowired
	private ReturnService returnService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private EventService eventService;

	private Map<RefundEntryModel, Long> refundEntries = new HashMap<RefundEntryModel, Long>();

	public boolean executeReturnProcess(OrderEntryModel entry, RefundReason reason)
	{
		boolean flag = false;
		try
		{

			int entryid = entry.getEntryNumber();
			OrderModel order = entry.getOrder();
			setReturnableOrderEntries(returnService.getAllReturnableEntries(order));

			createReturnRequest(order, entryid);

			if (refundEntries != null && !refundEntries.isEmpty())
			{
				final ReturnRequestModel returnRequest = returnService.createReturnRequest(order);
				refundEntries.forEach((refundEntry, quantityToReturn) -> {
					returnService.createRefund(returnRequest, refundEntry.getOrderEntry(), refundEntry.getNotes(),
							getReturnableOrderEntries().get(entry), ReturnAction.IMMEDIATE, reason);
				});

				refundEntries.clear();


				final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
				createReturnEvent.setReturnRequest(returnRequest);
				eventService.publishEvent(createReturnEvent);
				flag = true;
			}
			else
			{
				LOG.warn("There are not returnable enteries in the order: " + order.getCode());
			}

		}
		catch (final Exception e)
		{
			LOG.error("create return fail", e);
		}
		return flag;
	}


	public boolean executeReturnProcess(OrderEntryModel entry, RefundReason reason, Long returnQuantity)
	{
		boolean flag = false;
		try
		{

			int entryid = entry.getEntryNumber();
			OrderModel order = entry.getOrder();
			setReturnableOrderEntries(returnService.getAllReturnableEntries(order));

			createReturnRequest(order, entryid, returnQuantity);

			if (refundEntries != null && !refundEntries.isEmpty())
			{
				final ReturnRequestModel returnRequest = returnService.createReturnRequest(order);
				refundEntries.forEach((refundEntry, quantityToReturn) -> {
					returnService.createRefund(returnRequest, refundEntry.getOrderEntry(), refundEntry.getNotes(), returnQuantity,
							ReturnAction.IMMEDIATE, reason);
				});

				refundEntries.clear();


				final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
				createReturnEvent.setReturnRequest(returnRequest);
				eventService.publishEvent(createReturnEvent);
				flag = true;
			}
			else
			{
				LOG.warn("There are not returnable enteries in the order: " + order.getCode());
			}

		}
		catch (final Exception e)
		{
			LOG.error("create return fail", e);
		}
		return flag;
	}

	protected void createReturnRequest(OrderModel orderModel, int entryid, Long quantity)
	{
		ReturnRequestModel refundRequest;
		List<AbstractOrderEntryModel> orderEntries = orderModel.getEntries();
		Map<AbstractOrderEntryModel, Long> returnableOrderEntries = returnService.getAllReturnableEntries(orderModel);

		if (orderEntries.size() != 0)
		{
			refundRequest = modelService.create(ReturnRequestModel.class);
			orderEntries.stream().filter(

					orderEntry -> {
						if (returnableOrderEntries != null && !returnableOrderEntries.isEmpty())
						{
							return returnableOrderEntries.get(orderEntry) != null && orderEntry.getEntryNumber() == entryid;
						}
						else
						{
							return false;
						}
					}).forEach(orderEntry -> {
						final RefundEntryModel refundEntry = new RefundEntryModel();
						refundEntry.setReturnRequest(refundRequest);
						refundEntry.setOrderEntry(orderEntry);
						refundEntry.setNotes("Create Return");
						refundEntry.setStatus(ReturnStatus.WAIT);
						refundEntry.setExpectedQuantity(quantity);
						refundEntries.put(refundEntry, quantity);
					});
		}
	}

	protected void createReturnRequest(OrderModel orderModel, int entryid)
	{
		ReturnRequestModel refundRequest;
		List<AbstractOrderEntryModel> orderEntries = orderModel.getEntries();
		Map<AbstractOrderEntryModel, Long> returnableOrderEntries = returnService.getAllReturnableEntries(orderModel);

		if (orderEntries.size() != 0)
		{
			refundRequest = modelService.create(ReturnRequestModel.class);
			orderEntries.stream().filter(

					orderEntry -> {
						if (returnableOrderEntries != null && !returnableOrderEntries.isEmpty())
						{
							return returnableOrderEntries.get(orderEntry) != null && orderEntry.getEntryNumber() == entryid;
						}
						else
						{
							return false;
						}
					}).forEach(orderEntry -> {
						final RefundEntryModel refundEntry = new RefundEntryModel();
						refundEntry.setReturnRequest(refundRequest);
						refundEntry.setOrderEntry(orderEntry);
						refundEntry.setNotes("Create Return");
						refundEntry.setStatus(ReturnStatus.WAIT);
						refundEntry.setExpectedQuantity(returnableOrderEntries.get(orderEntry));
						refundEntries.put(refundEntry, returnableOrderEntries.get(orderEntry));
					});
		}
	}


	public boolean isReturnable(OrderEntryModel entry)
	{
		int entryid = entry.getEntryNumber();
		OrderModel orderModel = entry.getOrder();
		List<AbstractOrderEntryModel> orderEntries = orderModel.getEntries();
		Map<AbstractOrderEntryModel, Long> returnableOrderEntries = returnService.getAllReturnableEntries(orderModel);

		for (AbstractOrderEntryModel orderEntry : orderEntries)
		{
			if (returnableOrderEntries.get(orderEntry) != null && orderEntry.getEntryNumber() == entryid)
			{
				return true;
			}
		}
		return false;
	}


	protected Map<AbstractOrderEntryModel, Long> getReturnableOrderEntries()
	{
		return returnableOrderEntries;
	}

	public void setReturnableOrderEntries(final Map<AbstractOrderEntryModel, Long> returnableOrderEntries)
	{
		this.returnableOrderEntries = returnableOrderEntries;
	}

}
