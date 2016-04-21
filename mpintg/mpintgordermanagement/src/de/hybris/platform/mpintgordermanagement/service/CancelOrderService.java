package de.hybris.platform.mpintgordermanagement.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.warehousing.cancellation.OrderCancellationService;
import de.hybris.platform.warehousing.data.cancellation.CancellationEntry;
import de.hybris.platform.warehousing.process.impl.DefaultOrderProcessService;


public class CancelOrderService
{
	private static final Logger LOG = LoggerFactory.getLogger(CancelOrderService.class);

	@Autowired
	private DefaultOrderProcessService orderProcessService;

	@Autowired
	private OrderCancellationService orderCancellationService;

	public boolean cancelOrder(OrderEntryModel orderEntry, long cancelQuantity, CancelReason reason)
	{
		boolean flag = false;
		OrderModel order = orderEntry.getOrder();
		if (order != null)
		{
			try
			{
				LOG.debug("Canceling the order " + order.getCode() + " entry " + orderEntry.getEntryNumber() + " quantity "
						+ cancelQuantity + " with reason " + reason.getCode());
				orderProcessService.triggerChoiceEvent(order, "OrderActionEvent", "cancelOrder");
				orderCancellationService.cancelOrder(order, createCancellationEntries(orderEntry, cancelQuantity, reason));

				orderProcessService.triggerSimpleEvent(order, "CancellationCompletionEvent");
				flag = true;
			}
			catch (Exception e)
			{
				LOG.error("cancelling order fail", e);
			}
		}
		else
		{
			LOG.warn("Could not find order for the order entry " + orderEntry.getEntryNumber());
		}
		return flag;
	}

	public boolean cancelEntireOrderEntry(OrderEntryModel orderEntry, CancelReason reason)
	{
		boolean flag = false;
		OrderModel order = orderEntry.getOrder();
		if (order != null)
		{
			try
			{
				LOG.debug("Canceling the order " + order.getCode() + " entry " + orderEntry.getEntryNumber() + " quantity "
						+ orderEntry.getQuantity() + " with reason " + reason.getCode());
				orderProcessService.triggerChoiceEvent(order, "OrderActionEvent", "cancelOrder");
				orderCancellationService.cancelOrder(order, createCancellationEntries(orderEntry, orderEntry.getQuantity(), reason));

				orderProcessService.triggerSimpleEvent(order, "CancellationCompletionEvent");
				flag = true;
			}
			catch (Exception e)
			{
				LOG.error("cancelling order fail", e);
			}
		}
		else
		{
			LOG.warn("Could not find order for the order entry " + orderEntry.getEntryNumber());
		}
		return flag;
	}

	protected Collection<CancellationEntry> createCancellationEntries(final AbstractOrderEntryModel orderEntry,
			long cancelQuantity, CancelReason reason)
	{
		final CancellationEntry entry = new CancellationEntry();
		entry.setNotes("cancel");
		entry.setReason(reason.getCode());
		entry.setQuantity(cancelQuantity);
		entry.setOrderEntry(orderEntry);
		return Arrays.asList(entry);
	}


	public boolean isOrderCancelable(OrderModel order)
	{

		if (order != null)
		{
			LOG.warn("Order is empty");
			return false;
		}

		// An order is not cancellable when at least an item has already been shipped or when there is no quantity pending
		if (Objects.isNull(order) || Objects.isNull(order.getEntries())
				|| order.getEntries().stream().filter(entry -> Objects.nonNull(entry.getQuantityShipped()))
						.mapToLong(AbstractOrderEntryModel::getQuantityShipped).sum() > 0
				|| order.getEntries().stream().filter(entry -> Objects.nonNull(entry.getQuantityPending()))
						.mapToLong(AbstractOrderEntryModel::getQuantityPending).sum() == 0)
		{
			return false;
		}
		return true;
	}

	public boolean isEntryCancelable(AbstractOrderEntryModel entry)
	{
		if (entry == null)
		{
			LOG.warn("Order is empty");
			return false;
		}

		if (entry.getQuantityShipped() > 0 || entry.getQuantityPending() == 0)
		{
			return false;
		}
		return true;
	}


}
